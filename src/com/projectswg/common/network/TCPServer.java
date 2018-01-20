/***********************************************************************************
 * Copyright (c) 2017 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of Holocore.                                                  *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * Holocore is free software: you can redistribute it and/or modify                *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * Holocore is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with Holocore.  If not, see <http://www.gnu.org/licenses/>.               *
 *                                                                                 *
 ***********************************************************************************/
package com.projectswg.common.network;

import com.projectswg.common.concurrency.PswgBasicThread;
import com.projectswg.common.concurrency.PswgThreadPool;
import com.projectswg.common.debug.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class TCPServer<T extends TCPServer.TCPSession> {
	
	private final PswgThreadPool callbackThread;
	private final Map<SocketChannel, T> channels;
	private final Map<Long, T> sessionIdToChannel;
	
	private final PswgBasicThread listener;
	private final AtomicBoolean running;
	private final InetSocketAddress addr;
	private final Function<SocketChannel, T> sessionCreator;
	private ServerSocketChannel channel;
	
	private final ByteBuffer buffer;
	private final ByteArrayOutputStream bufferStream;
	private final WritableByteChannel byteBufferChannel;
	
	public TCPServer(int port, int bufferSize, Function<SocketChannel, T> sessionCreator) {
		this(new InetSocketAddress((InetAddress) null, port), bufferSize, sessionCreator);
	}
	
	public TCPServer(InetSocketAddress addr, int bufferSize, Function<SocketChannel, T> sessionCreator) {
		this.callbackThread = new PswgThreadPool(false, 1, "tcpserver-" + addr.getPort());
		this.channels = new ConcurrentHashMap<>();
		this.sessionIdToChannel = new ConcurrentHashMap<>();
		this.listener = new PswgBasicThread("tcpserver-listener-" + addr.getPort(), this::runListener);
		this.running = new AtomicBoolean(false);
		this.addr = addr;
		this.channel = null;
		this.sessionCreator = sessionCreator;
		this.buffer = ByteBuffer.allocateDirect(bufferSize);
		this.bufferStream = new ByteArrayOutputStream(bufferSize);
		this.byteBufferChannel = Channels.newChannel(bufferStream);
	}
	
	public int getPort() {
		return channel.socket().getLocalPort();
	}
	
	public void bind() throws IOException {
		assert !running.get() : "TCPServer is already running";
		if (running.getAndSet(true))
			return;
		callbackThread.start();
		channel = ServerSocketChannel.open();
		channel.bind(addr, 50);
		channel.configureBlocking(false);
		listener.start();
	}
	
	public void disconnect(long sessionId) {
		T session = sessionIdToChannel.remove(sessionId);
		if (session == null) {
			Log.w("TCPServer - unknown session id in disconnect: %d", sessionId);
			return;
		}
		
		disconnect(session.getChannel());
	}
	
	public void disconnect(T session) {
		disconnect(Objects.requireNonNull(session, "session").getChannel());
	}
	
	public void disconnect(SocketChannel sc) {
		T session = channels.remove(sc);
		if (session == null) {
			Log.w("TCPServer - unknown channel in disconnect: %d", sc);
			return;
		}
		sessionIdToChannel.remove(session.getSessionId());
		
		session.close();
		callbackThread.execute(session::onDisconnected);
	}
	
	public T getSession(long sessionId) {
		return sessionIdToChannel.get(sessionId);
	}
	
	public T getSession(SocketChannel sc) {
		return channels.get(sc);
	}
	
	public void close() {
		assert running.get() : "TCPServer isn't running";
		if (!running.getAndSet(false))
			return;
		callbackThread.stop(false);
		listener.stop(true);
		safeClose(channel);
	}
	
	private void runListener() {
		try (Selector selector = Selector.open()) {
			channel.register(selector, SelectionKey.OP_ACCEPT);
			while (running.get()) {
				selector.select();
				accept(selector);
				selector.selectedKeys().forEach(this::read);
			}
		} catch (IOException e) {
			Log.e(e);
		}
	}
	
	private void accept(Selector selector) {
		try {
			while (channel.isOpen()) {
				SocketChannel sc = channel.accept();
				if (sc == null)
					return;
				sc.configureBlocking(false);
				sc.register(selector, SelectionKey.OP_READ);
				acceptConnection(sc);
			}
		} catch (ClosedChannelException e) {
			// Ignored
		} catch (Throwable t) {
			Log.w("TCPServer - IOException in accept(): %s", t.getMessage());
		}
	}
	
	private void acceptConnection(SocketChannel sc) {
		T session = sessionCreator.apply(sc);
		if (session == null) {
			Log.w("Session creator for TCPServer-%d created a null session!", addr.getPort());
			safeClose(sc);
			return;
		}
		if (session.getChannel() != sc) {
			Log.w("Session creator for TCPServer-%d created a session with an invalid channel!", addr.getPort());
			safeClose(sc);
			return;
		}
		channels.put(sc, session);
		sessionIdToChannel.put(session.getSessionId(), session);
		callbackThread.execute(session::onConnected);
	}
	
	private void read(SelectionKey key) {
		SelectableChannel selectableChannel = key.channel();
		if (selectableChannel == channel)
			return;
		SocketChannel sc = (SocketChannel) selectableChannel;
		T session = getSession(sc);
		if (session == null || !sc.isConnected()) {
			invalidate(sc, key);
			return;
		}
		try {
			bufferStream.reset();
			int n = 1;
			while (n > 0) {
				buffer.clear();
				n = sc.read(buffer);
				buffer.flip();
				byteBufferChannel.write(buffer);
			}
			if (bufferStream.size() > 0) {
				byte[] data = bufferStream.toByteArray();
				callbackThread.execute(() -> session.onIncomingData(data));
			}
			if (n < 0) {
				invalidate(sc, key);
			}
		} catch (ClosedChannelException e) {
			// Ignored
		} catch (Throwable t) {
			Log.w("TCPServer - IOException in read(): %s", t.getMessage());
			invalidate(sc, key);
		}
	}
	
	private void invalidate(SocketChannel sc, SelectionKey key) {
		key.cancel();
		disconnect(sc);
	}
	
	private static void safeClose(Channel c) {
		try {
			c.close();
		} catch (IOException e) {
			// Ignored - as long as it's closed
		}
	}
	
	public abstract static class TCPSession {
		
		private static final AtomicLong GLOBAL_SESSION_ID = new AtomicLong(0);
		
		private final SocketChannel sc;
		private final SocketAddress addr;
		private final long sessionId;
		
		protected TCPSession(SocketChannel sc) {
			this.sc = Objects.requireNonNull(sc, "socket");
			this.sessionId = GLOBAL_SESSION_ID.incrementAndGet();
			
			SocketAddress addr;
			try {
				addr = sc.getRemoteAddress();
			} catch (IOException e) {
				addr = null;
			}
			this.addr = addr;
		}
		
		protected void onConnected() {
			
		}
		
		protected void onDisconnected() {
			
		}
		
		/**
		 * Returns a globally unique session id for this particular connection
		 * @return the unique session id
		 */
		protected final long getSessionId() {
			return sessionId;
		}
		
		/**
		 * Returns the socket channel associated with this session
		 * @return the socket channel
		 */
		protected final SocketChannel getChannel() {
			return sc;
		}
		
		/**
		 * Returns the remote address that this socket is/was connected to
		 * @return the remote socket address
		 */
		@SuppressWarnings("unused") // open for subclass to use
		protected final SocketAddress getRemoteAddress() {
			return addr;
		}
		
		@SuppressWarnings("unused") // open for subclass to use
		protected void writeToChannel(ByteBuffer data) throws IOException {
			sc.write(data);
		}
		
		@SuppressWarnings("unused") // open for subclass to use
		protected void writeToChannel(byte [] data) throws IOException {
			sc.write(ByteBuffer.wrap(data));
		}
		
		protected void close() {
			safeClose(sc);
		}
		
		protected abstract void onIncomingData(byte[] data);
	}
	
}
