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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import com.projectswg.common.callback.CallbackManager;
import com.projectswg.common.concurrency.Delay;
import com.projectswg.common.debug.Assert;
import com.projectswg.common.debug.Log;

public class TCPSocket {
	
	private final CallbackManager<TCPSocketCallback> callbackManager;
	private final TCPSocketListener listener;
	private final InetSocketAddress address;
	private final AtomicReference<SocketState> state;
	private final Object stateMutex;
	private final int bufferSize;
	private Socket socket;
	private InputStream socketInputStream;
	private OutputStream socketOutputStream;
	
	public TCPSocket(InetSocketAddress address, int bufferSize) {
		this.callbackManager = new CallbackManager<>("tcpsocket-"+address, 1);
		this.listener = new TCPSocketListener();
		this.address = address;
		this.state = new AtomicReference<>(SocketState.CLOSED);
		this.stateMutex = new Object();
		this.bufferSize = bufferSize;
		
		this.socket = null;
		this.socketInputStream = null;
		this.socketOutputStream = null;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public InetSocketAddress getRemoteAddress() {
		return address;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public boolean isAlive() {
		synchronized (stateLock()) {
			return socket != null && listener.isAlive();
		}
	}
	
	public boolean isConnected() {
		synchronized (stateLock()) {
			return socket != null && socket.isConnected();
		}
	}
	
	public void setCallback(TCPSocketCallback callback) {
		callbackManager.setCallback(callback);
	}
	
	public void removeCallback() {
		callbackManager.clearCallbacks();
	}
	
	public void createConnection() throws IOException {
		synchronized (stateLock()) {
			checkAndSetState(SocketState.CLOSED, SocketState.CREATED);
			socket = createSocket();
		}
	}
	
	public void startConnection() throws IOException {
		synchronized (stateLock()) {
			try {
				checkAndSetState(SocketState.CREATED, SocketState.CONNECTING);
				socket.connect(address);
				socketInputStream = socket.getInputStream();
				socketOutputStream = socket.getOutputStream();
			} catch (IOException e) {
				checkAndSetState(SocketState.CONNECTING, SocketState.CLOSED);
				socket = null;
				socketInputStream = null;
				socketOutputStream = null;
				throw e;
			}
			
			callbackManager.start();
			listener.start();
			checkAndSetState(SocketState.CONNECTING, SocketState.CONNECTED);
			callbackManager.callOnEach((callback) -> callback.onConnected(this));
		}
	}
	
	public void connect() throws IOException {
		createConnection();
		startConnection();
	}
	
	public boolean disconnect() {
		synchronized (stateLock()) {
			if (socket == null)
				return true;
			try {
				checkAndSetState(SocketState.CONNECTED, SocketState.CLOSED);
				socket.close();
				socket = null;
				socketInputStream = null;
				socketOutputStream = null;
				
				if (listener.isAlive()) {
					listener.stop();
					listener.awaitTermination();
				}
				
				if (callbackManager.isRunning()) {
					callbackManager.callOnEach((callback) -> callback.onDisconnected(this));
					callbackManager.stop();
				}
				return true;
			} catch (IOException e) {
				Log.e(e);
			}
			return false;
		}
	}
	
	public boolean send(NetBuffer data) {
		return send(data.array(), data.position(), data.remaining());
	}
	
	public boolean send(ByteBuffer data) {
		return send(data.array(), data.position(), data.remaining());
	}
	
	public boolean send(byte [] data) {
		return send(data, 0, data.length);
	}
	
	public boolean send(byte [] data, int offset,  int length) {
		synchronized (stateLock()) {
			try {
				if (socket == null)
					return false;
				
				if (length > 0)
					socketOutputStream.write(data, offset, length);
				
				return true;
			} catch (IOException e) {
				Log.e(e);
			}
			return false;
		}
	}
	
	protected Socket createSocket() throws IOException {
		return new Socket();
	}
	
	protected final Object stateLock() {
		return stateMutex;
	}
	
	/**
	 * Checks the current state to see if it matches the expected, and if so, changes it to the new state. If not, it fails the assertion
	 * @param expected the expected state
	 * @param state the new state
	 */
	private void checkAndSetState(SocketState expected, SocketState state) {
		Assert.notNull(expected, "Expected state cannot be null!");
		Assert.notNull(state, "New state cannot be null!");
		Assert.test(this.state.compareAndSet(expected, state), "Failed to set state! Was: " + this.state.get() + "  Expected: " + expected + "  Update: " + state);
	}
	
	public interface TCPSocketCallback {
		void onConnected(TCPSocket socket);
		void onDisconnected(TCPSocket socket);
		void onIncomingData(TCPSocket socket, byte [] data);
	}
	
	private enum SocketState {
		CLOSED,
		CREATED,
		CONNECTING,
		CONNECTED
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
			thread = new Thread(this, "TCPServer Port#" + address.getPort());
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
				InputStream input = TCPSocket.this.socketInputStream;
				byte [] buffer = new byte[TCPSocket.this.bufferSize];
				while (running.get()) {
					waitIncoming(input, buffer);
				}
			} catch (Throwable t) {
				
			} finally {
				running.set(false);
				alive.set(false);
				thread = null;
				disconnect();
			}
		}
		
		private void waitIncoming(InputStream input, byte [] buffer) throws IOException {
			int n = input.read(buffer);
			if (n == 0)
				return;
			if (n < 0)
				throw new EOFException();
			byte [] data = new byte[n];
			System.arraycopy(buffer, 0, data, 0, n);
			callbackManager.callOnEach((callback) -> callback.onIncomingData(TCPSocket.this, data));
		}
		
	}
}
