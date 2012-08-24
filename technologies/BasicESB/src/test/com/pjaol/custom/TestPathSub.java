package com.pjaol.custom;

import org.junit.Test;

import com.pjaol.ESB.config.BasicESBVariables;

import junit.framework.TestCase;

public class TestPathSub extends TestCase{
	
	String besbhome = "/tra/la/la/la";
	String post = "/foo/lala";
	String pre = "${basicesb.home}";

	@Override
	protected void setUp() throws Exception {
	
		System.setProperty("basicesb.home", besbhome );
	}
	
	@Test
	public void testPathSubstitution(){
		
		String path = pre + post;
		String newPath = BasicESBVariables.populateQuery(path);
		assertEquals("populateQuery fail",besbhome+post, newPath);
	}

	@Test
	public void testPathInvalidSubstitution(){
		// should just return the post value
		String path = "${lalala}" + post;
		String newPath = BasicESBVariables.populateQuery(path);
		assertEquals("populateQuery fail",post, newPath);
	}
	
	@Test
	public void testPathNoSubstitution(){
		// should just return the post value
		String path = post;
		String newPath = BasicESBVariables.populateQuery(path);
		
		assertEquals("populateQuery fail",post, newPath);
	}
}
