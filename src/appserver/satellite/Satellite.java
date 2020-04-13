package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;

/**
 * Class [Satellite] Instances of this class represent computing 
 * nodes that execute jobs by calling the callback method of tool a 
 * implementation, loading the tool's code dynamically over a network
 * or locally from the cache, if a tool got executed before.
 * 
 * FROM ASSIGNMENT 7 DESCRIPTION:
 * 
 * Satellite servers are actually doing the work of the application 
 * server - the latter only being a way to access satellites. 
 * 
 * Thus satellites take on Jobs, get the right tool to do the job, 
 * process the job and get the results back to whoever requested the job to be done.
 * 
 * Application server adds value by: 
 * 		(a) letting the registering happen for satellite servers
 * 		(b) providing load-balancing based in some policy. 
 * 
 * Satellite servers can be run on their own. 
 * 
 * ** Concentrate on the satellite server implementation, without getting 
 * side-tracked by the details and complexity of the overall application server structure.
 * 
 * 
 * TODO: Before you start the whole application, you will first need to MOVE the PlusOne tool consisting of two classes 
 * in package appserver.job.impl.plusone to the appropriate directory (mimicking the package structure) in the docRoot. 
 * Make sure to MOVE, otherwise the classes are found prematurely in the class path and won't be loaded over the network through the web server.
 * 
 * Then start the webserver, a satellite server and finally the client - in this order. Pay attention to the following:
 * All three programs need to get access to the correct properties files. The way how I set up the directory structure makes sure that happens. Check that the directories config (which contains the properties files) and also the docRoot are in your NetBeans project at the very same spot as they are in the package that you received.
 * Now start all three programs, each one of them in their own terminal window (don't start them from the IDE!!!). In each of the three terminal windows, go into the directory where the beginning of the class path for all compiled classes is, which is in build/classes, then start the program you want to run. The command lines, for your convenience, are as follows:
 * java web/SimpleWebServer ../../config/WebServer.properties
 * java appserver.satellite.Satellite ../../config/Satellite.Earth.properties ../../config/WebServer.properties ../../config//Server.properties
 * java appserver.job.impl.plusone.PlusOneClient ../../config/Satellite.Earth.properties
 */
public class Satellite extends Thread {

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private Hashtable<String, Tool> toolsCache = null;

    /**
     * Note: just pass in, and read the application server properties file, 
     * as you will not want to change the satellite server's constructor
     * 
     * @param satellitePropertiesFile
     * @param classLoaderPropertiesFile
     * @param serverPropertiesFile
     */
    public Satellite(String satellitePropertiesFile, 
    				 String classLoaderPropertiesFile, 
    				 String serverPropertiesFile) {
    	
    	// satellite's configuration and properties from file passed in
    	PropertyHandler satConfig; String satName; int satPort; 
    	
    	// server's configuration and properties from file passed in
    	PropertyHandler serverConfig; String serverName; int serverPort; 
    	
    	// class loader's configuration and properties from file passed in
    	PropertyHandler classConfig; String className; int classPort; 
    	
    	
        // read this SATELLITE'S properties and populate satelliteInfo object,
        // which later on will be sent to the server
        try {
        	 // read satellite's properties 
        	 satConfig = new PropertyHandler(serverPropertiesFile); 
        	 
        	 // populate satelliteInfo 
        	 satName = satConfig.getProperty("NAME");
        	 satPort = Integer.parseInt(satConfig.getProperty("PORT")); //convert to type int
        	 
        } catch (Exception e) {
        	// no config file
        	e.printStackTrace(); 
        	System.exit(1);
        }
        
        // read properties of the APPLICATION SERVER and populate serverInfo object
        // other than satellites, the as doesn't have a human-readable name, so leave it out
        try {
        	 // read server's properties 
        	 serverConfig = new PropertyHandler(serverPropertiesFile);
        	 // populate serverInfo 
        	 serverName = serverConfig.getProperty("NAME");
        	 serverPort = Integer.parseInt(serverConfig.getProperty("PORT")); //convert to type int
       	 
        } catch (Exception e) {
        	// no config file
        	e.printStackTrace(); 
        	System.exit(1);
        }
        
        // read properties of the code server and create class loader
        // -------------------
        try {
	       	 // read classLoader's properties 
	       	 classConfig = new PropertyHandler(classLoaderPropertiesFile);
	       	 // populate serverInfo 
	       	 className = classConfig.getProperty("NAME");
	       	 classPort = Integer.parseInt(classConfig.getProperty("PORT")); //convert to type int
	      	 
       } catch (Exception e) {
	       	// no config file
	       	e.printStackTrace(); 
	       	System.exit(1);
       }
        
        // create tools cache
        // -------------------
        toolsCache = new HashMap<>(); 
        
    }

    @Override
    public void run() {

        // IGNORE: register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        // ...
        
        
        // create server socket
        // ---------------------------------------------------------------
        // ...
        
        
        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        // ...
    }

    // inner helper class that is instanciated in above server loop and processes single job requests
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        @Override
        public void run() {
            // setting up object streams
            // ...
            
            // reading message
            // ...
            
            switch (message.getType()) {
                case JOB_REQUEST:
                    // processing job request
                    // ...
                    break;

                default:
                    System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the cache,
     * otherwise it is loaded dynamically
     */
    public Tool getToolObject(String toolClassString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Tool toolObject = null;

        // ...
        
        return toolObject;
    }

    public static void main(String[] args) {
        // start the satellite
        Satellite satellite = new Satellite(args[0], args[1], args[2]);
        satellite.run();
    }
}
