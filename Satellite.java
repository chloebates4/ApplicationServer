package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.IOException;
import java.io.FileNotFoundException;
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

/** ASSIGNMENT 7: APPLICATION SERVER - SATELLITE SERVER
     *  @author Chloe Bates     
     *  @author Sam Guilb   
     *  @author Colton Spector
 * -----------------------------------------------------------------------------------------
 * Class [Satellite] Instances of this class represent computing nodes that execute jobs by
 * calling the callback method of tool a implementation, loading the tool's code dynamically 
 * over a network or locally from the cache, if a tool got executed before.
 * -----------------------------------------------------------------------------------------
 *  Satellite servers are actually doing the work of the application server - the latter only
 *  being a way to access satellites. Thus satellites: 
 *      --> take on Jobs, 
 *      --> get the right tool to do the job, 
 *      --> process the job & get the results back to whoever requested the job to be done.
 *  Application server adds value by: 
 *      (a) letting the registering happen for satellite servers
 *      (b) providing load-balancing based in some policy. 
 *  Satellite servers can be run on their own. 
 * -----------------------------------------------------------------------------------------
 * --> Concentrate on satellite server implementation, w/out getting side-tracked 
 * by the details & complexity of the overall application server structure.
 */
public class Satellite extends Thread {

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = null;
    private HashMap<String, Tool> toolsCache = null;

    /**
     * Satellite () 
     * -----------------------------------------------------------------------------------------
     * Note: just pass in, and read the application server properties file, 
     * as you will not want to change the satellite server's constructor
     * @param satellitePropertiesFile
     * @param classLoaderPropertiesFile
     * @param serverPropertiesFile
     */
    public Satellite(String satellitePropertiesFile, 
                     String classLoaderPropertiesFile, 
                     String serverPropertiesFile) {
        // satellite's configuration and properties
        PropertyHandler satConfig; 
        String satName; int satPort; 
        
        // server's configuration and properties 
        PropertyHandler serverConfig; 
        String serverHost; int serverPort; 
        
        // class loader's configuration and properties 
        PropertyHandler classConfig; 
        String classHost; int classPort; 
        
        
        // read satellitePropertiesFile & populate satelliteInfo which later on will be sent to the server
        // ---------------------------------------------------------------
        try {
             // read properties 
             satConfig = new PropertyHandler(serverPropertiesFile); 
             // populate satelliteInfo 
             satelliteInfo.setName(satConfig.getProperty("NAME"));
             satelliteInfo.setPort(Integer.parseInt(satConfig.getProperty("PORT")));
             
             // Q: set Host? 
             
        } catch (Exception e) {
            // no config file
            System.err.println("[Satellite.constructor] "+e); 
            System.exit(1);
        } 
        
        // read serverPropertiesFile & populate serverInfo object 
        // ---------------------------------------------------------------
        try {
             // read properties 
             serverConfig = new PropertyHandler(serverPropertiesFile);
             // populate serverInfo 
             serverInfo.setHost(serverConfig.getProperty("HOST"));
             serverInfo.setPort(Integer.parseInt(serverConfig.getProperty("PORT"))); 

        }catch (Exception e) {
            // no config file
            System.err.println("[Satellite.constructor] "+e); 
            System.exit(1);
        } 
        
        
        // read properties of classLoaderPropertiesFile & create classLoader
        // ---------------------------------------------------------------
        try {
             // read properties 
             classConfig = new PropertyHandler(classLoaderPropertiesFile);
             classHost = classConfig.getProperty("HOST");
             classPort = Integer.parseInt(classConfig.getProperty("PORT")); //convert to type int
             
             // populate class loader
            if (classHost != null && classPort != 0 ) {
                 classLoader = new HTTPClassLoader(classHost, classPort); 
            }
       } catch (Exception e) {
            System.err.println("[Satellite.constructor] CAUGHT -- FileNotFound Exception\n"+e); 
            System.exit(1);
       } 
        
        // is the classLoader working properly? 
        if( classLoader == null )
        {
            System.err.println("[Satellite.constructor] Unable to create HTTPClassLoader....");
            System.exit(1);
        }
        
        
        // CREATE TOOLS CACHE
        // ---------------------------------------------------------------
        // note: param types are set in it's initialization above
        toolsCache = new HashMap<>(); 
        
    }

    /**
     * run () 
     * -----------------------------------------------------------------------------------------
     */
    @Override
    public void run() {

        // IGNORE: register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        
        
        // create server socket
        // ---------------------------------------------------------------        
        try {
            ServerSocket serverSocket = new ServerSocket(satelliteInfo.getPort()); 
            System.out.println("[Satellite.run] Server socket created."); 
            
            // start taking job requests in a server loop
            // ---------------------------------------------------------------
            while (true) {
                System.out.println("[Satellite.run] PORT #" + satelliteInfo.getPort()  
                                + " waiting for a connection..." );
                
                Socket socket= serverSocket.accept(); // acceptance of new connections
                System.out.println("[Satellite.run] New connection was created."); 
                
                // thread for job to be completed
                (new SatelliteThread (socket, this)).start(); 
            }       
        
        } catch (Exception e) {
            System.err.println("[Satellite.run] Unable to connect.   " + e); 
            System.exit(1);
        }

    }

