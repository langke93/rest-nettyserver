package org.langke.util;

import org.apache.commons.codec.digest.DigestUtils;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class EncreptUtil {

	public static String md5(String input){
		if(input != null){
			return DigestUtils.md5Hex(input);
		}
		return null;
	}

	public static void main(String[] args){
		String data = "_T_ONLINE_K_W无线网卡";
		System.out.println(md5(data));
	}
}
