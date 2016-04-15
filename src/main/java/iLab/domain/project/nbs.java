package main.java.iLab.domain.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;

import org.apache.commons.io.FilenameUtils;

import main.java.iLab.domain.Instance;
import main.java.iLab.domain.Project;
import main.java.iLab.service.InstanceService;
import main.java.iLab.utils.EdgeListUtils;
import main.java.iLab.utils.SendGmail;

import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.FormDataBodyPart;

/**
 * This class is used to encapsulate an NBS project. A Project mainly a
 * collection of attributes which describe it, such as projectName, description,
 * etc. NBS class contains elements and functionality which are unique to NBS.
 * 
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

public class nbs extends Project {

	private static final boolean DEBUG = true;
	private static final String CXEXTENSION = "cx";
	private static final String MATEXTENSION = ".mat";
	private static final String PATIENT_FILE_PART = "patientFile";
	private static final String NETWORK_FILE_PART = "networkFile";

	/**
	 * This handles conversion of one file type to another.
	 * 
	 * @param inFile
	 * @return
	 * @throws IOException
	 */
	private File convertFile(final File inFile) throws IOException {
		File returnFile = null;
		if (inFile != null) {
			String ext = FilenameUtils.getExtension(inFile.getName());
			if (ext.equalsIgnoreCase(nbs.CXEXTENSION)) {
				/* convert a cx file to a matlab network file */
				String newName = FilenameUtils.getBaseName(inFile.getAbsolutePath()) + nbs.MATEXTENSION;
				if (DEBUG) {
					System.out.println("OUTPUT>INFO: Converting src: " + inFile.getName());
					System.out.println("OUTPUT>INFO: Converting dst: " + newName);
				}
				File f = new File(inFile.getParent() + File.separator + newName);
				EdgeListUtils.EdgeListToMatlab(inFile.getAbsolutePath(), f.getAbsolutePath());
				returnFile = f;
			}
		}
		return returnFile;
	}

	/**
	 * Fetch a file from NDex and convert if necessary.
	 * 
	 * @param inFile
	 * @return
	 * @throws IOException
	 */
	private File getNdexFile(final String inFile) throws IOException {
		return null;
	}

	/**
	 * This routine accepts an incoming FormDataMultiPart and extracts
	 * components and determines conversion.
	 */
	@Override
	public List<File> saveUploadFiles(FormDataMultiPart form, Project project, String path) throws IOException {
		if (DEBUG) {
			System.out.println("STARTED: saveUploadFiles");
		}

		if (form == null) {
			if (DEBUG) {
				System.out.println("form is null");
			}
			return null;
		}
		File file = null;
		FormDataBodyPart filePart = null;
		List<File> files = new ArrayList<File>();

		filePart = form.getField(PATIENT_FILE_PART);
		if (filePart != null) {
			file = saveFilePart(filePart, path);
			files.add(file);
			setPatientFileName(file.getPath());
			/* Look for a converted file... */
			if ((file = convertFile(file)) != null) {
				files.add(file);
				setPatientFileName(file.getPath());
				if (DEBUG) {
					System.out.println("OUTPUT>INFO: Patient File Conversion: " + file.getPath());
				}
			}
		}
		if (project.getNetworkInputType().equalsIgnoreCase("type_file")) {
			filePart = form.getField(NETWORK_FILE_PART);
			if (filePart != null) {
				file = saveFilePart(filePart, path);
				files.add(file);
				setNetworkFileName(file.getPath());
				/* Look for a converted file... */
				if ((file = convertFile(file)) != null) {
					files.add(file);
					setNetworkFileName(file.getPath());
					if (DEBUG) {
						System.out.println("OUTPUT>INFO: Network File Name: " + file.getPath());
					}
				}
			}
		} else if (project.getNetworkInputType().equalsIgnoreCase("type_ndex")) {
			file = getNdexFile("name_goes_here");
			if (file != null) {
				files.add(file);
				setNetworkFileName(file.getPath());
				/* Look for a converted file... */
				if ((file = convertFile(file)) != null) {
					files.add(file);
					setNetworkFileName(file.getPath());
					if (DEBUG) {
						System.out.println("OUTPUT>INFO: Network File Name: " + file.getPath());
					}
				}
			}
		}
		setUploadfiles(files);
		if (DEBUG) {
			System.out.println("FINISHED: saveUploadFiles");
		}
		return files;
	}

	/**
	 * This routine creates and initializes a new service, and runs an instance.
	 * 
	 */
	@Override
	public String execute() {
		if (DEBUG) {
			System.out.println("STARTED: execute");
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		setStartDate(dateFormat.format(date));

		String returnVal = "";

		String properties = null;

		final String os_name = System.getProperty("os.name").toLowerCase();
		if (os_name.contains("windows")) {
			System.out.println("WIN");
			properties = "/instance_win.properties";
		} else if (os_name.contains("mac") || os_name.contains("darwin")) {
			System.out.println("MAC");
			properties = "/instance_mac.properties";
		} else { // Linux runtime environment.
			System.out.println("linux");
			properties = "/instance_lnx.properties";
		}

		try {
			InstanceService svc = new InstanceService();
			Instance instance = svc.getInstanceDetails(properties);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);
			// IMPORTANT: Save the old System.out!
			PrintStream old = System.out;
			System.setOut(ps);

			//
			returnVal = svc.runInstance(instance, this);
			// Put things back
			System.out.flush();
			System.setOut(old);
			String str = "";
			try {
				str = baos.toString();
				if (str.length() > 6000) {
					final StringBuilder sb = new StringBuilder();
					sb.append(str.substring(0, 3000));
					sb.append("\n...\n...\n");
					sb.append(str.substring(str.length() - 3000, str.length() - 1));
					str = sb.toString();
				}
			} catch (final Exception e) {
				str = "";
				e.printStackTrace();
               // Do nothing
			}

			setLog(str);
			System.out.println(getLog());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return returnVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see main.java.iLab.domain.Project#broadcastDone()
	 */
	@Override
	public void broadcastDone() {
		try {
			final SendGmail mail = new SendGmail();
			mail.generateAndSendEmail(this);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void broadcastStarted() {
		try {
			final SendGmail mail = new SendGmail();
			mail.generateAndSendEmailProjectStarted(this);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
	}
}
