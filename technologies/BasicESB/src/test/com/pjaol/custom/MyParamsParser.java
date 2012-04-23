package com.pjaol.custom;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.web.HTTPParamsParser;

public class MyParamsParser extends HTTPParamsParser {

	
	/**
	 * Parse the URL /MyObject/123/getName
	 * and return /MyObject/getName
	 */
	@Override
	public String getPath(HttpServletRequest req) {
		String[] parts = getParts(req.getPathInfo());
		String path = parts[0]+"/"+parts[2];	
		return path;
	}
	
	/**
	 * get parameters as normal, but add path info to the args
	 * e.g.
	 *   /MyObject/123/getName add object id 123 to the parameters
	 */
	@Override
	public NamedList paramsToNamedList(HttpServletRequest req) {
		NamedList result = super.paramsToNamedList(req); // call original super method
		String[] parts = getParts(req.getPathInfo());
		result.add("objectid", parts[1]);
		return result;
	}
	
	private String[] getParts (String path){
		return path.split("/");
	}
	
}
