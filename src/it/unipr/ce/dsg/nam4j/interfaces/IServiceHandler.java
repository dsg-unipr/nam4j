package it.unipr.ce.dsg.nam4j.interfaces;

public interface IServiceHandler {
	
	public IFunctionalModule getAssociatedFunctionalModule();
	
	public void setAssociatedFunctionalModule(IFunctionalModule functionalModule);
	
	public String getAddress();
	
	public void setAddress(String address);
	
	public int addRemoteServiceHandler(IServiceHandler remoteServiceHandler);
	
	public int removeRemoteServiceHandler(IServiceHandler remoteServiceHandler);
	
	public int deliverServiceRequest(IServiceRequest serviceRequest);

}
