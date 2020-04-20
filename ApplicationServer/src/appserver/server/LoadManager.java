package appserver.server;

import java.util.ArrayList;

/** ASSIGNMENT 8: APPLICATION SERVER - APPLICATION SERVER
	 * 	@author Chloe Bates 	
	 * 	@author Sam Gilb	
	 * 	@author Colton Spector
 * This manager can potentially implement any load balancing policy. It responds 
 * to requests from the application server asking for the next satellite to do 
 * a job. For simplicity, I want you to implement a "round-robin" policy. 
 * Whenever a satellite server registers with the client, the load manager is 
 * also informed, so that it knows about the existence of the new satellite server.
 * 
 */
public class LoadManager {

    static ArrayList satellites = null;
    static int lastSatelliteIndex = -1;

    public LoadManager() {
        satellites = new ArrayList<String>();
    }

    public void satelliteAdded(String satelliteName) {
        // add satellite
        satellites.add(satelliteName); 
        System.out.println("[LoadManager.satelliteAdded] Satellite " 
                            + satelliteName + " is now added."); 
    }


    public String nextSatellite() throws Exception {
        
        int numberSatellites;
        
        synchronized (satellites) {
            // implement policy that returns the satellite name according to a round robin methodology
            // ...
        }

        return // ... name of satellite who is supposed to take job
        ;
    }
}
