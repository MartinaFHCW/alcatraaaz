/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraaaz.server;

import alcatraaaz.common.Player;
import alcatraaaz.common.Util;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import spread.*;

/**
 *
 * @author MartinaB
 */
public class SpreadServer implements AdvancedMessageListener {
   // Constans
    private final static InetAddress DEFAULT_ADDR = null;
    private final static int         DEFAULT_PORT = 0;
    
    // spread configuration 
    private String groupName;
    
    // Master Server 
    private String       groupMasterServerName = null;
    private String       groupMasterServerAddr = null;
    private Boolean      i_am_MasterServer     = false;
    private LinkedList   serverList            = new LinkedList();
        
    // keep track of playerlist to send it to new group members
    private List<Player>       playerList   = new ArrayList<Player>();
    
    // connection and listner   
    private SpreadConnection connection   = new SpreadConnection();
    private SpreadGroup      group        = new SpreadGroup();
    
    // Synchronisation
    private final Object syncGroupMasterServerAddress = new Object();
    
    // Logger    
    private static Logger l               = Util.getLogger();
        
    /**
     * Open Spread Server Connection
     * 
     * @param address       Spread Server Address      null = default localhost
     * @param port          Spread Server Port            0 = default 4803
     * @param privateName   Name of this connection    must be unique per spread server
     * @param groupName     Name of the group          null = don't join a group
     * 
     * @return Connection
     */    
    public SpreadServer(InetAddress address, int port, String privateName, String groupName) throws SpreadException
    {        
        this.groupName = groupName;
    
        // set listener
    //    playerList.addObjectChangedListner(this);   
        
        // Connect
        connection.connect(address, port, privateName, false, (groupName != null));
        
        // Join Group
        group.join(connection, this.groupName);
        
        //Add Listener    
        l.debug("SPREAD: Setup listener");   
        /*
         * Spread 4.2.0     SpreadConnection.java
         * 
         * The Spread Listener (1601: private class Listener extends Thread)
         * does not provide any kind of error handling in case the connection
         * brakes. Then the error message is simply printed to System.out 
         * (1656, 1866). Thus we can't discover if we lost the connection.
         * 
         * Spread does not provide any service to check if the connection is alive.
         * All methods which performe read() are private, and all methods which are
         * public, check if a listener is attached. Anyway read() would be hard to 
         * handle.
         * 
         * Therefore the only way is to multicast and to check if write() fails. 
         * If so, we assume that the connection is dead.
         * 
         */
        connection.add(this);                
    }
    
    public void sync()
    {
        /*
         * The constructor attaces the Listener and returns. 
         * i.e. this server is not in sync with the other server.
         * 
         * After the master received the memebership information, it will 
         * multicast the relevant information.
         * 
         * If this is the first server it sets its own address.
         * 
         * In any case all relevant information is received, once the master
         * server address is set.
         * 
         */
        
        boolean sync;
                
        l.info("SPREAD: Sync...");
        
        synchronized(syncGroupMasterServerAddress)
        {
            sync = (groupMasterServerAddr != null);
        
            while(!sync) 
            {
                try { syncGroupMasterServerAddress.wait(); break; } 
                catch (InterruptedException e) {  }
            };   
        }
        
        l.info("SPREAD: Synced. Master Server is: " + groupMasterServerAddr);
    }
    
    public boolean isSynced()
    {
        synchronized(syncGroupMasterServerAddress)
        {
            return (groupMasterServerAddr != null);
        }
    }
    
    /**
     * Close Spread Server Connection
     */
    public void close()
    {
        try { group.leave();                               } catch(Exception e) { }
        try { connection.remove(this);                 } catch(Exception e) { }
        try { connection.disconnect();                     } catch(Exception e) { }
      //  try { playerList.removeObjectChangedListner(this); } catch(Exception e) { }
    }
    
    public static InetAddress getAddr(String name)
    {
        InetAddress addr = DEFAULT_ADDR; 
        
        try 
        {
            addr = InetAddress.getByName(name); 
        } 
        catch (UnknownHostException e) 
        {
            l.warn("Can't resolve spread host address: " + name + "\n" + e.getMessage());
            l.info("Using default address...");
        }
        
        return addr;
    }
    
    public static int getPort(String port)
    {
        int intPort = DEFAULT_PORT;
                
        try
        {
            intPort = Integer.parseInt(port);
        }
        catch(NumberFormatException e)
        {
            l.warn("Can't parse spread host port: " + port + "\n" + e.getMessage());
            l.info("Using default port...");
        }
        
        return intPort;
    }
    
