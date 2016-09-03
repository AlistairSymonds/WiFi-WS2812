package com.sayfog.homeconnect;

import java.util.ArrayList;

/**
 * Created by alist on 26/08/2016.
 */
public class CmdBundle {
    private String bundleName;
    private ArrayList<CmdDefinition> cmds;
    private Class viewClass;

    public CmdBundle(String name, Class viewClass){
        this.bundleName = name;
        this.viewClass = viewClass;

        cmds = new ArrayList<CmdDefinition>();
    }

    public String getName(){
        return this.bundleName;
    }

    public void addCmd(CmdDefinition cmdIn){
        cmds.add(cmdIn);
    }

    public Class getViewClass(){
        return this.viewClass;
    }
    public ArrayList<CmdDefinition> getAllCmds(){
        return this.cmds;
    }
}
