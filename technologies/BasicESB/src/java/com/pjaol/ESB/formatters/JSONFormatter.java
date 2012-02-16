package com.pjaol.ESB.formatters;

import org.apache.solr.common.util.NamedList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONFormatter extends Formatter {

	@Override
	public String toOutput(NamedList output) {

		return iterateNamedList(output);
	}
	
	
	private String iterateNamedList(NamedList output){
		JSONObject jo = new JSONObject();
		int sz = output.size();
		
		for(int i =0; i< sz; i++){
			String k = output.getName(i);
			
			Object v = output.getVal(i);
			if (v instanceof NamedList){
				
				jo.put(k, recurseNamedList((NamedList)v));
			} else {
				jo.put(k, v);
			}
		}
		
		return jo.toJSONString();
	}
	
	private JSONArray recurseNamedList(NamedList output){
		
		
		int sz = output.size();
		JSONArray jarr = new JSONArray();
		
		for(int i =0; i< sz; i++){
			JSONObject jo = new JSONObject();
			
			String k = output.getName(i);
				
			Object v = output.getVal(i);
			if (v instanceof NamedList){
				
				jo.put(k, recurseNamedList((NamedList)v));
			} else {
				jo.put(k, v);
			}
			jarr.add(jo);
			
		}
		
		return jarr;
	}

}
