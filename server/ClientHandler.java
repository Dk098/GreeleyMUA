package GreeleyMUA.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Callable;
import greeleysmtpserver.parser.*;
import greeleysmtpserver.responder.*;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yasgur99
 */
public class ClientHandler implements Callable<Void> {

    private final static Logger requestLogger = Logger.getLogger("requests");
    private final static Logger errorLogger = Logger.getLogger("errors");

    private Socket connection;
    private BufferedReader in;
    private PrintWriter out;
    private boolean connected;

    public ClientHandler(Socket connection) {
        this.connection = connection;
        this.connected = true;
        requestLogger.info("Connection opened with " + connection.getInetAddress().getHostAddress());
    }

    @Override
    public Void call() {
        setupStreams();
        //TODO: check to make sure streams are setup

        /*Continously get instructions from client while connection is open*/
        while (connected) {
            
        }
        closeConnection();
        return null;
    }

    private boolean setupStreams() {
        try {
            this.in = new BufferedReader(
                    new InputStreamReader(
                            this.connection.getInputStream()));
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
            line = in.readLine(); //try and read line from server
            if (line != null) {
                line = line.trim();
                SMTPCommand command = SMTPParser.parse(line);
                if (command.getCommand() == SMTPParser.DATA)
                    return readDataFromClient((SMTPDataCommand) command);
                else
                    return command.execute(session);
            }
        } catch (IOException ex) { //error reading
            this.connected = false;
        } catch (RuntimeException rex) { //we read in nothing
            errorLogger.log(Level.SEVERE, "Unexpected error reading client message" + rex.getLocalizedMessage(), rex);
            this.connected = false;
        }
        return null;
    }

    private SMTPResponse readDataFromClient(SMTPDataCommand command) {
        writeResponse(command.execute(session));
        while (!command.isDone()) {
            String line;
            try {
                line = in.readLine(); //try and read line from server
                if (line != null)
                    command.addData(line, session);
            } catch (IOException ex) { //error reading
                this.connected = false;
                return null;
            } catch (RuntimeException rex) { //we read in nothing
                errorLogger.log(Level.SEVERE, "Unexpected error reading client message" + rex.getLocalizedMessage(), rex);
                this.connected = false;
                return null;
            }
        }
        SMTPResponse response = command.execute(session);
        session.setShouldSend(true);
        return response;
    }

    private void writeResponse(SMTPResponse response) {
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
