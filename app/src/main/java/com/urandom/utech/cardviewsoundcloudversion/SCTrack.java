package com.urandom.utech.cardviewsoundcloudversion;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nopphon on 4/18/16.
 */
public class SCTrack extends TrackType{

    @SerializedName("title")
    private String songTitle;

    @SerializedName("id")
    private String trackID;

    @SerializedName("stream_url")
    private String trackURL;

    @SerializedName("artwork_url")
    private String artWorkURL;

    @SerializedName("user")
    private Object user;

    @SerializedName("genre")
    private String genre;

    @SerializedName("duration")
    private String duration;

    public String getTrackDuraion()
    {
        int time = Integer.parseInt(duration);
        return time/60000 + " min " + (time%60000)/1000 + " sec.";
    }

    public String getTrackGenre()
    { return genre;}

    public String getUserName() {
        String user = this.user.toString();
        int position_from = user.indexOf("username=")+9;
        int position_to = user.indexOf("last_modified=")-2;
        return user.substring(position_from,position_to);
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

    protected int getTrackType() {
        return TrackType.SC_TRACK_TYPE;
    }
}
