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
