package com.pjaol.ESB.Mock;

import java.util.Map;
import java.util.Random;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.core.Module;

public class MockModule extends Module {
	
	Random rand = new Random();

	@Override
	public NamedList process(NamedList input) throws Exception {
		NamedList result = new NamedList();
		int rst = rand.nextInt(100);
		
		result.add("randomTime", rst);
		System.out.println(getName()+" running with input: "+ input+" sleeping for "+ rst +"ms");
		
		Thread.sleep(rst);
		
		
		return result;
	}

	

	@Override
	public void initializeMonitor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Map<String, String> args) {
		// TODO Auto-generated method stub
		
	}

}
