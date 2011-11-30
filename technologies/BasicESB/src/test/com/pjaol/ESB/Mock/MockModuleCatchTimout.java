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
