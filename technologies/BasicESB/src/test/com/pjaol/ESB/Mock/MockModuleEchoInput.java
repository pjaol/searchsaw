package com.pjaol.ESB.Mock;

import java.util.Map;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.core.Module;

public class MockModuleEchoInput extends Module{

	@Override
	public void initializeMonitor() {
	
		
	}

	@Override
	public NamedList process(NamedList input) throws Exception {
		NamedList result = new NamedList();
		int sz = input.size();
		
		for (int i = 0; i <sz ; i++){
			String k = input.getName(i);
			result.add("echo'd-"+k, input.get(k));
		}
		
		return result;
	}

	@Override
	public void init(Map<String, String> args) {
		
	}

}
