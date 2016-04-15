package main.java.iLab.domain;

import javax.xml.bind.annotation.XmlRootElement;

import main.java.iLab.domain.project.nbs;
import main.java.iLab.utils.MyFileUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author mteixeira
 */

@XmlRootElement
public class Project {
	
	static final String SERVER_UPLOAD_LOCATION_FOLDER = "upload_temp";
	
	private int			id;
	private String 		status;
	private String 		projectName;
    private String 		ProjectDescription;
	private String		patientFileName;
    private String		networkFileName;
	private	String		networkResourceName;
	private String		networkInputType;
    private String		outputDirectory;
    private	String		outputFileName;
    private String		jobName;
    private String		emailAddress;
    private String		paramTextBlock;
	private List<File> 	uploadfiles;
	private String      log;
	private String 		start_date;
		
	
		public String getStartDate() {
			return start_date;
		}
		
		public void setStartDate(String start_date) {
			this.start_date = start_date;
		}
	
	
    /**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @param projectName the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	/**
	 * @return the projectDescription
	 */
	public String getProjectDescription() {
		return ProjectDescription;
	}
	/**
	 * @param projectDescription the projectDescription to set
	 */
	public void setProjectDescription(String projectDescription) {
		ProjectDescription = projectDescription;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the patientFileName
	 */
	public String getPatientFileName() {
		return patientFileName;
	}
	/**
	 * @param patientFileName the patientFileName to set
	 */
	public void setPatientFileName(String patientFileName) {
		this.patientFileName = patientFileName;
	}
	/**
	 * @return the networkFileName
	 */
	public String getNetworkFileName() {
		return networkFileName;
	}
	/**
	 * @param networkFileName the networkFileName to set
	 */
	public void setNetworkFileName(String networkFileName) {
		this.networkFileName = networkFileName;
	}
	 /**
	 * @return the networkInputType
	 */
	public String getNetworkInputType() {
		return networkInputType;
	}
	/**
	 * @param networkInputType the networkInputType to set
	 */
	public void setNetworkInputType(String networkInputType) {
		this.networkInputType = networkInputType;
	}
	/**
	 * @return the networkResourceName
	 */
	public String getNetworkResourceName() {
		return networkResourceName;
	}
	/**
	 * @param the networkResourceName to set
	 */
	public void setNetworkResourceName(String networkResourceName) {
		this.networkResourceName = networkResourceName;
	}
	/**
	 * @return the outputDirectory
	 */
	public String getOutputDirectory() {
		return outputDirectory;
	}
	/**
	 * @param outputDirectory the outputDirectory to set
	 */
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	 /**
	 * @return the outputFileName
	 */
	public String getOutputFileName() {
		return outputFileName;
	}
	/**
	 * @param outputFileName the outputFileName to set
	 */
	public void setClickableFilePath(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	/**
	 * @return the paramTextBlock
	 */
	public String getParamTextBlock() {
		return paramTextBlock;
	}
	/**
	 * @param paramTextBlock the paramTextBlock to set
	 */
	public void setParamTextBlock(String paramTextBlock) {
		this.paramTextBlock = paramTextBlock;
	}
	/**
	 * @return the uploadfiles
	 */
	protected List<File> getUploadfiles() {
		return uploadfiles;
	}
	/**
	 * @param uploadfiles the uploadfiles to set
	 */
	protected void setUploadfiles(List<File> uploadfiles) {
		this.uploadfiles = uploadfiles;
	}
	/**
	 * @param form containing the MIME file streams
	 * @throws IOException 
	 */
	public List<File> saveUploadFiles(FormDataMultiPart form, Project project, String path) throws IOException {
		/* Subclasses must override */
		return null;
	}
	
	/**
	 * execute - subclasses must override to perform their own program execution.
	 */
	public String execute() {
		return "Error: Execute must be subclassed!";
	}
	
	/**
	 * cleanup
	 * @param delete upload files associated with this project
	 * @throws IOException 
	 */
	public void cleanup() {
		File file = null;
		if( uploadfiles != null && ! uploadfiles.isEmpty() ) {
			Iterator<File> itr = uploadfiles.iterator();
		      while(itr.hasNext()) {
		         file = (File) itr.next();
		         file.delete(); 
			}
		}
		uploadfiles = null;
		try {
			FileUtils.deleteDirectory(new File(getOutputDirectory()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * broadcastDone - send notification to client that the op. has completed
	 */
	public void broadcastDone() {
		/* Subclasses must override */
	}
	
	public void broadcastStarted() {
		/* Subclasses must override */
	}
	/**
	 * JsonToProject
	 * @param inJsonData - json string
	 * @return - Project
	 */
	public static Project JsonToProject(final String inJsonData) {
		Project project = null;
		 try 
		 	{
			 project = new ObjectMapper().readValue(inJsonData, nbs.class);
			} catch (final JsonParseException e) {
				e.printStackTrace();
			} catch (final JsonMappingException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		 return project;
	}
	
	/**
	 * @param inProject
	 * @return
	 */
	public static String ProjectToJson(final Project inProject) {
		String project = null;
		 try 
		 	{
			 ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			 project = ow.writeValueAsString(inProject);
			} catch (final JsonParseException e) {
				e.printStackTrace();
			} catch (final JsonMappingException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		 return project;
	}
	/**
	 * @param filePart
	 * @return a file ref
	 */
	protected File saveFilePart(FormDataBodyPart filePart, String path) {
		File file = null;
		ContentDisposition headerOfFilePart = filePart.getContentDisposition();
		String extension = FilenameUtils.getExtension(headerOfFilePart.getFileName());
		InputStream fileIS = filePart.getValueAs(InputStream.class);
		String filePath = path + File.separator + SERVER_UPLOAD_LOCATION_FOLDER + File.separator + MyFileUtils.createRandomFileName("user_", extension);
		
		try {
			file = MyFileUtils.storeFile(fileIS, filePath);
			file.deleteOnExit();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return file;
	}
	
	public String getLog() {
		return log;
	}

	public void setLog( String log) {
		this.log = log;
	}
	

}
