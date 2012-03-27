/*******************************************************************************
 * Copyright 2012 Patrick O'Leary
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pjaol.oji.utils;

public class TimerThread extends Thread {

	private static boolean isWindows;
	private static String osName;
	private int time_elapsed = 0;
	private long timer_length;
	private Boolean stop = false;
	private final Object lock = new Object();
	private String name; 
	private String type;
	static {
		if (osName == null) {
			osName = System.getProperty("os.name");
			if (osName.startsWith("Windows"))
				isWindows = true;
		}

	}

	// windows x86 has a 10 ms granularity when it comes to clock accuracy
	protected static int sleep_rate = (isWindows) ? 10 : 3;

	public TimerThread(long timer_length, String name, String type) {

		this.timer_length = timer_length;
		this.name = name;
		this.type = type;
		setName(name);
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
		System.out.println("Timed out "+type+" :" + name);
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
