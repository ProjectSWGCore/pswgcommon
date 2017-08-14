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

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

import com.projectswg.common.callback.CallbackManager;
import com.projectswg.common.concurrency.Delay;
import com.projectswg.common.debug.Assert;
import com.projectswg.common.debug.Log;

public class TCPSocket {
	
	private final CallbackManager<TCPSocketCallback> callbackManager;
	private final TCPSocketListener listener;
	private final InetSocketAddress address;
	private final int bufferSize;
	private SocketChannel socket;
	
	public TCPSocket(InetSocketAddress address, int bufferSize) {
		this.callbackManager = new CallbackManager<>("tcpsocket-"+address, 1);
		this.listener = new TCPSocketListener();
		this.address = address;
		this.bufferSize = bufferSize;
		this.socket = null;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public InetSocketAddress getRemoteAddress() {
		return address;
	}
	
	public SocketChannel getSocket() {
		return socket;
	}
	
	public boolean isAlive() {
		return socket != null && listener.isAlive();
	}
	
	public boolean isConnected() {
		return socket != null && socket.isConnected();
	}
	
	public void setCallback(TCPSocketCallback callback) {
		callbackManager.setCallback(callback);
	}
	
	public void removeCallback() {
		callbackManager.clearCallbacks();
	}
	
	public boolean connect() {
		Assert.isNull(socket, "Socket must be null! Cannot connect twice!");
		Assert.test(!listener.isAlive(), "Listener must not be alive! Cannot connect twice!");
		try {
			callbackManager.start();
			socket = SocketChannel.open(address);
			if (socket.finishConnect()) {
				listener.start();
				callbackManager.callOnEach((callback) -> callback.onConnected(this));
				return true;
			}
		} catch (IOException e) {
			Log.e(e);
		}
		socket = null;
		callbackManager.stop();
		return false;
	}
	
	public boolean disconnect() {
		if (socket == null)
			return true;
		try {
			socket.close();
			if (listener.isAlive()) {
				listener.stop();
				listener.awaitTermination();
			}
			if (callbackManager.isRunning()) {
				callbackManager.callOnEach((callback) -> callback.onDisconnected(this));
				callbackManager.stop();
			}
			socket = null;
			return true;
		} catch (IOException e) {
			Log.e(e);
		}
		return false;
	}
	
	public boolean send(ByteBuffer data) {
		try {
			while (data.hasRemaining()) {
				if (socket == null || socket.write(data) <= 0)
					return false;
			}
			return true;
		} catch (IOException e) {
			Log.e(e);
		}
		return false;
	}
	
	public interface TCPSocketCallback {
		void onConnected(TCPSocket socket);
		void onDisconnected(TCPSocket socket);
		void onIncomingData(TCPSocket socket, byte [] data);
	}
	
	private class TCPSocketListener implements Runnable {
		
		private final AtomicBoolean running;
		private final AtomicBoolean alive;
		
		private Thread thread;
		
		public TCPSocketListener() {
			this.running = new AtomicBoolean(false);
			this.alive = new AtomicBoolean(false);
			this.thread = null;
		}
		
		public void start() {
			Assert.test(!running.get(), "Cannot start listener! Already started!");
			Assert.isNull(thread, "Cannot start listener! Already started!");
			thread = new Thread(this);
			running.set(true);
			thread.start();
		}
		
		public void stop() {
			Assert.test(running.get(), "Cannot stop listener! Already stopped!");
			Assert.notNull(thread, "Cannot stop listener! Already stopped!");
			running.set(false);
			if (thread != null)
				thread.interrupt();
			thread = null;
		}
		
		public void awaitTermination() {
			while (isAlive()) {
				if (!Delay.sleepMicro(5))
					break;
			}
		}
		
		public boolean isAlive() {
			return alive.get();
		}
		
		@Override
		public void run() {
			try {
				alive.set(true);
				SocketChannel sc = TCPSocket.this.socket;
				int bufferSize = TCPSocket.this.bufferSize;
				Assert.notNull(sc, "SocketChannel is null at start of listener run()!");
				Assert.test(bufferSize > 0, "Buffer size is <= 0 at start of listener run()!");
				ByteBuffer buf = ByteBuffer.allocateDirect(bufferSize);
				while (running.get()) {
					waitIncoming(sc, buf, bufferSize);
				}
			} catch (Throwable t) {
				
			} finally {
				running.set(false);
				alive.set(false);
				thread = null;
				disconnect();
			}
		}
		
		private void waitIncoming(SocketChannel sc, ByteBuffer buf, int bufferSize) throws IOException {
			buf.position(0);
			buf.limit(bufferSize);
			int n = sc.read(buf);
			buf.flip();
			if (n == 0)
				return;
			if (n < 0)
				throw new EOFException();
			byte [] data = new byte[n];
			buf.position(0);
			buf.get(data, 0, n);
			callbackManager.callOnEach((callback) -> callback.onIncomingData(TCPSocket.this, data));
		}
		
	}
}
