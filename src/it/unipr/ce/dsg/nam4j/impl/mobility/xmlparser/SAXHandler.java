package it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser;

import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * The SAX Events Handler for jar or dex dependencies.
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

public class SAXHandler extends DefaultHandler {

	private LibraryInformation libraryInformation = new LibraryInformation();
	private List<Dependency> dependencyList = new ArrayList<Dependency>();
	private Dependency dependency = null;
	private FunctionalModuleForService functionalModuleForService = null;
	private String content = null;

	public LibraryInformation getLibraryInformation() {
		return libraryInformation;
	}
	
	public List<Dependency> getDependencyList() {
		return dependencyList;
	}
	
	public FunctionalModuleForService getFunctionalModuleForService() {
		return functionalModuleForService;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		switch (qName) {
		
		// Create a new LibraryInformation object when the opening tag is found
		case "info":
			libraryInformation = new LibraryInformation();
			break;
		
			// Create a new object representing the functional module to which the migrated service has to be added
		case "functional_module":
			functionalModuleForService = new FunctionalModuleForService();
			break;
			
		// Create a new Dependency object when the opening tag is found
		case "dependency":
			dependency = new Dependency();
			break;
		
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		switch (qName) {
		
		// ******************* General info *******************
		
		case "id":
			libraryInformation.setId(content);
			break;
			
		case "version":
			libraryInformation.setVersion(content);
			break;
			
		case "type":
			libraryInformation.setType(content);
			break;
			
		case "main":
			libraryInformation.setMainClass(content);
			break;
			
		// ******************* The FM to which a migrating Service has to be linked *******************
			
		case "functional_module":
			Dependency d = new Dependency();
			d.setId(functionalModuleForService.getId());
			d.setVersion(functionalModuleForService.getVersion());
			dependencyList.add(d);
			
			Dependency dInfoFile = new Dependency();
			dInfoFile.setId(functionalModuleForService.getId()+ MobilityUtils.INFO_FILE_EXTENSION);
			dInfoFile.setVersion("-1"); // Version -1 is used to mark the item as an xml info file and not as a library
			dependencyList.add(dInfoFile);
			
			break;
		
		case "functional_module_id":
			functionalModuleForService.setId(content);
			break;
			
		case "functional_module_version":
			functionalModuleForService.setVersion(content);
			break;
		
		// ******************* Dependencies *******************
			
		// Add the dependency to list once closing tag is found
		case "dependency":
			dependencyList.add(dependency);
			break;
			
		// For all other closing tags update the dependency
		
		case "dependency_id":
			dependency.setId(content);
			break;
			
		case "dependency_version":
			dependency.setVersion(content);
			break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		content = String.copyValueOf(ch, start, length).trim();
	}
	
}