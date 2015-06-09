package it.unipr.ce.dsg.nam4j.impl.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamLogger {

	private Logger logger;
	
	/** Turn logging on or off */
	private final boolean ENABLED = true;

	public NamLogger(String generatorClass) {
		logger = LogManager.getLogger(generatorClass);
	}
	
	public void info(String message) {
		if(ENABLED)
			logger.info(message);
	}
	
	public void debug(String message) {
		if(ENABLED)
			logger.debug(message);
	}
	
	public void error(String message) {
		if(ENABLED)
			logger.error(message);
	}
	
	public void warn(String message) {
		if(ENABLED)
			logger.warn(message);
	}
	
}
