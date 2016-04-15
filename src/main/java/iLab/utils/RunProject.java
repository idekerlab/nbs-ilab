/**
 * 
 */
package main.java.iLab.utils;

import main.java.iLab.domain.Project;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is very straightforward : it's a class wrapping a timer instance.
 * RunProject creates a new timer instance which calls into 'run()'.
 * 
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

public class RunProject extends TimerTask {

	Timer	myTimer = null;
	Project project = null;
	
	/**
	 * top-level interface to call private constructor
	 * @param project - NBS Project
	 */
	public static void doRunProject(Project project){
		new RunProject(project);
	}

    /**
     * RunProject - private constructor for new timer instance.
     * @param project
     */
    private RunProject(Project project) {
		super();
		this.project = project;
		this.myTimer = new Timer(true);
		this.myTimer.scheduleAtFixedRate(this, 0, 10*1000);
	}
    
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
    public void run() {
		project.broadcastStarted();
		myTimer.cancel();
		myTimer = null;
		project.execute();
		project.cleanup();
		project.broadcastDone();
    }
}
