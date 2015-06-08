package it.unipr.ce.dsg.nam4j.security;

/**
 * <p>
 * This class stores the result of an encryption as a pair (ciphertext,
 * initialization vector). Some block cipher operation modes (e.g., CBC, OFB)
 * use an initialization vector, or starting variable, which is a block of bits
 * used to randomize the encryption and produce distinct ciphertexts even if the
 * same plaintext is encrypted multiple times. Decryption requires the same
 * vector used during encryption.
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

public class CipherTextIvPair {
	
	byte[] ciphertext, iv;
	
	public CipherTextIvPair (byte[] ciphertext, byte[] iv) {
		setCiphertext(ciphertext);
		setIv(iv);
	}

	public byte[] getCiphertext() {
		return ciphertext;
	}

	private void setCiphertext(byte[] ciphertext) {
		this.ciphertext = ciphertext;
	}

	public byte[] getIv() {
		return iv;
	}

	private void setIv(byte[] iv) {
		this.iv = iv;
	}
}
