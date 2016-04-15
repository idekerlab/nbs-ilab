package main.java.iLab.service;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import main.java.iLab.domain.Instance;
import main.java.iLab.domain.Project;

/**
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

public class InstanceService {
	
	private static boolean[] inUseFlags = {false,false};

	/**
	 * @param fileName
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public Instance getInstanceDetails(String fileName)
			throws InvalidPropertiesFormatException, IOException {
    	
		Properties properties = new Properties();
		properties.loadFromXML(Instance.class.getResourceAsStream(fileName));
		
		// Set Instance instance from XML data.
    	
    	Instance instance = new Instance();
    	instance.setLogger_file(properties.getProperty("logger_file"));
		instance.setParams_file(properties.getProperty("params_file"));
		instance.setProcess_str(properties.getProperty("process_str"));
		instance.setProcess_env(properties.getProperty("process_env"));
		
		return instance;
	}
	
	/**
	 * @return boolean (has an available instance)
	 */
	public static boolean hasAvailableInstance() {
		for(int i=0; i<inUseFlags.length;i++) {
			if(!inUseFlags[i]) {
				return true;
			}
	     }
	     return false;
	}


	/**
	 * @return next available instance
	 */
	public static int getNextInstance() {
		for(int i=0; i<inUseFlags.length;i++) {
			if(!inUseFlags[i]) {
				inUseFlags[i] = true;
	            return i;
			}
	     }
	     return -1;
	}

	/**
	 * @param i instance to reset to available.
	 */
	public static void clearInstance(int i) {
		if(i >=0 && i<inUseFlags.length)
			inUseFlags[i]=false;
	}
	
	/**
	 * runInstance - Run instance.
	 * @param instance
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	
	public String runInstance(final Instance instance, Project inProject)
			throws IOException, InterruptedException {
	
		instance.setInstanceIndex(InstanceService.getNextInstance());
		String results = instance.execute(inProject);
		InstanceService.clearInstance(instance.getInstanceIndex());
		return results;
	}
}

