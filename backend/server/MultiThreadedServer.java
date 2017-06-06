package GreeleyMUA.backend.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yasgur99
 */
public class MultiThreadedServer {

    private final static int POOL_SIZE = 50;
    private final static Logger errorLogger = Logger.getLogger("server errors");
    private final static Logger requestLogger = Logger.getLogger("requests");

    private ServerSocket serverSocket;
    private int port;
    private boolean running;

    public MultiThreadedServer(int port) {
        this.port = port;
        this.running = false;
    }

    public void start() {
        this.running = true;
        initServer();
        closeDownServer();
    }

    private void initServer() {
        try {
            this.serverSocket = new ServerSocket(port);
            makeConnection();
        } catch (IOException ex) {
            errorLogger.log(Level.SEVERE, "Error opening server socket" + ex.getLocalizedMessage(), ex);
        } catch (RuntimeException rex) {
            errorLogger.log(Level.SEVERE, "Runtime exception opening socket" + rex.getLocalizedMessage(), rex);
        }
    }

    private void makeConnection() {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
        while (running) {
            try {
                Socket newClient = this.serverSocket.accept();
                try {
                    newClient.setSoTimeout(180000);
                } catch (SocketException ex) {
                }
                Callable<Void> task = new ClientHandler(newClient);
                pool.submit(task);
            } catch (IOException ex) {
                errorLogger.log(Level.SEVERE, "Accept error" + ex.getLocalizedMessage(), ex);
            } catch (RuntimeException rex) {
                errorLogger.log(Level.SEVERE, "Unexpected error" + rex.getLocalizedMessage(), rex);
            }
        }
    }

    private void closeDownServer() {
        try {
            this.serverSocket.close();
            System.out.println("Server shutdown");
        } catch (IOException ex) {
            errorLogger.log(Level.SEVERE, "Error closing server socket" + ex.getLocalizedMessage(), ex);
        } catch (RuntimeException rex) {
            errorLogger.log(Level.SEVERE, "Unexpected error opening server socket" + rex.getLocalizedMessage(), rex);
        }
    }

    public void shutdown() {
        this.running = false;
    }
}
