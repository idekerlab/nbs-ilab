/**
 * 
 */
package main.java.iLab.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.apache.commons.io.IOUtils;

/**
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

public class MyFileUtils {
	
	/**
	 * @param fileIS
	 * @param fileName
	 * @return File object
	 * @throws IOException
	 */
	public static File storeFile(
					InputStream fileIS,
					String 		location) throws IOException {		
		
		File file = MyFileUtils.writeInputStreamToTempFile(fileIS, location);
		file.deleteOnExit();
		return file;
	}
	
	/**
	 * @param fileIS
	 * @param fileName
	 * @return File object
	 * @throws IOException
	 */
	public static File writeFile(
					final String data,
					final String location) throws IOException {		
		
		File file = new File(location);
		file.setWritable(true);
		
		FileWriter fw = new FileWriter(file);
		fw.write(data);
		fw.close();

		return file;
	}
	
	/**
	 * check directory items for maxItems and delete contents if necessary
	 * @param destinationDir
	 * @param maxNumItems
	 * @return
	 */
	public static Boolean deleteDirectoryContents(
			File destinationDir, 
			Integer maxNumItems) {
		
		if( destinationDir.exists() ) {
			if( destinationDir.list().length > maxNumItems ) {
				for (File f: destinationDir.listFiles()) {
					f.delete();
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param fileIS
	 * @param fileName
	 * @return File object
	 * @throws IOException
	 */
	static private File writeInputStreamToTempFile(
					InputStream fileIS,
					String 		location) throws IOException{
		
		File file = new File(location);
		file.setWritable(true);
		
		try {			
			FileOutputStream fileOS = new FileOutputStream(file);
			IOUtils.copy(fileIS, fileOS);
			fileOS.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		return file;
	}
	
	/**
	 * @param prefix
	 * @param extension
	 * @return
	 */
	public static String createRandomFileName(
					String prefix,
					String extension){

		SimpleDateFormat 	sdf = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss-SSS");
		Random 				ran = new java.util.Random();
		
		return prefix + sdf.format(new Date()) + "_" + String.valueOf(ran.nextInt(9999)) + "." + extension;
	}
	
}
