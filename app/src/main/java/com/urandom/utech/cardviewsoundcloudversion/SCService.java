package com.urandom.utech.cardviewsoundcloudversion;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by nopphon on 4/21/16.
 */
public interface SCService {

    String LIMITER = "&limit=800";
    //What does really @GET mean??
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    //What the f___ really @Query meaning to????
    void getRecentTracks(@Query("created_at[from]") String date, Callback<ArrayList<SCTrack>> cb);
    //Callback?? why need a List??

    @GET("/tracks/?client_id=" + Config.CLIENT_ID)
    void getSpecificTracks(@Query(LIMITER+"genres") String genres , Callback<ArrayList<SCTrack>> cb);

    @GET("/tracks/?client_id=" + Config.CLIENT_ID)
    void getPopularTrack(@Query("playback\\_100000") Callback<ArrayList<SCTrack>> cb);

}
