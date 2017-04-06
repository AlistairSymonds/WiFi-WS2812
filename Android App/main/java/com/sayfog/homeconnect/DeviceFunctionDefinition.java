package com.sayfog.homeconnect;

import java.util.HashMap;

/**
 * Created by alist on 26/08/2016.
 */
public class DeviceFunctionDefinition {
    private String name;
    private byte byteId;
    private HashMap<String, CmdBundle> bundles;
    private Class viewActivity;

    public DeviceFunctionDefinition(byte idIn, String name, Class viewActivity){
        this.byteId = idIn;
        this.name = name;
        bundles = new HashMap<String, CmdBundle>();
        this.viewActivity = viewActivity;
    }

    public DeviceFunctionDefinition(byte idIn, String name){
        this.byteId = idIn;
        this.name = name;
        bundles = new HashMap<>();

    }

    public void addBundle(CmdBundle bundle){
        this.bundles.put(bundle.getName(), bundle);
    }

    public CmdBundle getBundle(String key){
       return bundles.get(key);
    }

    public String getName(){
        return this.name;
    }

    public  HashMap<String, CmdBundle> getAllBundles(){
       return this.bundles;
    }

    public Class getViewClass(){
        return this.viewActivity;
    }

    public byte getByteID(){
        return this.byteId;
    }


}
