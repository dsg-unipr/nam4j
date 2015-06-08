package it.unipr.ce.dsg.nam4j.security;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

/**
 * <p>
 * This class implements Diffie-Hellman key exchange. The generated shared
 * secret can be used to encrypt data using encryption algorithms.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class DHKeyExchange {
	
	public DHKeyExchange() {
		generateDHParameters();
		
		// ACTIONS PERFORMED BY THE PEER THAT STARTS THE CONVERSATION
		// The peer has to call generateDHParameters() to get the DHParameterSpec params
		// Then it has to call generateDHKeypair() and pass it the DHParameterSpec as param to get the KeyPair object
		// Then it has to call initializeKeyAgreement and pass it the KeyPair as param to get the KeyAgreement object
		// Then it has to encode its public key before sending it, by calling encodePublicKey pass it the KeyPair as param
		// *** The peer SENDS its key and WAITS to receive the key from the partner
		
		// The peer decodes the received key by calling decodeReceivedPublicKey
		// The peer generates its DES shared secret by calling generateDesSharedSecret and passing as params its key agreement and the key of the partner
		// The peer can decrypt using DES.decryptDataDESECB or DES.decryptDataDESCBC
		
		// ACTIONS PERFORMED BY THE PARTNER PEER
		// The peer received the partner's encoded public key
		// It must generate the key pair by calling generateDHKeypairFromReceivedKey and get a KeyPair object
		// Then it has to to generate the key agreement by calling initializeKeyAgreementFromReceivedKey and get a KeyAgreement object
		// Then it has to encode its public key before sending it, by calling encodePublicKey pass it the KeyPair as param
		// *** The peer SENDS its public key to the partner
		
		// The peer generates its DES shared secret by calling generateDesSharedSecret and passing as params its key agreement and the key of the partner
		// The peer can encrypt using DES.encryptDataDESECB or DES.encryptDataDESCBC (ECB is faster but less secure than CBC)
		
	}
	
	// XXX Methods executed by the node that starts the conversation
	
	/** Method to generate Diffie-Hellman parameters. */
	public static DHParameterSpec generateDHParameters() {
		System.out.println("Creating Diffie-Hellman parameters");
		
		DHParameterSpec dhSkipParamSpec = null;
		try {
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			paramGen.init(512);
			AlgorithmParameters params = paramGen.generateParameters();
			dhSkipParamSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			e.printStackTrace();
		}
		return dhSkipParamSpec;
	}
	
	/**
	 * Method to generate a Diffie-Hellman key pair.
	 * 
	 * @return a Diffie-Hellman key pair
	 */
	public static KeyPair generateDHKeypair(DHParameterSpec dhSkipParamSpec) {
		System.out.println("Generating Diffie-Hellman key pair...");
		
		KeyPair aliceKpair = null;
		try {
			KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
			aliceKpairGen.initialize(dhSkipParamSpec);
			aliceKpair = aliceKpairGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
        return aliceKpair;
	}
	
	/**
	 * Method to initialize a key agreement.
	 * 
	 * @param keyPair
	 *            a key pair
	 * 
	 * @return a key agreement
	 */
	public static KeyAgreement initializeKeyAgreement(KeyPair keyPair) {
		System.out.println("Initializing key agreement for key pair...");
		
		KeyAgreement keyAgree = null;
		try {
			keyAgree = KeyAgreement.getInstance("DH");
			keyAgree.init(keyPair.getPrivate());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        return keyAgree;
	}
	
	// XXX Methods executed by the node that was asked for a conversation
	
	public static PublicKey decodeReceivedPublicKey(byte[] receivedPublicKeyEncripted) {
		PublicKey pubKey = null;
		try {
			KeyFactory keyFac = KeyFactory.getInstance("DH");
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(receivedPublicKeyEncripted);
	        pubKey = keyFac.generatePublic(x509KeySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
        return pubKey;
	}
	
	/**
	 * Method to generate a Diffie-Hellman key pair from a received encripted
	 * public key.
	 * 
	 * @param receivedPublicKeyEncripted
	 *            the received encripted public key
	 * 
	 * @return a Diffie-Hellman key pair
	 */
	public static KeyPair generateDHKeyPairFromReceivedKey(byte[] receivedPublicKeyEncripted) {
		KeyPair keypair = null;
		try {
	        PublicKey pubKey = decodeReceivedPublicKey(receivedPublicKeyEncripted);
	
			// The peer gets the DH parameters associated with received's public
			// key. The node must use the same parameters when generating its
			// own key pair.
	        DHParameterSpec dhParamSpec = ((DHPublicKey)pubKey).getParams();
	
	        // The peer creates its own DH key pair
	        System.out.println("Generating Diffie-Hellman keypair from received key...");
	        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
	        keyPairGen.initialize(dhParamSpec);
	        keypair = keyPairGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
        return keypair;
	}
	
	public static KeyAgreement initializeKeyAgreementFromReceivedKey(KeyPair keyPair) {
		KeyAgreement keyAgree = null;
		try {
	        System.out.println("Creating and initializing a Diffie-Hellman agreement object from received key...");
	        keyAgree = KeyAgreement.getInstance("DH");
	        keyAgree.init(keyPair.getPrivate());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return keyAgree;
	}
	
	// XXX Methods executed by both partners
	
	/**
	 * Method to encode the public key.
	 * 
	 * @param keyPair
	 *            a Diffie-Hellman key pair
	 * 
	 * @return the encoded public key
	 */
	public static byte[] encodePublicKey(KeyPair keyPair) {
		return keyPair.getPublic().getEncoded();
	}
	
	/**
	 * Method to convert a byte to hex digit.
	 * 
	 * @param b
	 *            the byte to be converted
	 * 
	 * @param buf
	 *            the buffer to which the result is written
	 */
	public static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

	/**
	 * Method to convert a byte array to hex string.
	 * 
	 * @param block
	 *            the byte array to be converted
	 * 
	 * @return the string representing the byte array
	 */
    public static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();

        int len = block.length;

        for (int i = 0; i < len; i++) {
             byte2hex(block[i], buf);
             if (i < len-1) {
                 buf.append(":");
             }
        }
        return buf.toString();
    }

}
