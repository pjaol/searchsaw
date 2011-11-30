package com.pjaol.ESB.monitor;

public interface ESBMXBean {

	public abstract String getName();

	public abstract String getType();

	// should these be synced on total & cardinal?
	// deciding to not block setting for reporting
	public abstract double getAverage();

	public abstract int getTotal();

	public abstract int getCardinal();

	public abstract int getMax();

	public abstract String getMetric();

}