
package GreeleyMUA.backend.response;

import java.util.List;

/**
 * @author michaelmaitland
 */

public class HTMLResponse {

    private List<String> messages;
    
    public HTMLResponse(List<String> messages){
        this.messages = messages;
    }
    
    @Override
    public String toString(){
        if(messages == null) return "No Messages";
        else return messages.toString();
    }
}
