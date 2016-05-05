package com.urandom.utech.cardviewsoundcloudversion;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by nopphon on 4/18/16.
 */
public class SCTrack extends TrackType{

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public void setTrackURL(String trackURL) {
        this.trackURL = trackURL;
    }

    public void setArtWorkURL(String artWorkURL) {
        this.artWorkURL = artWorkURL;
    }

    public void setUser(JSONObject user) {
        this.user = user;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public SCTrack(){

    }

    private String songTitle;

    private String trackID;

    private String trackURL;

    private String artWorkURL;

    private JSONObject user;

    private String genre;

    private String duration;

    public String getTrackDuraion()
    {
        int time = Integer.parseInt(duration);
        return time/60000 + " min " + (time%60000)/1000 + " sec.";
    }

    public String getTrackGenre()
    { return genre;}

    public String getUserName() throws JSONException {
        return user.get("full_name").toString();
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getTrackID() {
        return trackID;
    }

    public String getTrackURL() {
        return trackURL;
    }

    public String getArtWorkURL() {
        return artWorkURL;
    }

}
