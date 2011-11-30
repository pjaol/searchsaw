package com.pjaol.oji.utils;

public class TimerThread extends Thread {

	private static boolean isWindows;
	private static String osName;
	private int time_elapsed = 0;
	private long timer_length;
	private Boolean stop = false;
	private final Object lock = new Object();
	private String name; 
	
	static {
		if (osName == null) {
			osName = System.getProperty("os.name");
			if (osName.startsWith("Windows"))
				isWindows = true;
		}

	}

	// windows x86 has a 10 ms granularity when it comes to clock accuracy
	protected static int sleep_rate = (isWindows) ? 10 : 3;

	public TimerThread(long timer_length, String name) {

		this.timer_length = timer_length;
		this.name = name;
	}

	@Override
	public void run() {

		// Keep looping
		while (true) {
			
			synchronized (lock) {
				if (stop){
					break;
				}
			}
			// Put the timer to sleep
			try {
				Thread.sleep(sleep_rate);
			} catch (InterruptedException ioe) {
				continue;
			}

			// Use 'synchronized' to prevent conflicts
			synchronized (this) {
				// Increment time remaining
				time_elapsed += sleep_rate;

				// Check to see if the time has been exceeded
				if (time_elapsed > timer_length) {
					// Trigger a timeout
					timeout();
				}
			}

		}

	}

	public void timeout() {
		this.interrupt();
		System.out.println("Timed out :" + name);
	}
	
	public void halt(){
		synchronized (lock) {
			stop = true;
		}
		
	}
	
	public static int getSleepTime(){
		return sleep_rate;
	}
	
	public int getElapsedTime(){
		return time_elapsed;
	}
}
