package appserver.server;

import appserver.comm.ConnectivityInfo;
import java.util.Enumeration;
import java.util.Hashtable;

/** ASSIGNMENT 8: APPLICATION SERVER - APPLICATION SERVER
	 * 	@author Chloe Bates 	
	 * 	@author Sam Gilb	
	 * 	@author Colton Spector
 * Keeps track of all satellites that have registered. For that you need to implement functionality: 
 *      - that allows a satellite server to register itself with the application server, 
 *        providing its connectivity information (i.e. name, port #).
 *      - that responds to registration requests and forwards the information 
 *        received to its satellite manager. 
 */
public class SatelliteManager {

    // (the one) hash table that contains the connectivity information of all satellite servers
    static private Hashtable<String, ConnectivityInfo> satellites = null;

    public SatelliteManager() {
        satellites = new Hashtable<String, ConnectivityInfo>(); 
    }

    public void registerSatellite(ConnectivityInfo satelliteInfo) {
        String satelliteName = satelliteInfo.getName();
        satellites.put(satelliteName, satelliteInfo); 
        System.out.println("[SatelliteManager.registerSatellite] Satellite " 
                            + satelliteName + " is now registered."); 
    }

    public ConnectivityInfo getSatelliteForName(String satelliteName) {
        return satellites.get(satelliteName); 
    }
    
}
