package nl.dcs.network.server;

/**
 * Created by Ivanis on 10/04/2014.
 */

/**
 * Abstract class to initialize a thread with a server instance to do something in parallel
 */
public abstract class Sender extends Thread implements Runnable{

    protected Server server;

    public Sender (Server s){
        this.server = s;
    }



}
