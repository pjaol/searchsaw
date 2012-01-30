package com.pjaol.ESB.formatters;

import org.apache.solr.common.util.NamedList;

public abstract class Formatter {
	
	public abstract String toOutput(@SuppressWarnings("rawtypes") NamedList output);

}
