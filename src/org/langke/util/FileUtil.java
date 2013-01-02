package org.langke.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class FileUtil {

	public static BufferedReader getReader(String folder, String fileName) throws IOException {
		return getReader(new File(folder, fileName), "utf8");
	}

	public static BufferedReader getReader(String path) throws IOException {
		return getReader(new File(path));
	}

	public static BufferedReader getReader(URL path) throws IOException {
		return new BufferedReader(new InputStreamReader(path.openConnection().getInputStream()));
	}

	public static BufferedReader getReader(File file) throws IOException {
		return getReader(file, "utf8");
	}

	public static BufferedWriter getWriter(String folder, String fileName) throws IOException {
		return getWriter(new File(folder, fileName), "utf8");
	}

	public static BufferedWriter getWriter(String path) throws IOException {
		return getWriter(new File(path), "utf8");
	}

	public static BufferedWriter getWriter(File file) throws IOException {
		return getWriter(file, "utf8");
	}

	public static int lineCount(File file) throws IOException {
		BufferedReader reader = getReader(file);
		int count = 0;
		while (reader.readLine() != null) {
			count++;
		}
		return count;
	}

	public static BufferedWriter getWriter(File file, String encode) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encode));
	}

	public static BufferedReader getReader(File file, String encode) throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), encode));
	}
	
	public static boolean createNewFile(String path){
		try {
			new File(path).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void move(String filepath,String dirpath){
		move(new File(filepath),dirpath);
	}
	public static void move(File file,String dirpath){
		File newfile = new File(dirpath,file.getName());
		if(!newfile.exists())
			file.renameTo(newfile);
		else
			file.delete();
	}

	public static void delete(String filepath) {
		File file = new File(filepath);
		if(file.exists())
			file.delete();
	}

	public static void closeReader(Reader reader) {
		try {
			if(reader!=null)
				reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        if(in!=null)
        	in.close();
        if(out!=null)
        	out.close();
    }
	public static void copy(InputStream in, File dst) throws IOException {
		OutputStream out = new FileOutputStream(dst);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
        if(out!=null)
        	out.close();
	}

}
