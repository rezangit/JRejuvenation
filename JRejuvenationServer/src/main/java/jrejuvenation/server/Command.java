package jrejuvenation.server;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author Reza Azizi (azizi.ra@gmail.com)
 * @version 1.0.1
 */
public class Command implements Serializable{
    public static final int QUERY_MASTER        = 10;
    public static final int QUERY_MASTER_RESPOSE= 20;
    public static final int NETWORK_TEST_MSG    = 30;
    public static final int SYNC_DATA_MSG       = 40;
    
    private int command;
    private int data;
    private String str;
    private HashMap map;

    public Command(int command, int data) {
        this.command = command;
        this.data = data;
    }

    public Command(int command, String str) {
        this.command = command;
        this.str = str;
    }
    
    public Command(int command, int data, HashMap map) {
        this.command = command;
        this.map = map;
        this.data = data;
    }

    
    /**
     * @return the command
     */
    public int getCommand() {
        return command;
    }

    /**
     * @return the data
     */
    public int getData() {
        return data;
    }

    /**
     * @return the str
     */
    public String getStr() {
        return str;
    }

    /**
     * @return the map
     */
    public HashMap getMap() {
        return map;
    }
    
    
    
}
