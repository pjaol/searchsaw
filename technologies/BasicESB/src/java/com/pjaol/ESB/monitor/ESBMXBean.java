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
