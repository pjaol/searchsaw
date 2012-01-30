package com.pjaol.ESB.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.common.util.NamedList;

public class HTTPParamsParser {

	
		public static NamedList paramsToNamedList(HttpServletRequest req){
			NamedList result = new NamedList();
			Enumeration<String> enumeration = req.getParameterNames();
			while(enumeration.hasMoreElements()){
				String k = enumeration.nextElement(); 
				String[] v = req.getParameterValues(k);
				
				if (v.length > 1){
					result.add(k, v);
				} else if (v.length == 1) {
					result.add(k, v[0]);
				}
					
			}
			return result;
		}
}
