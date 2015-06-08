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
import javax.crypto.spec.SecretKeySpec;

public class AES {
	
	/**
	 * Method to execute the Diffie-Hellman phase and generate a shared 128-bit
	 * secret for AES algorithm.
	 * 
	 * The generated key size is 256 bit, however the Cipher class will
	 * generally not allow encryption with a key size of more than 128 bits. The
	 * apparent reason behind this is that some countries (although increasingly
	 * fewer) have restrictions on the permitted key strength of imported
	 * encryption software, although the actual number 128 is questionable. You
	 * can easily remove the restriction by overriding the security policy files
	 * with others that Sun provides here:
	 * http://www.oracle.com/technetwork/java
	 * /javase/downloads/jce-6-download-429243.html
	 * 
	 * @param keyAgreement
	 *            The peer's {@link KeyAgreement}
	 * 
	 * @param receivedPublicKey
	 *            The {@link PublicKey} of the partner
	 * 
	 * @return the AES key
	 */
	public static SecretKey generateSharedSecret(KeyAgreement keyAgreement, PublicKey receivedPublicKey) {
		SecretKey aesKey = null;
		SecretKey truncatedAesKey = null;
		try {
			keyAgreement.doPhase(receivedPublicKey, true);
			aesKey = keyAgreement.generateSecret("AES");
			
			// The method should return aesKey that is a 256-bit key. However
			// the Cipher class will generally not allow encryption with a key
			// size of more than 128 bits. The apparent reason behind this is
			// that some countries (although increasingly fewer) have
			// restrictions on the permitted key strength of imported encryption
			// software so the following code generates a 128-bit key with the
			// most significant bytes.
			
			byte[] keyBytes = aesKey.getEncoded();
			byte[] truncatedKeyBytes = new byte[keyBytes.length / 2];
			
			for (int i = 0; i < keyBytes.length / 2; i++)
				truncatedKeyBytes[i] = keyBytes[i];
			
			truncatedAesKey = new SecretKeySpec(truncatedKeyBytes, 0, truncatedKeyBytes.length, "AES");
			
			System.out.println("AES key length: " + aesKey.getEncoded().length + " ; truncated AES key length: " + truncatedAesKey.getEncoded().length);
			
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return truncatedAesKey;
	}
	
	/**
	 * Method to encrypt a byte array using AES algorithm in ECB mode.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return the encrypted byte array
	 */
	public static byte[] encryptDataECB(byte[] plaintext, SecretKey secret) {
        byte[] ciphertext = null ;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
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
	 * Method to decrypt a byte array encrypted using AES algorithm in ECB mode.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataECB(byte[] ciphertext, SecretKey secret) {
        byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
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
	 * Method to encrypt a byte array using AES algorithm in CBC mode.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataCBC(byte[] plaintext, SecretKey secret) {
        byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			
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
	 * Method to decrypt a byte array encrypted using AES algorithm in ECB mode.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataCBC(byte[] ciphertext, SecretKey secret, byte[] iv) {
        byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
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
	 * Method to encrypt a byte array using AES algorithm in OFB mode which does
	 * not require padding.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataOFB(byte[] plaintext, SecretKey secret) {
        byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
			
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
	 * Method to decrypt a byte array encrypted using AES algorithm in OFB  mode which does
	 * not require padding.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataOFB(byte[] ciphertext, SecretKey secret, byte[] iv) {
        byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("AES/OFB/NoPadding");
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
	 * Method to encrypt a byte array using AES algorithm in CFB mode which does
	 * not require padding.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataCFB(byte[] plaintext, SecretKey secret) {
        byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
			
	        cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
			
			// Getting the auto-generated initialization vector (a block of bits
			// that is used by several modes, including CFB, to randomize the
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
	 * Method to decrypt a byte array encrypted using AES algorithm in CFB  mode which does
	 * not require padding.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataCFB(byte[] ciphertext, SecretKey secret, byte[] iv) {
        byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("AES/CFB/NoPadding");
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
	 * Method to encrypt a byte array using AES algorithm in CTR mode which does
	 * not require padding.
	 * 
	 * @param plaintext
	 *            the byte array to be encrypted
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return a {@link CipherTextIvPair} containing the pair (encrypted byte
	 *         array, initialization vector)
	 */
	public static CipherTextIvPair encryptDataCTR(byte[] plaintext, SecretKey secret) {
        byte[] ciphertext = null ;
        byte[] iv = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
			
	        cipher.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = cipher.doFinal(plaintext);
			
			// Getting the auto-generated initialization vector (a block of bits
			// that is used by several modes, including CFB, to randomize the
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
	 * Method to decrypt a byte array encrypted using AES algorithm in CTR mode
	 * which does not require padding.
	 * 
	 * @param ciphertext
	 *            the ciphered byte array
	 * 
	 * @param secret
	 *            the AES secret
	 * 
	 * @return the decrypted byte array
	 */
	public static byte[] decryptDataCTR(byte[] ciphertext, SecretKey secret, byte[] iv) {
        byte[] cleartext = null ;
		try {
			Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
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
