package main.java.iLab.utils;

import java.io.IOException;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import main.java.iLab.domain.Project;

public class SendGmail {
	
	static Properties properties;
	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage generateMailMessage;
 
	@SuppressWarnings("unused")
	public void generateAndSendEmail(Project project) 
				throws AddressException, MessagingException, InvalidPropertiesFormatException, IOException {
		
		System.out.println("OUTPUT>INFO: NBS_web: Begin Send mail");
		/*
		 * Get the basic email properties.
		 */
		properties = new Properties();
		properties.loadFromXML(SendGmail.class.getResourceAsStream("/email.properties"));
 
		/* Determines the current system properties and 
		 * initializes property keys with input values.
		 */
		Authenticator auth = new SMTPAuthenticator();
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", properties.getProperty("srvcPort"));
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		getMailSession = Session.getDefaultInstance(mailServerProperties, auth);
 
		final String job_name = project.getJobName();
		
		/*
		 * Creates a new mail session object from mail server properties.
		 * Sets up some basic addressing, date and subject field.
		 */
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.setSubject(properties.getProperty("subject") + ": " + job_name);
		generateMailMessage.setFrom(new InternetAddress(properties.getProperty("mailFrom"),"UCSD - Ideker Lab Web Services"));
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(project.getEmailAddress()));
		generateMailMessage.setSentDate(new Date());
		
		/*
		 * Create a body part for the body text.
		 */
		
		final String project_status = project.getStatus();
		
		StringBuilder txt = new StringBuilder();
		txt.append("\n");
		txt.append("Job name  : " + job_name + "\n");
		txt.append("Start date: " + project.getStartDate() + "\n");
		txt.append("\n\n");
		
		StringBuilder params = new StringBuilder();
		params.append("\n");
		params.append("\n");
		params.append("\n");
		params.append("Patient file name: " + project.getPatientFileName());
		params.append("\n");
		params.append("Network file name: " + project.getNetworkFileName());
		params.append("\n");
		params.append("Parameters: \n");
		params.append(project.getParamTextBlock() );
		params.append("\n");
		params.append("\n");
		params.append("Log: \n");
		params.append(project.getLog());
		params.append("\n");
	
		String text = properties.getProperty("msgBody") + txt + "Results: " +  project.getOutputFileName() + params;
		MimeBodyPart mbp1 = new MimeBodyPart();
	    mbp1.setText(text);
	    
	    /*
		 * Create a body part for the file attachment.
		 * NOTE: this is currently unused but could be used again.
		 */
	    if( false ) {
		    String includeFile = project.getOutputDirectory() + ".zip";
		    FileDataSource fds = new FileDataSource(includeFile){
	    		public String getContentType() {
	    		    return "application/zip; name=nbs_results.zip";
	    		}
		    };
		    MimeBodyPart mbp2 = new MimeBodyPart();
	        mbp2.setFileName(properties.getProperty("fileName"));
	        mbp2.setDataHandler(new DataHandler(fds));
	    }
        
        /*
		 * Create a multi-part enclosure and set its body parts.
		 */
	    Multipart mp = new MimeMultipart();
	    mp.addBodyPart(mbp1);
	    //mp.addBodyPart(mbp2);
	    generateMailMessage.setContent(mp);
	  
		/*
		 * Send it!
		 */
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect(properties.getProperty("hostName"),
						  properties.getProperty("userName"),
						  properties.getProperty("passWord"));
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
		System.out.println("OUTPUT>INFO: NBS_web: End Send Mail");

	}
	
	// Authenticates to SendGrid
  private class SMTPAuthenticator extends javax.mail.Authenticator {
    @Override
    public PasswordAuthentication getPasswordAuthentication() {
      String username = properties.getProperty("srvcUser");
      String password = properties.getProperty("passWord");
      return new PasswordAuthentication(username, password);
    }
  }
  
  
 
	public void generateAndSendEmailProjectStarted(Project project) 
				throws AddressException, MessagingException, InvalidPropertiesFormatException, IOException {
		
		System.out.println("OUTPUT>INFO: NBS_web: Begin Send mail: Project started");
		/*
		 * Get the basic email properties.
		 */
		properties = new Properties();
		properties.loadFromXML(SendGmail.class.getResourceAsStream("/email.properties"));

		/* Determines the current system properties and 
		 * initializes property keys with input values.
		 */
		Authenticator auth = new SMTPAuthenticator();
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", properties.getProperty("srvcPort"));
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		getMailSession = Session.getDefaultInstance(mailServerProperties, auth);

		final String job_name = project.getJobName();
		
		/*
		 * Creates a new mail session object from mail server properties.
		 * Sets up some basic addressing, date and subject field.
		 */
		generateMailMessage = new MimeMessage(getMailSession);
		generateMailMessage.setSubject("NBS Job Started: " + job_name);
		generateMailMessage.setFrom(new InternetAddress(properties.getProperty("mailFrom"),"UCSD - Ideker Lab Web Services"));
		generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(project.getEmailAddress()));
		generateMailMessage.setSentDate(new Date());
		
		/*
		 * Create a body part for the body text.
		 */

		StringBuilder params = new StringBuilder();
		params.append("\n");
		params.append("Job started: " + job_name + "\n");
		params.append("\n");
		params.append("Patient file name: " + project.getPatientFileName());
		params.append("\n");
		params.append("Network file name: " + project.getNetworkFileName());
		params.append("\n");
		params.append("Parameters: \n");
		params.append(project.getParamTextBlock() );
		params.append("\n");
	
		MimeBodyPart mbp1 = new MimeBodyPart();
	    mbp1.setText(params.toString());
      
         /*
		 * Create a multi-part enclosure and set its body parts.
		 */
	    Multipart mp = new MimeMultipart();
	    mp.addBodyPart(mbp1);
	    generateMailMessage.setContent(mp);
	  
		/*
		 * Send it!
		 */
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect(properties.getProperty("hostName"),
						  properties.getProperty("userName"),
						  properties.getProperty("passWord"));
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
		System.out.println("OUTPUT>INFO: NBS_web: End Send Mail");

	}
  
		
}