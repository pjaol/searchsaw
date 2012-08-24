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
package com.pjaol.ESB.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicESBVariables {
	
	public static final String basicESBHomeProperty = "basicesb.home";
	
	private static final Pattern envReplacer = Pattern.compile("\\$\\{(.+?)\\}");
	
	public static String populateQuery(String query) {

		Matcher m = envReplacer.matcher(query);
		StringBuffer result = new StringBuffer();
		while (m.find()) {
			String variable = m.group(1);
			// pull it from a property
			String value = System.getProperty(variable);
			if (value == null){
				// failing that an env
				value = System.getenv(variable);
			}
			if (value == null){
				// failing that from esb globals
				if (ESBCore.getInstance().getGlobals().containsKey(variable)){
					value = ESBCore.getInstance().getGlobals().get(variable);
				}
			}
			
			// sub with value or empty string if nothing found
			if (value != null){
				m.appendReplacement(result, value);
			} else {
				m.appendReplacement(result, "");
			}
		}
		m.appendTail(result);

		return result.toString();
	}

}
