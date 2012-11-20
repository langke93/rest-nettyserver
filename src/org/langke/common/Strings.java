package org.langke.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class Strings {

	public static int[] toIntArray(String[] array){
		int[] arrayInt = new int[array.length];
		for(int i=0;i<array.length;i++){
			arrayInt[i] = Integer.parseInt(array[i]);
		}
		return arrayInt;
	}
	
	public static long[] toLongArray(String[] array){
		long[] arrayLong = new long[array.length];
		for(int i=0;i<array.length;i++){
			arrayLong[i] = Long.parseLong(array[i]);
		}
		return arrayLong;
	}
	
	public static double[] toDoubleArray(String[] array){
		double[] arrayDouble = new double[array.length];
		for(int i = 0; i < array.length; i++){
			arrayDouble[i] = Double.parseDouble(array[i]);
		}
		return arrayDouble;
	}
	

	public static String toStr(long[] ls) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<ls.length;i++){
			sb.append(String.valueOf(ls[i]));
			if(i < ls.length-1){
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static StringBuilder quoteSafeJson(StringBuilder sb, String string) {
		return sb.append("\"").append(string).append("\"");
	}

	public static String quote(String string) {

		if (string == null || string.length() == 0) {
			return "\"\"";
		}

		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len * 2);
		String t;
		char[] chars = string.toCharArray();
		char[] buffer = new char[1030];
		int bufferIndex = 0;
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			if (bufferIndex > 1024) {
				sb.append(buffer, 0, bufferIndex);
				bufferIndex = 0;
			}
			b = c;
			c = chars[i];
			switch (c) {
			case '\\':
			case '"':
				buffer[bufferIndex++] = '\\';
				buffer[bufferIndex++] = c;
				break;
			case '/':
				if (b == '<') {
					buffer[bufferIndex++] = '\\';
				}
				buffer[bufferIndex++] = c;
				break;
			default:
				if (c < ' ') {
					switch (c) {
					case '\b':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'b';
						break;
					case '\t':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 't';
						break;
					case '\n':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'n';
						break;
					case '\f':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'f';
						break;
					case '\r':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'r';
						break;
					default:
						t = "000" + Integer.toHexString(c);
						int tLength = t.length();
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'u';
						buffer[bufferIndex++] = t.charAt(tLength - 4);
						buffer[bufferIndex++] = t.charAt(tLength - 3);
						buffer[bufferIndex++] = t.charAt(tLength - 2);
						buffer[bufferIndex++] = t.charAt(tLength - 1);
					}
				} else {
					buffer[bufferIndex++] = c;
				}
			}
		}
		sb.append(buffer, 0, bufferIndex);
		sb.append('"');
		return sb.toString();
	}

	public static StringBuilder quoteJson(StringBuilder sb, String s){
		return sb.append(quote(s));
	}
	
	public static final void trimEndComma(StringBuilder sb) {
		int l = sb.length();
		if (sb.charAt(l - 1) == ',') {
			sb.setLength(l - 1);
		}
	}
	public static final void trimEnd(StringBuilder sb) {
		int l = sb.length();
		sb.setLength(l - 1);
	}
	public static String arrayToString(String[] values) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < values.length; i++){
			sb.append(values[i]);
			if(i != values.length-1){
				sb.append(",");
			}
		}
		return sb.toString();
	}
	public static final void arrayToString(StringBuilder sb, String name, Object[] os) {
		sb.append(name).append(":");
		if (os == null) {
			sb.append(" null \n");
			return;
		}
		sb.append("[");
		for (int i=0;i<os.length;i++) {
			sb.append(os[i]);
			if(i < os.length-1){
				sb.append(",");
			}
		}
		sb.append("]\n");
	}

	public static final String throwableToString(Throwable t) {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		PrintWriter p = new PrintWriter(ba);
		t.printStackTrace(p);
		p.flush();
		return ba.toString();
	}

	public static String[] splitTwo(String s, char split) {
		if (s == null) {
			return new String[] { s };
		}
		int i = s.indexOf(split);
		if (i < 0) {
			return new String[] { s };
		}
		String[] r = new String[2];
		r[0] = s.substring(0, i);
		r[1] = s.substring(i + 1);
		return r;

	}

	/**
	 * Converts some important chars (int) to the corresponding html string
	 */
	static String conv2Html(int i) {
		if (i == '&')
			return "&amp;";
		else if (i == '<')
			return "&lt;";
		else if (i == '>')
			return "&gt;";
		else if (i == '"')
			return "&quot;";
		else
			return "" + (char) i;
	}

	public final static void exec(String command, String dir, StringBuilder ret) {
		final String[] COMMAND_INTERPRETER = { "/bin/sh", "-c" };
		final long MAX_PROCESS_RUNNING_TIME = 30 * 1000; // 30 seconds

		String[] comm = new String[3];
		comm[0] = COMMAND_INTERPRETER[0];
		comm[1] = COMMAND_INTERPRETER[1];
		comm[2] = command;
		long start = System.currentTimeMillis();
		try {
			// Start process
			Process ls_proc = Runtime.getRuntime().exec(comm, null,
					new File(dir));
			// Get input and error streams
			BufferedInputStream ls_in = new BufferedInputStream(ls_proc
					.getInputStream());
			BufferedInputStream ls_err = new BufferedInputStream(ls_proc
					.getErrorStream());
			boolean end = false;
			while (!end) {
				int c = 0;
				while ((ls_err.available() > 0) && (++c <= 1000)) {
					ret.append(conv2Html(ls_err.read()));
				}
				c = 0;
				while ((ls_in.available() > 0) && (++c <= 1000)) {
					ret.append(conv2Html(ls_in.read()));
				}
				try {
					ls_proc.exitValue();
					// if the process has not finished, an exception is thrown
					// else
					while (ls_err.available() > 0)
						ret.append(conv2Html(ls_err.read()));
					while (ls_in.available() > 0)
						ret.append(conv2Html(ls_in.read()));
					end = true;
				} catch (IllegalThreadStateException ex) {
					// Process is running
				}
				// The process is not allowed to run longer than given time.
				if (System.currentTimeMillis() - start > MAX_PROCESS_RUNNING_TIME) {
					ls_proc.destroy();
					end = true;
					ret.append("!!!! Process has timed out, destroyed !!!!!");
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException ie) {
				}
			}
		} catch (IOException e) {
			ret.append("Error: " + e);
		}

	}

	public static void main(String[] args) {
		String json = "{\"title\":\"hello,\"jim\",haha\"}";
		System.out.println(json);
		System.out.println(quote(json));
	}

}
