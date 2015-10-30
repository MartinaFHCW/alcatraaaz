/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alcatraaaz.common;

import java.io.Serializable;

/**
 *
 * @author MartinaB
 */
public class Player implements Serializable {
    private String  address;
    private int     port;
    private boolean ready;
    private IGameClient proxy;
    private String  rmiURI;

    public Player(String name, int id, String address, int port, boolean ready) 
    {
        //super(id);
        //super.setName(name);        
        this.address = address;
        this.port    = port;
        this.ready   = ready;
        this.proxy   = null;
    }       

    public String getAddress() 
    {
        return address;
    }

    public void setAddress(String address) 
    {
        this.address = address;
        this.rmiURI  = null;
    }

    public int getPort() 
    {
        return port;
    }

    public void setPort(int port) 
    {
        this.port    = port;
        this.rmiURI  = null;
    }
    
    public void setProxy(IGameClient proxy) 
    {
        this.proxy = proxy;
    }
    
    public IGameClient getProxy() 
    {
        return proxy;
    }
     
   /* public String getRmiURI()
    {
        if(this.rmiURI == null)
        {
            this.rmiURI = Util.buildRMIString(this.address, this.port,
                          Util.getClientRMIPath(), super.getName());
        }
        
        return this.rmiURI;
    }*/
    
    public String getRmiURI(String name)
    {
        return this.rmiURI = Util.buildRMIString(this.address, this.port,
                             Util.getClientRMIPath(), name);
    }
              
    public boolean isReady() 
    {
        return ready;
    }

    public void setReady(boolean ready) 
    {
        this.ready = ready;
    }
    
   /* @Override
    public String toString() 
    {
        return "Player{" + "name=" + super.getName() + ", id=" + super.getId() 
                + ", address=" + address + ", port=" + port 
                + ", ready=" + ready + '}';
    }*/
}
