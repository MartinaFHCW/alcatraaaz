/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraaaz.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author MartinaB
 */
public interface IGameServer extends Remote{
    public LinkedList<Player> getPlayerList()                                        throws RemoteException;
    public Player register  (String name, String address, int port) throws RemoteException;
}
