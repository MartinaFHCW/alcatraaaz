/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraaaz.server;

import java.io.Serializable;

/**
 *
 * @author MartinaB
 */
public class AlcatrazMessage implements Serializable {
    String body;
    
    public AlcatrazMessage(String body) {
        this.body = body;
    }
    
    String getHeader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    String getBody() {
        return body;
    }
    
}
