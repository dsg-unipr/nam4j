package it.unipr.ce.dsg.nam4j.interfaces;

public interface IServiceRequest extends IService {
	
	/**
	 * 
	 * @return name of the NAM that requests the service
	 */
	public String getRequestorName();
	
	/**
	 * Set the name of the NAM that requests the service
	 * @param requestorName
	 */
	public void setRequestorName(String requestorName);
	
	/**
	 * 
	 * @return address (IP:PORT) of the NAM that requests the service
	 */
	public String getRequestorAddress();
	
	/**
	 * Set the address (IP:PORT) of the NAM that requests the service
	 * @param requestorAddress
	 */
	public void setRequestorAddress(String requestorAddress);
}
