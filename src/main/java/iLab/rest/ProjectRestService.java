package main.java.iLab.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import main.java.iLab.domain.Project;
import main.java.iLab.service.InstanceService;
import main.java.iLab.service.ProjectService;
import main.java.iLab.utils.EdgeListUtils;
import main.java.iLab.utils.MyFileUtils;

import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * This class is the main rest interface. It handles returning a param block to
 * the client containing the base NBS params, and it handles accepting a form
 * from an http client, parsing the form and submitting the parsed contents to
 * the NBS Matlab service.
 * 
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

@Path("project")
public class ProjectRestService {

	@Context
	private ServletContext context;
	@Context
	private HttpServletResponse response;
	@Context
	private HttpServletRequest request;

	/**
	 * Create a path to which ultimately serves as a file click path returned to
	 * the client via email.
	 * 
	 * @param request
	 *            - an HttpServletRequest object
	 * @return string - a path to this project
	 */
	public static String getURLWithContextPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
	}

	/**
	 * Rest service call to fetch paramaters and return to client.
	 * 
	 * @param fileName
	 * @return a json-formatted string suitable for display in html page.
	 */
	@GET
	@Path("/getDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Project getProjectDetails(@QueryParam("fileName") String fileName) {
		ProjectService svc = new ProjectService();
		try {
			return svc.getProjectDetails(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Rest service call to run a project. This is typically NBS
	 * 
	 * @param form
	 *            - a multi-part MIME formatted form which contains NBS
	 *            parameters.
	 * @return a json-formatted string suitable for display in html page.
	 */
	@POST
	@Path("/runProject")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Project runProject(FormDataMultiPart form) {

		System.out.println("OUTPUT>INFO: NBS_web: Welcome To NBS!");

		// ------------
		System.out.println("FORM");
		System.out.println(form);
		Map<String, List<FormDataBodyPart>> fields = form.getFields();
		Iterator it = fields.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			// it.remove(); // avoids a ConcurrentModificationException
		}
        FormDataBodyPart cof =  form.getField("config");
        System.out.println("config:");
        try {
            System.out.println(cof.getValue());
        }
        catch ( final Exception e ) {
        	 System.out.println("Problem with config data");
        	 e.printStackTrace();
        }
		// -----
		/**
		 * Extract the configuration data from the form. Create Project object.
		 */
		FormDataBodyPart config = form.getField("config");
		Project project = Project.JsonToProject(config.getValueAs(String.class));

		/**
		 * Check to see if there are any tasks that need to be run. NOTE: this
		 * is solely a development routine inserted into the main project
		 * thread. This will NEVER get run during normal execution.
		 */
		try {
			EdgeListUtils.convertEdgelistFiles(false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/**
		 * Check to see if NBS is already in use. Initial implementation can
		 * support 2 instances.
		 */
		if (!InstanceService.hasAvailableInstance()) {
			project.setStatus("nbs_in_use");
			System.out.println("OUTPUT>NBS is at full capacity. Returning");
			return project;
		}

		/**
		 * Each invocation of NBS results in the creation of a unique directory.
		 * These directories are stored inside a directory referenced by
		 * getOutputDirectory. Output Directory grows over time, as the NBS
		 * service is utilized. Get a reference to 'destinationDir' directory.
		 * Check size. Delete directory contents if necessary. TODO: Make this
		 * more intelligent. This is lame. Maybe do this by date (delete old
		 * items).
		 */
		String realPath = context.getRealPath(File.separator);
		String outputDir = project.getOutputDirectory();
		File destinationDir = new File(realPath + File.separator + outputDir);
		MyFileUtils.deleteDirectoryContents(destinationDir, 100);

		/**
		 * Create a unique directory inside of 'destinationDir'. This unique
		 * directory is where the results for this particular NBS session are
		 * stored. NBS will write result files into this directory, then .zip
		 * the entire directory. This .zip file is referenced in an email that
		 * is sent to the client (ClickableFilePath). Lastly, this directory is
		 * deleted (see cleanup()), leaving only the .zip file.
		 */
		try {
			String contextPath = getURLWithContextPath(request);
			java.nio.file.Path path = Files.createTempDirectory(destinationDir.toPath(), "nbs_");
			project.setOutputDirectory(path.toString());
			project.setClickableFilePath(contextPath + "/" + outputDir + "/" + path.getFileName().toString() + ".zip");
		} catch (IOException e) {
			e.printStackTrace();
		}

		/**
		 * Extract files uploaded with the form (e.g.: patient / network files).
		 */
		try {
			project.saveUploadFiles(form, project, realPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/**
		 * Create a new service and finally run the project.
		 */
		ProjectService svc = new ProjectService(); // TODO for debug - comment
		svc.runProject(project);// TODO for debug - comment
		return project;
	}
}
