package org.langke.common;

import java.io.Serializable;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class StrIntBag implements Serializable {
	private static final long serialVersionUID = 3454388888654757L;
	public String _str;
	public int _int;

	public StringPair toStringPair() {
		return new StringPair(_str, _int + "");
	}

	public StrIntBag() {
	}

	public StrIntBag(String name, int value) {
		this._str = name;
		this._int = value;
	}

	public static StringPair[] toStringPairs(StrIntBag[] strIntBags) {
		if (strIntBags == null) {
			return null;
		}
		StringPair[] strPairs = new StringPair[strIntBags.length];
		for (int i = 0; i < strIntBags.length; i++) {
			strPairs[i] = strIntBags[i].toStringPair();
		}
		return strPairs;
	}

	@Override
	public String toString() {
		return _str + ":" + _int;
	}
	
}