    /**
     * SATELLITE THREAD
     * -----------------------------------------------------------------------------------------
     *  Inner Helper Method Class: instanciated in above server loop and processes single job requests
     *      --> take on Jobs, 
     *      --> get the right tool to do the job, 
     *      --> process the job & get the results back to whoever requested the job to be done.
     */
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        /**
         * SatelliteThread () 
         * -----------------------------------------------------------------------------------------
             * @param jobRequest
             * @param satellite
         */
        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        /**
         * run () 
         * -----------------------------------------------------------------------------------------
         */
        @Override
        public void run() {
            String toolType; // name of tool
            Tool tool; 
            
            // SETTING UP OBJECT STREAMS
            try {
                readFromNet = new ObjectInputStream(jobRequest.getInputStream()); 
                writeToNet  = new ObjectOutputStream(jobRequest.getOutputStream()); 
            
            } catch (Exception e) {
                System.err.println("[SatelliteThread.run] Unable to connect....." + e); 
                //System.exit(1);
            }
            
            
            // READING MESSAGE.
            try {
                // cast as a message
                message = (Message) readFromNet.readObject (); 
                
            } catch (Exception e) {
                System.err.println("[SatelliteThread.run] Unable to read message....." + e); 
                //System.exit(1);
            }
/*
            if (message == null) {
                System.err.println("[SatelliteThread.run] message is equal to null."); 
                return; 
            }
*/
            // DETERMINING WHICH TOOL TO USE.
            switch (message.getType()) {
                case JOB_REQUEST:
                    System.out.println("[SatelliteThread.run.JOB_REQUEST]");
                    // processing job request
                    Job job = (Job) message.getContent(); //create job
                    
                    // gets the right tool for the job
                    toolType = job.getToolName(); 
                    
                    System.out.println("[SatelliteThread.run] Looking in to tool: " + toolType); 
                    
                    // Tell client the results //TODO
                    try {
                        tool = getToolObject(toolType); 
                    } catch (Exception e) {
                        System.err.println("[SatelliteThread.run] Problem occured in "
                                                + "getToolObject()....." + e); 
                        return; 
                    }

                    Object properties = tool.go(job.getParameters()); 

                    try {
                        writeToNet.writeObject(properties); 
                    } catch (Exception e) {
                        System.err.println("[SatelliteThread.run] Problem occured in "
                                                + "getToolObject() trying to write to server ....." + e); 
                        return; 
                    }
                    break;

                default:
                    System.err.println("[SatelliteThread.run] something went wrong");
            }
        }
    }

    /**
     * getToolObject ()
     * -----------------------------------------------------------------------------------------
     * Aux method to get the appropriate tool object, given the class string. If the tool has been 
     * used before, it is returned immediately out of the cache, otherwise it is loaded dynamically.
     */
    public Tool getToolObject(String toolClassString) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Tool toolObject;

        //Check if tool is not already in cache
        if ((toolObject = (Tool) toolsCache.get(toolClassString)) == null) {     

            System.out.println("[SatelliteThread.getToolObject] Tool " 
                                + toolClassString + " is NOT already in the cache.");

            System.out.println("[SatelliteThread.getToolObject] Tool is not already in cache --> " 
                    + toolClassString + " is being added to the cache...");

            if (toolClassString == null) {
                return null; 
            }

            // load from web server (connected to classLoader) 
            Class toolClass = classLoader.loadClass(toolClassString);
            toolObject = (Tool) toolClass.newInstance();
            toolsCache.put(toolClassString, toolObject);
        } 
        // tool is in our cache
        else {
            
            System.out.println("[SatelliteThread.getToolObject] Tool " 
                                + toolClassString + " is already in the cache.");
        }
        
        
        return toolObject;

    }

    /**
     * main
     * -----------------------------------------------------------------------------------------
     */
    public static void main(String[] args) {
        
        if (args.length != 3) {
            // need to make arguments for: satellite, classLoader, & server properties!
            Satellite satelliteImprov = new Satellite(
                                "../../config/Satellite.Venus.properties", 
                                "../../config/WebServer.properties", 
                                "../../config/Server.properties");
            satelliteImprov.run(); 
        }
        else { // 3 arguments 
            Satellite satellite = new Satellite(args[0], args[1], args[2]);
            satellite.run();
        }
    }
}
