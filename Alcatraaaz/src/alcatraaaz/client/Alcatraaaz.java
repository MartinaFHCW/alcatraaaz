/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraaaz.client;

import alcatraaaz.common.IGameServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MartinaB
 */
public class Alcatraaaz {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            IGameServer srv = (IGameServer) Naming.lookup("rmi://localhost:1099/ServerRMI");
            srv.register("name", "address", 1234);
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }
    
}
