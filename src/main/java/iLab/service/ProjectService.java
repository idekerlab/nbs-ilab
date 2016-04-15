package main.java.iLab.service;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import main.java.iLab.domain.Project;
import main.java.iLab.utils.RunProject;

import org.apache.commons.io.IOUtils;
import org.json.XML;

/**
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */
public class ProjectService {
    	
	/**
	 * @param fileName
	 * @return
	 * @throws InvalidPropertiesFormatException
	 * @throws IOException
	 */
	public Project getProjectDetails(String fileName)
			throws InvalidPropertiesFormatException, IOException {
    	
		Properties properties = new Properties();
		properties.loadFromXML(Project.class.getResourceAsStream(fileName));
		
    	// Set Project instance from XML data.
    	
    	Project project = new Project();
        project.setProjectName(properties.getProperty("projectName"));
		project.setProjectDescription(properties.getProperty("projectDescription"));
		project.setStatus(properties.getProperty("status"));
		project.setPatientFileName(properties.getProperty("patientFileName"));
		project.setNetworkFileName(properties.getProperty("networkFileName"));
		project.setNetworkResourceName(properties.getProperty("networkResourceName"));
		project.setNetworkInputType(properties.getProperty("networkInputType"));
		project.setOutputDirectory(properties.getProperty("outputDirectory"));
		project.setEmailAddress(properties.getProperty("emailAddress"));
		
		/* This is kind of exotic - I want to return json not xml */
		
		project.setParamTextBlock(
				
				XML.toJSONObject(new String(IOUtils.toByteArray(
						ProjectService.class.getResourceAsStream("/nbs_params.xml")))).toString(4));
	
		
    	return project;
    }
	
	/**
	 * runProject - Call project runner to run this project.
	 * its logic.
	 * @param project
	 * @return
	 */
	public void runProject(Project project) {
		
		RunProject.doRunProject(project);
	}
}

