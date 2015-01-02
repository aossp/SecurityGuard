package com.beiluoshimen.securityguard.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;
/**
 * @param zipfile = string path to zip wanted to be unzip
 * @param location = the folder where zip will be extracted to.
 * 
 * @author Hsieh Yu-Hua
 * @date Jan 3, 201512:06:52 AM
 */
public class ZipTools {
	private final static String TAG = "ZipTools";
	public static void unzip(String zipFile, String location) throws IOException {
		int BUFFER_SIZE = 1024;
		byte [] buffer = new byte[BUFFER_SIZE];
	    try {
	        File f = new File(location);
	        if(!f.isDirectory() || !f.exists()) {
	            f.mkdirs();
	        }
	        ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
	        try {
	            ZipEntry ze = null;
	            while ((ze = zin.getNextEntry()) != null) {
	                String path = location + ze.getName();

	                if (ze.isDirectory()) {
	                    File unzipFile = new File(path);
	                    if(!unzipFile.isDirectory()) {
	                        unzipFile.mkdirs();
	                    }
	                }
	                else {
	                    FileOutputStream fout = new FileOutputStream(path, false);
	                    try {
	                        for (int c = zin.read(); c != -1; c = zin.read(buffer)) {
	                            fout.write(buffer, 0, c);;
	                        }
	                        zin.closeEntry();
	                    }
	                    finally {
	                        fout.close();
	                    }
	                }
	            }
	        }
	        finally {
	            zin.close();
	        }
	    }
	    catch (Exception e) {
	        Log.e(TAG, "Unzip exception", e);
	    }
	}
}
