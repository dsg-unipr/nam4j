package it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser;

/**
 * Class representing a dependency for a jar or dex.
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class Dependency {
	
	private String type;
	private String version;
	private String id;
	
	public Dependency() {}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
}