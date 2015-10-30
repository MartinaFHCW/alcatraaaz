/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraaaz.server;

import alcatraaaz.common.IGameServer;
import alcatraaaz.common.Player;
import alcatraaaz.server.SpreadServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MartinaB
 */
public class ServerRMI extends UnicastRemoteObject implements IGameServer {
    SpreadServer spreadServer;
    
    public ServerRMI (SpreadServer spreadServer) throws RemoteException{
        this.spreadServer = spreadServer;
    }

    @Override
    public LinkedList<Player> getPlayerList() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Player register(String name, String address, int port) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ServerRMI srv = new ServerRMI(new SpreadServer(InetAddress.getLocalHost(), 4803, "privateName", "groupName"));
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://localhost:1099/ServerRMI", srv);
        } catch (Exception ex) {
            Logger.getLogger(ServerRMI.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
}
