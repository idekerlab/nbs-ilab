package main.java.iLab.domain;

import main.java.iLab.utils.*;

import java.io.IOException;
import java.io.FileOutputStream;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

@XmlRootElement
public class Instance {
	
	private int	instanceIndex=-1;	//* Index used to associate an instance to runtime resources */
	String 		logger_file;		//* NBS writes (logs) to this file */
	String 		params_file;		//* NBS reads parameters from this file */
	String 		process_str; 		//* process manager cmd string */
	String		process_env;		//* environment variables to pass to exec() */
	
	/**
	 * @return
	 */
	public int getInstanceIndex() {
		return instanceIndex;
	}
	/**
	 * @param instanceIndex
	 */
	public void setInstanceIndex(int instanceIndex) {
		this.instanceIndex = instanceIndex;
	}
	/**
	 * @return
	 */
	private String getLogger_file() {
		return logger_file;
	}
	/**
	 * @param logger_file
	 */
	public void setLogger_file(String logger_file) {
		this.logger_file = logger_file;
	}
	/**
	 * @return the params_file
	 */
	private String getParams_file() {
		return params_file;
	}
	/**
	 * @param params_file the params_file to set
	 */
	public void setParams_file(String params_file) {
		this.params_file = params_file;
	}
	/**
	 * @return
	 */
	private String getProcess_str() {
		return process_str;
	}
	/**
	 * @param nbs_process_str
	 */
	public void setProcess_str(String process_str) {
		this.process_str = process_str;
	}
	/**
	 * @return process_env
	 */
	public String getProcess_env() {
		return process_env;
	}
	/**
	 * @param process_env
	 */
	public void setProcess_env(String process_env) {
		this.process_env = process_env;
	}
	/**
	 * @param index
	 */
	public String getLoggerFilePath(int index){
		return getLogger_file().replace('#', index==0?'0':'1').
								replace("u@", System.getProperty("user.name"));
	}
	/**
	 * @param index
	 * @return
	 */
	public String getParamsFilePath(int index){
		return getParams_file().replace('#', index==0?'0':'1').
								replace("u@", System.getProperty("user.name"));
	}
	/**
	 * @param index
	 * @return
	 */
	public String getProcessManagerCmdString(int index){
		return getProcess_str().replace('#', index==0?'0':'1').
								replace("u@", System.getProperty("user.name"));
	}
	/**
	 * @param index
	 * @return 
	 */
	public String[] getProcessManagerEnvString(int index){
		return new String[]{getProcess_env().replace('#', index==0?'0':'1').
								replace("u@", System.getProperty("user.name"))};
	}
	/**
	 * @param inProject
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public String execute(Project inProject)
			throws IOException, InterruptedException {
						
		/**
		 * Serialize 'inProject' and write to disk as input to NBS.
		 */
		MyFileUtils.writeFile(Project.ProjectToJson(inProject), this.getParamsFilePath(this.instanceIndex));
	
		/**
		 * Use Runtime object to execute the NBS command
		 */
		Process process = null;
		Runtime runTime = Runtime.getRuntime();
		final String os_name = System.getProperty("os.name").toLowerCase();
		if( os_name.contains("windows") ) {
			process = runTime.exec(getProcessManagerCmdString(this.instanceIndex));
		}
		else if (os_name.contains("mac") || os_name.contains("darwin"))  {
			//process = runTime.exec(getProcessManagerCmdString(this.instanceIndex),
			//		               getProcessManagerEnvString(this.instanceIndex));	//	requires DYLD_LIBRARY_PATH in mac
			                                                                        // but must be set in .bashrc!!!! 
			process = runTime.exec(getProcessManagerCmdString(this.instanceIndex));
		}
		else {	//	Linux runtime environment.
			process = runTime.exec(getProcessManagerCmdString(this.instanceIndex),
								   getProcessManagerEnvString(this.instanceIndex));	//	requires LD_DIRECTORY_PATH in lnx
		}
		/**
		 * error and output message traps using stream gobbler.
		 */
		String logPath = this.getLoggerFilePath(this.instanceIndex);
		FileOutputStream fOutput = new FileOutputStream(logPath);
	    StreamGobbler errGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");            
	    StreamGobbler outGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", fOutput);
	    
	    /**
	     * Kick off..
	     */
	    errGobbler.start();
	    outGobbler.start();
	    
	    /**
	     * Call the cmd...			
	     */
		try {
			@SuppressWarnings("unused")
			int exitVal = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		/**
		 * Wait for these threads to die.
		 */
		errGobbler.join();
		outGobbler.join();
		
		/**
		 * Kill the output stream buff.
		 */
		fOutput.flush();
		fOutput.close();
			
		return "success";
	}
}