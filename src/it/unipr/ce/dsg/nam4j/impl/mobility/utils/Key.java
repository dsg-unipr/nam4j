package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * <p>
 * This class represents the key of the network's peers.
 * </p>
 * 
 * <p>
 * Copyright (c) 2014, Distributed Systems Group, University of Parma, Italy.
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
public class Key {

	private BigInteger keyValue;

	public Key(String input) {
		this.keyValue = generateKey(input);
	}

	/**
	 * Method to compute the SHA-1 key for the peers and the resources.
	 * 
	 * @param
	 * 			- input a String used to compute the key
	 * 
	 * @return The BigInteger key
	 */
	private BigInteger generateKey(String input) {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		md.reset();
		byte[] buffer = input.getBytes();
		md.update(buffer);
		byte[] digest = md.digest();

		BigInteger biKey = new BigInteger(1, digest);

		return biKey;
	}

	public BigInteger getKey() {
		return keyValue;
	}

	public void setKey(BigInteger key) {
		this.keyValue = key;
	}

	/**
	 * Method to compute a binary representation of the key.
	 * 
	 * @return a binary string representing the key
	 */
	public String getBitString() {

		StringBuilder sb = new StringBuilder();
		byte[] bytes = this.keyValue.toByteArray();

		/* If the most significant byte is 0 we can drop it */
		if(bytes[0] == 0) {
			bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
		}

		/* Convert to binary */
		for(int i = 0; i < bytes.length; i++) {
			sb.append(String.format("%8s", Integer.toBinaryString(bytes[i] & 255)).replace(' ', '0'));
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		return this.getBitString();
	}
	
	/* (non Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyValue == null) ? 0 : keyValue.hashCode());
		return result;
	}

	/* (non Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		if (keyValue == null) {
			if (other.keyValue != null)
				return false;
		} else if (!keyValue.equals(other.keyValue))
			return false;
		return true;
	}

	/**
	 * Compute the distance between two keys.
	 * 
	 * @param k1
	 * 			The first key
	 * 
	 * @param k2
	 * 			The second key
	 * 
	 * @return The distance between two keys
	 */
	public static BigInteger computeDistanceBetweenKeys(Key k1, Key k2) {
		BigInteger distance = k1.getKey().subtract(k2.getKey());
		return distance;
	}
	
}
