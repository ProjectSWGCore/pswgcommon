/***********************************************************************************
 * Copyright (c) 2018 /// Project SWG /// www.projectswg.com                       *
 *                                                                                 *
 * ProjectSWG is the first NGE emulator for Star Wars Galaxies founded on          *
 * July 7th, 2011 after SOE announced the official shutdown of Star Wars Galaxies. *
 * Our goal is to create an emulator which will provide a server for players to    *
 * continue playing a game similar to the one they used to play. We are basing     *
 * it on the final publish of the game prior to end-game events.                   *
 *                                                                                 *
 * This file is part of PSWGCommon.                                                *
 *                                                                                 *
 * --------------------------------------------------------------------------------*
 *                                                                                 *
 * PSWGCommon is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU Affero General Public License as                  *
 * published by the Free Software Foundation, either version 3 of the              *
 * License, or (at your option) any later version.                                 *
 *                                                                                 *
 * PSWGCommon is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   *
 * GNU Affero General Public License for more details.                             *
 *                                                                                 *
 * You should have received a copy of the GNU Affero General Public License        *
 * along with PSWGCommon.  If not, see <http://www.gnu.org/licenses/>.             *
 ***********************************************************************************/

package com.projectswg.common.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.SocketImplFactory;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SecureSocketFactory extends SSLSocketFactory implements SocketImplFactory {
	
	private final AtomicBoolean loaded;
	private SSLSocketFactory sslSocketFactory;
	
	public SecureSocketFactory() {
		this.loaded = new AtomicBoolean(false);
		this.sslSocketFactory = null;
	}
	
	@Override
	public Socket createSocket(Socket arg0, InputStream arg1, boolean arg2) throws IOException {
		return sslSocketFactory.createSocket(arg0, arg1, arg2);
	}
	
	@Override
	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
		return sslSocketFactory.createSocket(arg0, arg1, arg2, arg3);
	}
	
	@Override
	public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
		return sslSocketFactory.createSocket(arg0, arg1);
	}
	
	@Override
	public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
		return sslSocketFactory.createSocket(arg0, arg1);
	}
	
	@Override
	public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
		return sslSocketFactory.createSocket(arg0, arg1, arg2, arg3);
	}
	
	@Override
	public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
		return sslSocketFactory.createSocket(arg0, arg1, arg2, arg3);
	}
	
	@Override
	public boolean equals(Object obj) {
		return sslSocketFactory.equals(obj);
	}
	
	@Override
	public String[] getDefaultCipherSuites() {
		return sslSocketFactory.getDefaultCipherSuites();
	}
	
	@Override
	public String[] getSupportedCipherSuites() {
		return sslSocketFactory.getSupportedCipherSuites();
	}
	
	@Override
	public int hashCode() {
		return sslSocketFactory.hashCode();
	}
	
	@Override
	public String toString() {
		return sslSocketFactory.toString();
	}
	
	@Override
	public SocketImpl createSocketImpl() {
		return null;
	}

	/**
	 * Loads the encryption mechanism 
	 * @param keystoreFile the keystore file
	 * @param password the password for the keystore
	 * @throws KeyStoreException if KeyManagerFactory.init or TrustManagerFactory.init fails
	 * @throws NoSuchAlgorithmException if the algorithm for the keystore or key manager could not be found
	 * @throws CertificateException if any of the certificates in the keystore could not be loaded
	 * @throws FileNotFoundException if the keystore file does not exist
	 * @throws IOException if there is an I/O or format problem with the keystore data, if a password is required but not given, or if the given password was incorrect. If the error is due to a wrong password, the cause of the IOException should be an UnrecoverableKeyException
	 * @throws KeyManagementException if SSLContext.init fails
	 * @throws UnrecoverableKeyException if the key cannot be recovered (e.g. the given password is wrong).
	 */
	public void load(File keystoreFile, char [] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException {
		// First initialize the key and trust material
		KeyStore ksKeys = KeyStore.getInstance("JKS");
		ksKeys.load(new FileInputStream(keystoreFile), password);
		
		// KeyManagers decide which key material to use
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ksKeys, password);
		
		// TrustManagers decide whether to allow connections
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ksKeys);
		
		// Used to create the 
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		
		this.sslSocketFactory = sslContext.getSocketFactory();
		loaded.set(true);
	}
	
	public boolean isLoaded() {
		return loaded.get();
	}
	
	@Override
	public Socket createSocket() throws IOException {
		if (!loaded.get())
			throw new IllegalStateException("SecureSocketFactory hasn't been loaded yet!");
		return sslSocketFactory.createSocket();
	}
	
}
