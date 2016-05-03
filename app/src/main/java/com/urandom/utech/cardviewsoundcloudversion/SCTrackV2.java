package com.urandom.utech.cardviewsoundcloudversion;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nopphon on 4/25/16.
 */
public class SCTrackV2 {

    @SerializedName("tracks")
    private JSONArray tracks;

    public JSONArray getTrackList()
    {
        return tracks;
    }
}
