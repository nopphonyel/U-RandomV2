package com.urandom.utech.cardviewsoundcloudversion;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * To contain all information in each track
 * Created by nopphon on 4/18/16.
 */
public class SCTrack {

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

    public SCTrack() {

    }

    private JSONObject mainObject;

    private String songTitle;

    private String trackID;

    private String trackURL;

    private String artWorkURL;

    private JSONObject user;

    private String genre;

    private String duration;

    private String streamURL;

    public String getLargeArtWorkURL() {
        return artWorkURL.replace("-large.jpg", "-t500x500.jpg");
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

    public String getTrackDuraion() {
        int time = Integer.parseInt(duration);
        return time / 60000 + " min " + (time % 60000) / 1000 + " sec.";
    }

    public String getTrackMilisecond() {
        return duration;
    }

    public String getTrackGenre() {
        return genre;
    }

    public String getUserName() throws JSONException {
        return user.get("username").toString();
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

    public JSONObject getUser(){return user;}

    public JSONObject getJSONMainObject(){
        return mainObject;
    }

    public void setMainObject(JSONObject mainObject) {
        this.mainObject = mainObject;
    }

    @Override
    public String toString(){
        return songTitle + "\n" + genre + "\n" + trackID + "\n" + trackURL + "\n" + artWorkURL + "\n" + duration + "\n";
    }
}
