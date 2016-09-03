package com.sayfog.homeconnect;

/**
 * Created by alist on 26/08/2016.
 */
public class CmdDefinition {
    private String name;
    private boolean expectsResponse;
    private String messageOutBase;
    private int numberOfBytesAdded; //-1 for a variable number of args, this however means it must
                                    // be properly headered if going over multiple connections

    public CmdDefinition(String nameIn){
        this.name = nameIn;
    }

    public CmdDefinition(String nameIn, boolean expectsResponseIn, String messageOutBaseIn, int numberOfBytesAddedIn){
        this.name = nameIn;
        this.expectsResponse = expectsResponseIn;
        this.messageOutBase = messageOutBaseIn;
        this.numberOfBytesAdded = numberOfBytesAddedIn;
    }
    public String getName(){
        return this.name;
    }

    public int getNumberOfBytesAdded(){
        return this.numberOfBytesAdded;
    }

    public String getMessageOutBase(){
        return this.messageOutBase;
    }



}
