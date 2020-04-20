package appserver.client;

import appserver.comm.Message;
import appserver.comm.MessageTypes;
import appserver.job.Job;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import utils.PropertyHandler;
/** ASSIGNMENT 8: APPLICATION SERVER - APPLICATION SERVER
	 * 	@author Chloe Bates 	
	 * 	@author Sam Gilb	
	 * 	@author Colton Spector
 *
 * 
 */
public class FibonacciClient implements MessageTypes{

    String host = null;
    int port;

    Properties properties;

    public FibonacciClient(String serverPropertiesFile) {
        try {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[FibonacciClient.FibonacciClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[FibonacciClient.FibonacciClient] Port: " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        PlusOneClient client = null;
        for(int i=46; i>0; i--)
        {
          (new FibonacciClient("../../config/Server.properties", i)).start();
        }
    }

}