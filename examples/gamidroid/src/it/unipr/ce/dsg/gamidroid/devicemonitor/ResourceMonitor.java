package it.unipr.ce.dsg.gamidroid.devicemonitor;

import java.io.Serializable;

/**
 * Class which describes the resources of the device.
 */
public class ResourceMonitor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	float usedCpuPerc;
	float availableMem;
	float totalMem;

	public float getUsedCpuPerc() {
		return usedCpuPerc;
	}

	public void setUsedCpuPerc(float usedCpuPerc) {
		this.usedCpuPerc = usedCpuPerc;
	}

	public float getAvailableMem() {
		return availableMem;
	}

	public void setAvailableMem(float availableMem) {
		this.availableMem = availableMem;
	}

	public float getTotalMem() {
		return totalMem;
	}

	public void setTotalMem(float totalMem) {
		this.totalMem = totalMem;
	}

	public ResourceMonitor(float cpuPerc, float avMem, float totMem) {
		setUsedCpuPerc(cpuPerc);
		setAvailableMem(avMem);
		setTotalMem(totMem);
	}

}