package it.unipr.ce.dsg.nam4j.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * <p>
 * This class provides methods to encrypt/decrypt data using DES algorithms.
 * Electronic Codebook (ECB), Cipher Block Chaining (CBC) and Output Feedback
 * (OFB) modes are supported.
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

public class DES {
	
	/**
	 * Method to execute the Diffie-Hellman phase and generate a shared secret
	 * for DES algorithm.
	 * 
	 * @param keyAgreement
	 *            The peer's {@link KeyAgreement}
	 * 
	 * @param receivedPublicKey
	 *            The {@link PublicKey} of the partner
	 */
	public static SecretKey generateSharedSecret(KeyAgreement keyAgreement, PublicKey receivedPublicKey) {
		SecretKey desKey = null;
		try {
			keyAgreement.doPhase(receivedPublicKey, true);
			desKey = keyAgreement.generateSecret("DES");
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return desKey;
	}
	
	/**
	 * Method to encrypt a byte array using DES algorithm in ECB mode.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @return the encrypted byte array
	 */
	public static byte[] encryptDataECB(byte[] plaintext, SecretKey secret) {
        byte[] ciphertext = null ;
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");
	        cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        return ciphertext;
	}
	
	/**
	 * Method to decrypt a byte array encrypted using DES algorithm in ECB mode.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataECB(byte[] ciphertext, SecretKey secret) {
        byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, secret);
	        cleartext = cipher.doFinal(ciphertext);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} 
        return cleartext;
	}
	
	/**
	 * Method to encrypt a byte array using DES algorithm in CBC mode.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataCBC(byte[] plaintext, SecretKey secret) {
		byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5PADDING");
			
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
			
			// Getting the auto-generated initialization vector (a block of bits
			// that is used by several modes, including CBC, to randomize the
			// encryption and hence to produce distinct ciphertexts even if the
			// same plaintext is encrypted multiple times
			iv = cipher.getIV();
		
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        
		return new CipherTextIvPair(ciphertext, iv);
	}
	
	/**
	 * Method to decrypt a byte array encrypted using DES algorithm in CBC mode.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @param iv
	 *            the initialization vector
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataCBC(byte[] ciphertext, SecretKey secret, byte[] iv) {
		byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5PADDING");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
	        cleartext = cipher.doFinal(ciphertext);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} 
        return cleartext;
	}
	
	/**
	 * Method to encrypt a byte array using DES algorithm in OFB mode which does
	 * not require padding.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataOFB(byte[] plaintext, SecretKey secret) {
		byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/OFB/NoPadding");
			
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
			
			// Getting the auto-generated initialization vector (a block of bits
			// that is used by several modes, including OFB, to randomize the
			// encryption and hence to produce distinct ciphertexts even if the
			// same plaintext is encrypted multiple times
			iv = cipher.getIV();
		
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        
		return new CipherTextIvPair(ciphertext, iv);
	}
	
	/**
	 * Method to decrypt a byte array encrypted using DES algorithm in OFB mode which does
	 * not require padding.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @param iv
	 *            the initialization vector
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataOFB(byte[] ciphertext, SecretKey secret, byte[] iv) {
		byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("DES/OFB/NoPadding");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
	        cleartext = cipher.doFinal(ciphertext);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} 
        return cleartext;
	}
	
	/**
	 * Method to encrypt a byte array using DES algorithm in CFB mode which does
	 * not require padding.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataCFB(byte[] plaintext, SecretKey secret) {
		byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/CFB/NoPadding");
			
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
			
			// Getting the auto-generated initialization vector (a block of bits
			// that is used by several modes, including CBC, to randomize the
			// encryption and hence to produce distinct ciphertexts even if the
			// same plaintext is encrypted multiple times
			iv = cipher.getIV();
		
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        
		return new CipherTextIvPair(ciphertext, iv);
	}
	
	/**
	 * Method to decrypt a byte array encrypted using DES algorithm in CFB mode which does
	 * not require padding.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @param iv
	 *            the initialization vector
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataCFB(byte[] ciphertext, SecretKey secret, byte[] iv) {
		byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("DES/CFB/NoPadding");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
	        cleartext = cipher.doFinal(ciphertext);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} 
        return cleartext;
	}
	
	/**
	 * Method to encrypt a byte array using DES algorithm in CTR mode which does
	 * not require padding.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte
	 *         array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataCTR(byte[] plaintext, SecretKey secret) {
		byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("DES/CTR/NoPadding");
			
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
			
			// Getting the auto-generated initialization vector (a block of bits
			// that is used by several modes, including CBC, to randomize the
			// encryption and hence to produce distinct ciphertexts even if the
			// same plaintext is encrypted multiple times
			iv = cipher.getIV();
		
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
        
		return new CipherTextIvPair(ciphertext, iv);
	}
	
	/**
	 * Method to decrypt a byte array encrypted using DES algorithm in CTR mode
	 * which does not require padding.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the DES secret
	 * 
	 * @param iv
	 *            the initialization vector
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataCTR(byte[] ciphertext, SecretKey secret, byte[] iv) {
		byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("DES/CTR/NoPadding");
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
	        cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
	        cleartext = cipher.doFinal(ciphertext);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		
        return cleartext;
	}

}
