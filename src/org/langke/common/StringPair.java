package org.langke.common;

import java.io.*;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class StringPair implements Serializable {
	private static final long serialVersionUID = 3454234325353654757L;
	private String name;
	private String value;

	public StringPair() {
	}

	public StringPair(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
