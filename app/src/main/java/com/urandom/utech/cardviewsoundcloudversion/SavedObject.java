package com.urandom.utech.cardviewsoundcloudversion;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Saved object
 * Created by nopphonyel on 5/24/16.
 */
public class SavedObject implements Serializable {
    private static final long serialVersionUID = 8730368233789769177L;
    private HashMap<String , SCTrack> savedMap;

    public SavedObject(HashMap<String , SCTrack> setMap){
        savedMap = new HashMap<String, SCTrack>();
        setSavedMap(setMap);
    }

    public void setSavedMap(HashMap<String , SCTrack> setMap){
        for(String id : setMap.keySet()){
            savedMap.put(id , setMap.get(id));
        }
    }

    public HashMap<String , SCTrack> getSavedMap(){
        return savedMap;
    }
}
