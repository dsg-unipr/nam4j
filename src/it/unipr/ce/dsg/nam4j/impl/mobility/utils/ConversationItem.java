package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils.EncryptionAlgorithm;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.Dependency;

import java.security.KeyPair;
import java.util.ArrayList;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;

/**
 * <p>
 * This class represents a conversation between two peers.
 * </p>
 * 
 * <p>
 *  Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 *  Permission is granted to copy, distribute and/or modify this document
 *  under the terms of the GNU Free Documentation License, Version 1.3
 *  or any later version published by the Free Software Foundation;
 *  with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
 *  A copy of the license is included in the section entitled "GNU
 *  Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ConversationItem {
	
	/** The id of the conversation */
	String id = null;
	
	/** The contact address of the partner in the conversation */
	String partnerContactAddress = null;
	
	/** The id of the item to be migrated */
	String itemId = null;
	
	/** The id of the item to be migrated */
	String itemVersion;
	
	/** The object to be migrated for state preservation */
	Object object = null;
	
	/** The mobility action */
	Action action = null;
	
	/** The role of the item to be migrated */
	MigrationSubject role;
	
	/** The platform of the addressee node */
	Platform platform;
	
	/** List of missing dependencies */
	ArrayList<Dependency> missingDependencies;
	
	/** Diffie-Hellman parameters */
	DHParameterSpec dhParameterSpec = null;
	
	/** Diffie-Hellman public-private keys pair */
	KeyPair keyPair = null;
	
	/** Diffie-Hellman agreement */
	KeyAgreement keyAgreement = null;
	
	/** Diffie-Hellman public key */
	byte[] encodedPublicKey = null;
	
	/** The secret used for encryption/decryption */
	SecretKey secret = null;
	
	/** The encryption algorithm and its operation mode to be used */
	EncryptionAlgorithm encryptionAlgorithm;

	public ConversationItem(String id, String partnerContactAddress, String itemId, Object object, String itemVersion, Action action, MigrationSubject role, Platform platform, EncryptionAlgorithm encryptionAlgorithm) {
		setId(id);
		setPartnerContactAddress(partnerContactAddress);
		setItemId(itemId);
		setItemVersion(itemVersion);
		setObject(object);
		setAction(action);
		setRole(role);
		setPlatform(platform);
		missingDependencies = new ArrayList<Dependency>();
		setEncryptionAlgorithm(encryptionAlgorithm);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartnerContactAddress() {
		return partnerContactAddress;
	}

	public void setPartnerContactAddress(String partnerContactAddress) {
		this.partnerContactAddress = partnerContactAddress;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemVersion() {
		return itemVersion;
	}

	public void setItemVersion(String itemVersion) {
		this.itemVersion = itemVersion;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public MigrationSubject getRole() {
		return role;
	}

	public void setRole(MigrationSubject role) {
		this.role = role;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	/**
	 * Method to store the list of missing dependencies.
	 * 
	 * @param list
	 *            The list of missing dependencies
	 */
	public void addMissingDependencies(ArrayList<Dependency> list) {
		for (Dependency dependency : list) {
			boolean found = false;
			for (Dependency dependencyInList : this.missingDependencies) {
				if (dependency.getId().equals(dependencyInList.getId())) {
					found = true;
					break;
				}
			}
			if(!found)
				this.missingDependencies.add(dependency);
		}
	}
	
	/**
	 * Method to get a {@link Dependency} from the list of missing ones.
	 * 
	 * @param id
	 *            The id of the requested {@link Dependency}
	 * 
	 * @return a {@link Dependency} from the list of missing ones
	 */
	public Dependency getMissingDependency(String id) {
		for (Dependency dependencyInList : this.missingDependencies) {
			if (dependencyInList.getId().equals(id)) {
				return dependencyInList;
			}
		}
		return null;
	}
	
	/**
	 * Method to remove a {@link Dependency} from the list of missing ones.
	 * 
	 * @param id
	 *            The id of the {@link Dependency} to be removed
	 */
	public void removeMissingDependency(String id) {
		for (Dependency dependencyInList : this.missingDependencies) {
			if (dependencyInList.getId().equals(id)) {
				this.missingDependencies.remove(dependencyInList);
				break;
			}
		}
	}
	
	/**
	 * Method to get the number of missing dependencies.
	 * 
	 * @return the number of missing dependencies
	 */
	public int getMissingDependenciesSize() {
		return this.missingDependencies.size();
	}

	public ArrayList<Dependency> getMissingDependencies() {
		return missingDependencies;
	}

	public void setMissingDependencies(ArrayList<Dependency> missingDependencies) {
		this.missingDependencies = missingDependencies;
	}

	public DHParameterSpec getDhParameterSpec() {
		return dhParameterSpec;
	}

	public void setDhParameterSpec(DHParameterSpec dhParameterSpec) {
		this.dhParameterSpec = dhParameterSpec;
	}

	public KeyPair getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public KeyAgreement getKeyAgreement() {
		return keyAgreement;
	}

	public void setKeyAgreement(KeyAgreement keyAgreement) {
		this.keyAgreement = keyAgreement;
	}

	public byte[] getEncodedPublicKey() {
		return encodedPublicKey;
	}

	public void setEncodedPublicKey(byte[] encodedPublicKey) {
		this.encodedPublicKey = encodedPublicKey;
	}

	public SecretKey getSecret() {
		return secret;
	}

	public void setSecret(SecretKey secret) {
		this.secret = secret;
	}

	public EncryptionAlgorithm getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}
}
