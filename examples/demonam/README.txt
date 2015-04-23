
DEMO:

1) run the bootstrap server (runBootstrap.sh in s2pChord)

2) from the logs, get the IP address of the bootstrap server and put it into 
config/chordPeer.cfg:
bootstrap_peer=bootstrap@ip.add.re.ss:5080

3) run the DemoNam

X - publish "Temperature x"  where x is a random room among (bedroom, kitchen, livingroom, bathroom)
X - search for "Temperature x"  where x is a random room among (bedroom, kitchen, livingroom, bathroom)


** General concepts about context events (NAM theory) and resource descriptors (s2pChord library) **

Structure of a context event:
	String id 
	String name 
	String timestamp 
	String temporalValidity 
	String producerId 
	Parameter subject 
	Parameter action
	Parameter object 
	Parameter location 
	
where Parameter has the following public attributes
	String id 
	String name 
	String value 
	
An example of context event is TemperatureNotification, used in ProvideTemperatureRunnable:

		Temperature temperature = new Temperature();
		temperature.setId("i21");
		temperature.setValue("20");
		TemperatureNotification tempNotif = new TemperatureNotification();
		Room room = new Room();
		room.setValue("bedroom");
		tempNotif.setLocation(room);
		tempNotif.setSubject(temperature);
		
A context event Java object is serialized into a JSON string, 
which is the mean for moving the context event from one functional module to another,
and also the payload of the s2pChord Resource.		
		
The s2pChord ResourceDescriptor for a context event contains 4 parameters, 
one for each Paramater of the context event.
If a Parameter of a context event is not set, 
the corresponding parameter of the ResourceDescriptor is left empty.

Example:

		ResourceDescriptor rd = new ResourceDescriptor();
		rd.setType("TemperatureNotification"); // type of resource
		rd.setResourceOwner(cp.getMyNetPeerInfo());
		rd.addParameter(new ResourceParameter("Subject", "Temperature"));
		rd.addParameter(new ResourceParameter("Action", ""));
		rd.addParameter(new ResourceParameter("Object", ""));
		rd.addParameter(new ResourceParameter("Location", "bedroom"));

In the publish process, the key obtained from the ResourceDescriptor 
AND the payload (i.e. the JSON string) are published.
In the lookup process, the key obtained from the ResourceDescriptor is used 
to find the published context event.

NOTE: the parameters of the ResourceDescriptor only contain the names of the Paramaters 
of the context event. The values of such paramaters, if any, are in thes payload.








