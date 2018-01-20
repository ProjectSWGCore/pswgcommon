package com.projectswg.common.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class TCPSecureSocket extends TCPSocket {
	
	private final SecureSocketFactory socketFactory;
	
	public TCPSecureSocket(InetSocketAddress address, int bufferSize) {
		super(address, bufferSize);
		this.socketFactory = new SecureSocketFactory();
	}
	
	/**
	 * Sets up the encryption mechanism 
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
	public void setupEncryption(File keystoreFile, char [] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyManagementException, UnrecoverableKeyException {
		socketFactory.load(keystoreFile, password);
	}
	
	@Override
	public Socket createSocket() throws IOException {
		return socketFactory.createSocket();
	}
	
}
