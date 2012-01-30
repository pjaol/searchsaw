package com.pjaol.ESB.formatters;

import org.apache.solr.common.util.NamedList;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONFormatter extends Formatter {

	@Override
	public String toOutput(NamedList output) {

		String jsonText = JSONValue.toJSONString(output);
		return jsonText;
	}

}
