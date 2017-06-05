package GreeleyMUA.server;

import GreeleyMUA.Response.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yasgur99
 */
public class ClientHandler implements Callable<Void> {

    private final static Logger requestLogger = Logger.getLogger("requests");
    private final static Logger errorLogger = Logger.getLogger("errors");

    private Socket connection;
    private Reader in;
    private PrintWriter out;
    private boolean connected;
    private HTMLRequest postRequest;

    public ClientHandler(Socket connection) {
        this.connection = connection;
        this.connected = true;
        requestLogger.info("Connection opened with " + connection.getInetAddress().getHostAddress());
    }

    @Override
    public Void call() {
        setupStreams();
        //TODO: check to make sure streams are setup
        while (connected) {
            readClientMessage();
            HTMLResponse query = query();
            writeResponse(query);
        }
        closeConnection();
        return null;
    }

    private boolean setupStreams() {
        try {
            this.in
                    = new InputStreamReader(
                            this.connection.getInputStream());
            this.out = new PrintWriter(
                    new OutputStreamWriter(
                            this.connection.getOutputStream()));
            return true;
        } catch (IOException ex) {
            this.connected = false;
        } catch (RuntimeException rex) {
            errorLogger.log(Level.SEVERE, "Error opening streams" + rex.getLocalizedMessage(), rex);
        }
        return false;
    }

    private HTMLResponse readClientMessage() {
        String line;
        try {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1)
                sb.append((char) c);
            //create formatted request
            postRequest = new HTMLRequest(sb.toString());
        } catch (IOException ex) { //error reading
            this.connected = false;
        } catch (RuntimeException rex) { //we read in nothing
            errorLogger.log(Level.SEVERE, "Unexpected error reading client message" + rex.getLocalizedMessage(), rex);
            this.connected = false;
        }
        return null;
    }

    private HTMLResponse query() {
        List<String> messages = DatabaseManager.getInstance().
                getMessages(postRequest.getUsername(), postRequest.getMd5Password());
        //need to format response full of mail
        return new HTMLResponse();//create empty response
    }

    private void writeResponse(HTMLResponse response) {
        try {
            this.out.write(response.toString() + "\r\n");
            this.out.flush();
        } catch (RuntimeException rex) {
            errorLogger.log(Level.SEVERE, "Error writing response" + rex.getLocalizedMessage(), rex);
        }
    }

    private void closeConnection() {
        try {
            this.in.close();
            this.out.close();
            this.connection.close();
        } catch (IOException ex) {
        } catch (RuntimeException rex) {
            errorLogger.log(Level.SEVERE, "Error closing connection" + rex.getLocalizedMessage(), rex);
        }
        this.connected = false;
        requestLogger.info("Connection closed with " + connection.getInetAddress().getHostAddress());
    }
}
