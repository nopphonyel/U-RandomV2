package com.urandom.utech.cardviewsoundcloudversion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nopphon on 4/25/16.
 */
public class SCTrackV2 {

    @SerializedName("tracks")
    private List<SCTrack> tracks;

    public List<SCTrack> getTrackList()
    {
        return tracks;
    }
}
