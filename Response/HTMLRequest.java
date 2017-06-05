
package GreeleyMUA.Response;
/**
 * @author michaelmaitland
 */

public class HTMLRequest {

    private String username;
    private String md5Password;
    
    public HTMLRequest (String postRequest){
        parse(postRequest);
    }
    
    public void parse(String postRequest){
        
    }

    public String getUsername() {
        return username;
    }

    public String getMd5Password() {
        return md5Password;
    }
}
