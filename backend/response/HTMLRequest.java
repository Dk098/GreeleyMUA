package GreeleyMUA.backend.response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author michaelmaitland
 */
public class HTMLRequest {

    private String username;
    private String md5Password;
    private boolean validForm;

    public HTMLRequest(String postRequest) {
        parse(postRequest);
    }

    public void parse(String postRequest) {
        try {
            String decodedRequest = URLDecoder.decode(postRequest, "UTF-8");
            if (!decodedRequest.matches("&username.*&password.*")){
                validForm = false;
                return;
            }

            if (decodedRequest.contains("&username=")) {
                int start = decodedRequest.indexOf("&username=") + 10;
                int end = decodedRequest.indexOf("&password=");
                username = decodedRequest.substring(start, end);
            }
            if (decodedRequest.contains("&password=")) {
                int start = decodedRequest.indexOf("&password=") + 10;
                md5Password = decodedRequest.substring(start);
            }
            validForm = true;
        } catch (UnsupportedEncodingException ex) {
        }
    }

    public String getUsername() {
        return username;
    }

    public String getMd5Password() {
        return md5Password;
    }
}
