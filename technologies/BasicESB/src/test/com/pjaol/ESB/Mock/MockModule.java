package com.pjaol.ESB.Mock;

import java.util.Map;
import java.util.Random;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.core.Module;

public class MockModule extends Module {
	
	Random rand = new Random();

	@Override
	public NamedList process(NamedList input) {
		NamedList result = new NamedList();
		int rst = rand.nextInt(100);
		
		result.add("randomTime", rst);
		System.out.println(getName()+" running with input: "+ input+" sleeping for "+ rst +"ms");
		try {
			Thread.sleep(rst);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public void init(Map args) {
		// TODO Auto-generated method stub
		
	}

}
