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
package com.pjaol.ESB.Mock;

import java.util.Map;
import java.util.Random;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.core.Module;

public class MockModuleCatchTimout extends Module {
	
	Random rand = new Random();

	@Override
	public NamedList process(NamedList input) {
		NamedList result = new NamedList();
		int rst = rand.nextInt(200);
		
		result.add("randomTime", rst);
		System.out.println(getName()+" running with input: "+ input+" sleeping for "+ rst +"ms");
		try {
			Thread.sleep(rst);
		} catch (InterruptedException e) {
			System.out.println(getName()+" Caught exception continuing sleeping");
			try {
				Thread.sleep(rst);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return result;
	}



	@Override
	public void initializeMonitor() {}

	@Override
	public void init(Map<String, String> args) {
		// TODO Auto-generated method stub
		
	}

}