    // getters and setters for internal data structure 
    public void addMemberServer(String name) {
        serverList.add(name);
    }

    public void updateMemberServer(LinkedList serverList) {
        this.serverList = serverList;
    }
    
    public LinkedList getMemberServer() {
        return serverList;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public SpreadGroup getPrivateGroup() {
        return connection.getPrivateGroup();
    }

    public void setMasterServer(String name) 
    {     
        groupMasterServerName = name;
        
        if (name.equalsIgnoreCase(getPrivateGroup().toString())) 
        {
            this.i_am_MasterServer = true;
              
            synchronized(syncGroupMasterServerAddress)
            {
                this.groupMasterServerAddr = Util.getMyServerAddress();
                syncGroupMasterServerAddress.notifyAll();
            
                l.debug("SPREAD: Master Server Host Address: " + this.groupMasterServerAddr);
            }
        }
    }
    
    public void setMasterServerAddress(String address) 
    {
        synchronized(syncGroupMasterServerAddress)
        {
            this.groupMasterServerAddr = address;  
            syncGroupMasterServerAddress.notifyAll();
        }
    }

    public String getMasterServerName() 
    {
        return this.groupMasterServerName;
    }
    
    public String getMasterServerAddress() 
    {
        if(!isSynced())
        {
            sync();
        } 
            
        synchronized(syncGroupMasterServerAddress)
        {
            return this.groupMasterServerAddr;
        }
    }

    public boolean i_am_MasterServer() {
        return i_am_MasterServer;
    }

    public void setPlayerList(List<Player> playerList) {
        this.playerList.clear();
        this.playerList.addAll(playerList);
    }

    public List<Player> getPlayerList() {
        return playerList;
    }
    // remove a server and start election if appropriate 

    public void removeServer(String server) 
    {
        // remove server from list caused by spread leave or disconnect
        l.debug("SPREAD: Remove server node from list (" + server + ")");
        serverList.remove(server);
        
        // the master has gone
        if (server.equalsIgnoreCase(groupMasterServerName)) 
        {
            // elect new master server and master messages other nodes
            // election:
            if (getPrivateGroup().toString().equalsIgnoreCase((String) serverList.getFirst())) 
            {
                l.debug("SPREAD: This server node is new MASTER");
                // new master updates other server nodes
                setMasterServer(getPrivateGroup().toString());
            }
        }

      /*  if (i_am_MasterServer) 
        {
            this.multicastMasterServerInformation();
            this.multicastMasterHostAddress();
            this.multicastServerList();
        }*/
    }

    // Spread Group Messages
    private void multicastMessage(AlcatrazMessage message) 
    {
        try 
        {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(message);     // Send one Java object 
            msg.addGroup(groupName);    // Specify a group to send the message to
            msg.setReliable();          // Set the message to be reliable.
            msg.setFifo();              // Set the delivery method to FiFo
            msg.setSelfDiscard(true);   // This message should not be sent back to the user who is sending it
            connection.multicast(msg);  // Send the message!

            l.debug("SPREAD: Multicast message: " + message.getHeader() + "\n" + message.getBody());
        } 
        catch (SpreadException e) 
        {
            // We assume that the connection is dead
            l.fatal("SPREAD: " + e.getMessage());
            System.exit(1);
        } 
    }
    
    public void multicastPlayerList() 
    {
        // update servers nodes 
        multicastMessage(new AlcatrazMessage("test"));
  //      multicastMessage(new AlcatrazMessage(MessageHeader.PLAYER_LIST, playerList.getLinkedList()));
    }

 /*   public void multicastServerList() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.SERVER_LIST, serverList));
    }

    public void multicastMasterServerInformation() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER, groupMasterServerName));
    }

    public void multicastMasterHostAddress() 
    {
        synchronized(syncGroupMasterServerAddress)
        {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER_ADDRESS, groupMasterServerAddr));
        }
    }
*/
    /*@Override
    public void updateObject(ObjectChangedEvent event) 
    {
        // the playerList was change
        // spread modified playerList to other server nodes
        l.debug("SPREAD: PlayerList was changed.");
        this.multicastPlayerList();
    }*/

    @Override
    public void regularMessageReceived(SpreadMessage sm) {
        try {
            System.out.println("Regular message received: "+((AlcatrazMessage)sm.getObject()).getBody());
        } catch (SpreadException ex) {
            l.error("",ex);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage sm) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
