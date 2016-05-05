package com.urandom.utech.cardviewsoundcloudversion;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by nopphon on 4/25/16.
 */
public interface SCServiceV2 {
    //https://api.soundcloud.com/explore/sounds/category?limit=10&offset=0&linked_partitioning=1

    String LIMITER = "&limit=200&offset=0&linked_partitioning=0";

    @GET("/explore/Popular+Music?client_id="+Config.CLIENT_ID)
    void getPopularTrack(@Query(LIMITER) Callback<TrackObject> cb);

    @GET("/")
    void getPopularTrackByGenre(@Query("/explore/") String genre , Callback<ArrayList<SCTrack>> cb);
}
