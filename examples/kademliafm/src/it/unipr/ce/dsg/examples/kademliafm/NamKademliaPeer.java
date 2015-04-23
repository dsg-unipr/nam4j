package it.unipr.ce.dsg.examples.kademliafm;

import it.unipr.ce.dsg.s2p.example.peer.PeerConfig;
import it.unipr.ce.dsg.s2p.kademlia.KBucket;
import it.unipr.ce.dsg.s2p.kademlia.KademliaKey;
import it.unipr.ce.dsg.s2p.kademlia.KademliaPeer;
import it.unipr.ce.dsg.s2p.kademlia.KademliaResource;
import it.unipr.ce.dsg.s2p.kademlia.Triplet;
import it.unipr.ce.dsg.s2p.kademlia.eventlistener.KademliaEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class NamKademliaPeer extends KademliaPeer implements
KademliaEventListener {

	private final NamKademliaPeer kademliaPeer;

	public NamKademliaPeer(String pathConfig, String key, String name,
			int port, String certificatePath, boolean hasToLog) {
		super(pathConfig, key, name, port, certificatePath, hasToLog);

		kademliaPeer = this;

		/*
		 * Registering for the observer pattern so that when the required
		 * resource is received, this node gets notified
		 */
		kademliaPeer.addEventListener(kademliaPeer);

		// Ping the bootstrap to enter the network (the PeerConfig object is
		// used to parse the configuration file and get the boostrap
		// address)
		PeerConfig peerConfig = new PeerConfig(pathConfig);
		String bootstrapPeerAddress = peerConfig.bootstrap_peer;
		kademliaPeer.pingToPeer(bootstrapPeerAddress);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

				while(true) {
					System.out.println();
					System.out.println("***************************");
					System.out.println("*  p: send PING           *");
					System.out.println("*  f: find node           *");
					System.out.println("*  s: store a resource    *");
					System.out.println("*  k: show known nodes    *");
					System.out.println("*  r: show resources      *");
					System.out.println("*  e: export resource     *");
					System.out.println("*  v: find value          *");
					System.out.println("*  q: quit                *");
					System.out.println("***************************");
					System.out.print("Enter command: ");
					try {
						String choice = br.readLine().trim();
						System.out.println();
						if (choice.equals("p")) {
							System.out.print("Enter contact address: ");
							String ca = br.readLine().trim();
							kademliaPeer.pingToPeer(ca);				
						}

						if (choice.equals("f")) {
							System.out.print("Node to find: ");
							String key = br.readLine().trim();
							kademliaPeer.findNodeOrValue(new KademliaKey(key, 0), 'n');
						}

						if (choice.equals("s")) {
							System.out.print("Enter the file name: ");
							String fileName = br.readLine().trim();
							KademliaResource kr = new KademliaResource("", fileName);
							//DEBUG
							System.out.println("Key: "+kr.getResourceKey().getBitString());

							kademliaPeer.publishResource(kr);
						}

						if (choice.equals("k")) {
							System.out.println("This peer knows " + kademliaPeer.getKnownNodesNumber() + " nodes:");
							KBucket[] buckets = kademliaPeer.getBuckets();
							for(KBucket b : buckets) {
								ArrayList<Triplet> list = b.getTriplets();
								for(Triplet t : list) {
									System.out.println("-  " + t.getIpAddress() + ":" + t.getUdpPort());
								}
							}
						}

						if (choice.equals("r")) {
							Iterator<KademliaKey> iter = kademliaPeer.getResourceIterator();
							while(iter.hasNext()) {
								KademliaKey key = iter.next();
								KademliaResource kr = kademliaPeer.getResourceFromKey(key);
								System.out.println(kr.getFileName());
							}
						}

						if (choice.equals("e")) {
							System.out.println("Choose the resource to export as a file: ");
							int i=0;
							Iterator<KademliaKey> iter = kademliaPeer.getResourceIterator();
							while(iter.hasNext()) {
								KademliaKey key = iter.next();
								KademliaResource kr = kademliaPeer.getResourceFromKey(key);
								System.out.println(i + ".  " + kr.getFileName());
								i++;
							}
							System.out.print("\nResource number: ");
							int resNum = Integer.parseInt(br.readLine().trim());
							iter = kademliaPeer.getResourceIterator();
							for(int j=0; j<resNum; j++) {
								iter.next();
							}
							KademliaKey key = iter.next();
							KademliaResource res = kademliaPeer.getResourceFromKey(key);
							System.out.println("Enter new file name (no extension): ");
							String newFileName = br.readLine().trim();
							res.exportAsFile("", newFileName);
							System.out.println("The resource has been exported.");
						}

						if (choice.equals("v")) {
							System.out.print("Resource key: ");
							String bitString = br.readLine().trim();
							KademliaKey key = new KademliaKey(bitString, 0);
							kademliaPeer.findNodeOrValue(key, 'v');
						}

						if (choice.equals("q")) {
							br.close();
							System.exit(0);
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		});
		t.start();
	}

	/**
	 * The observer function that gets notified when the searched resource is
	 * received.
	 */
	@Override
	public void onReceivedSearchedResource(KademliaKey resourceKey,
			String fileName) {

		/* Getting the bytes of resource corresponding to the file */
		KademliaResource res = this.getResourceFromKey(resourceKey);
		byte[] bytes = res.getBytes();

		String item = new String(bytes);
		System.out.println("\nThe required resource has been received:\n"
				+ item + "\n");

		/*
		 * Creating a new file containing the received bytes converted to a
		 * String
		 */
		/*
		 * FileOutputStream fileOuputStream; try { fileOuputStream = new
		 * FileOutputStream(fileName); fileOuputStream.write(bytes);
		 * fileOuputStream.close(); } catch (FileNotFoundException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * File f = new File(fileName);
		 */
	}

}
