
package GreeleyMUA.backend;

import GreeleyMUA.backend.response.DatabaseManager;
import GreeleyMUA.backend.server.MultiThreadedServer;

/**
 * @author michaelmaitland
 */

public class Main {

    public static void main(String[] args){
        DatabaseManager database = DatabaseManager.getInstance();
        /*Create and run server so it is listening for connections*/
        MultiThreadedServer server = new MultiThreadedServer(4442);
        server.start();
    }
}
