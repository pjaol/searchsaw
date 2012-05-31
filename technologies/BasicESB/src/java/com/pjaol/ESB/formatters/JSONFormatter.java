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
package com.pjaol.ESB.formatters;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
			} else if (v instanceof Map){
				jo.put(k, recurseMap((Map)v));
			}else{
				jo.put(k, v);
			}
			jarr.add(jo);
			
		}
		
		return jarr;
	}

	
	private JSONObject recurseMap(Map items){
		JSONObject jo = new JSONObject();
		
		Set keys = items.keySet();
		Iterator i = keys.iterator();
		while(i.hasNext()){
		
			String k = (String)i.next();
			Object v = items.get(k);
			
			if (v instanceof NamedList){
				jo.put(k, recurseNamedList((NamedList)v));
			} else if (v instanceof Map){
				jo.put(k, recurseMap((Map)v));
			}else{
				jo.put(k, v);
			}	
		}
		
		return jo;
		
	}
}
