/***********************************************************************************
* Copyright (c) 2015 /// Project SWG /// www.projectswg.com                        *
*                                                                                  *
* ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on           *
* July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies.  *
* Our goal is to create an emulator which will provide a server for players to     *
* continue playing a game similar to the one they used to play. We are basing      *
* it on the final publish of the game prior to end-game events.                    *
*                                                                                  *
* This file is part of Holocore.                                                   *
*                                                                                  *
* -------------------------------------------------------------------------------- *
*                                                                                  *
* Holocore is free software: you can redistribute it and/or modify                 *
* it under the terms of the GNU Affero General Public License as                   *
* published by the Free Software Foundation, either version 3 of the               *
* License, or (at your option) any later version.                                  *
*                                                                                  *
* Holocore is distributed in the hope that it will be useful,                      *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                    *
* GNU Affero General Public License for more details.                              *
*                                                                                  *
* You should have received a copy of the GNU Affero General Public License         *
* along with Holocore.  If not, see <http://www.gnu.org/licenses/>.                *
*                                                                                  *
***********************************************************************************/
package com.projectswg.common.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.projectswg.common.callback.CallbackManager;
import com.projectswg.common.debug.Assert;
import com.projectswg.common.debug.Log;

public class TCPServer {
	
	private final CallbackManager<TCPCallback> callbackManager;
	private final Map<SocketAddress, SocketChannel> sockets;
	private final AtomicBoolean running;
	private final InetAddress addr;
	private final int port;
	private final int bufferSize;
	private final TCPServerListener listener;
	private ServerSocketChannel channel;
	
	public TCPServer(int port, int bufferSize) {
		this(null, port, bufferSize);
	}
	
	public TCPServer(InetAddress addr, int port, int bufferSize) {
		this.callbackManager = new CallbackManager<>("tcpserver-"+port, 1);
		this.sockets = new HashMap<>();
		this.running = new AtomicBoolean(false);
		this.addr = addr;
		this.port = port;
		this.bufferSize = bufferSize;
		this.channel = null;
		listener = new TCPServerListener();
	}
	
	public int getPort() {
		return channel.socket().getLocalPort();
	}
	
	public void bind() throws IOException {
		if (running.getAndSet(true)) {
			Assert.fail();
			return;
		}
		callbackManager.start();
		channel = ServerSocketChannel.open();
		channel.socket().bind(new InetSocketAddress(addr, port));
		channel.configureBlocking(false);
		listener.start();
	}
	
	public boolean disconnect(SocketAddress sock) {
		Assert.notNull(sock);
		synchronized (sockets) {
			SocketChannel sc = sockets.get(sock);
			if (sc == null)
				return true;
			sockets.remove(sock);
			try {
				sc.close();
				callbackManager.callOnEach((callback) -> callback.onConnectionDisconnect(sc, sock));
				return true;
			} catch (IOException e) {
				Log.e(e);
				return false;
			}
		}
	}
	
	private boolean disconnect(SocketChannel sc) {
		try {
			return disconnect(sc.getRemoteAddress());
		} catch (IOException e) {
			return false;
		}
	}
	
	public boolean close() {
		if (!running.getAndSet(false)) {
			Assert.fail();
			return false;
		}
		callbackManager.stop();
		listener.stop();
		try {
			channel.close();
			return true;
		} catch (IOException e) {
			Log.e(e);
		}
		return false;
	}
	
	public SocketChannel getChannel(SocketAddress sock) {
		synchronized (sockets) {
			return sockets.get(sock);
		}
	}
	
	public void setCallback(TCPCallback callback) {
		callbackManager.setCallback(callback);
	}
	
	public interface TCPCallback {
		void onIncomingConnection(SocketChannel s, SocketAddress addr);
		void onConnectionDisconnect(SocketChannel s, SocketAddress addr);
		void onIncomingData(SocketChannel s, SocketAddress addr, byte [] data);
	}
	
	private class TCPServerListener implements Runnable {
		
		private final ByteBuffer buffer;
		private Thread thread;
		private boolean running;
		
		public TCPServerListener() {
			buffer = ByteBuffer.allocateDirect(bufferSize);
			running = false;
			thread = null;
		}
		
		public void start() {
			running = true;
			thread = new Thread(this, "TCPServerListener-" + channel.socket().getLocalPort());
			thread.start();
		}
		
		public void stop() {
			running = false;
			if (thread != null)
				thread.interrupt();
			thread = null;
		}
		
		@Override
		public void run() {
			try (Selector selector = setupSelector()) {
				while (running) {
					try {
						selector.select();
						processSelectionKeys(selector);
					} catch (Exception e) {
						Log.e(e);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							break;
						}
					}
				}
			} catch (IOException e) {
				Log.e(e);
			}
		}
		
		private Selector setupSelector() throws IOException {
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_ACCEPT);
			return selector;
		}
		
		private void processSelectionKeys(Selector selector) throws ClosedChannelException {
			for (SelectionKey key : selector.selectedKeys()) {
				if (!key.isValid())
					continue;
				if (key.isAcceptable()) {
					accept(selector);
				} else if (key.isReadable()) {
					SelectableChannel selectable = key.channel();
					if (selectable instanceof SocketChannel) {
						boolean canRead = true;
						while (canRead)
							canRead = read(key, (SocketChannel) selectable);
					}
				}
			}
		}
		
		private void accept(Selector selector) {
			try {
				while (channel.isOpen()) {
					SocketChannel sc = channel.accept();
					if (sc == null)
						break;
					SocketChannel old = sockets.get(sc.getRemoteAddress());
					if (old != null)
						disconnect(old);
					sockets.put(sc.getRemoteAddress(), sc);
					sc.configureBlocking(false);
					sc.register(selector, SelectionKey.OP_READ);
					SocketAddress addr = sc.getRemoteAddress();
					callbackManager.callOnEach((callback) -> callback.onIncomingConnection(sc, addr));
				}
			} catch (IOException e) {
				Log.e(e);
			}
		}
		
		private boolean read(SelectionKey key, SocketChannel s) {
			try {
				buffer.position(0);
				buffer.limit(bufferSize);
				int n = s.read(buffer);
				buffer.flip();
				if (n < 0) {
					key.cancel();
					disconnect(s);
				} else if (n > 0) {
					ByteBuffer smaller = ByteBuffer.allocate(n);
					smaller.put(buffer);
					SocketAddress addr = s.getRemoteAddress();
					callbackManager.callOnEach((callback) -> callback.onIncomingData(s, addr, smaller.array()));
					return true;
				}
			} catch (IOException e) {
				if (e.getMessage() != null && e.getMessage().toLowerCase(Locale.US).contains("connection reset"))
					Log.e("Connection Reset with %s", s.socket().getRemoteSocketAddress());
				else if (!(e instanceof ClosedByInterruptException))
					Log.e(e);
				key.cancel();
				disconnect(s);
			}
			return false;
		}
	}
	
}
