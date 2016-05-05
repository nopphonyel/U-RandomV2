package com.urandom.utech.cardviewsoundcloudversion;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nopphon on 4/25/16.
 */
public class TrackObject {

    private static final String TAG_INTERNET = "internet" , TAG_JSON = "json";

    public static final int URL_ERROR = 8751 , NO_CONNECTION = 9054 , LOAD_SUCCESFULLY = 7571 , AN_ERROR_HAS_OCCRED = 9999;
    private static final int JSON_OPTIMIZATION_ERROR = 3154;

    public static final int GET_BY_TAGS = 120, GET_BY_POPULAR = 172 , GET_BY_GENRES = 312;
    public static final int FALLBACK_API = 299731;
    private static final String MODE_EXPLORE = "/explore";
    private static final String POPULAR = "/Popular+Music";
    private static final String MODE_FILTER_TRACK = "/track/";

    private int apiVersion = 2;

    private JSONObject soundCloudJsonObject;
    private JSONArray tracks;

    public JSONArray getTrackList()
    {
        return tracks;
    }

    //Get json data following by choice.
    public void getTrack(int choice)
    {
        switch (choice){
            case GET_BY_POPULAR :
                Log.d(TAG_INTERNET , Config.API_V2_URL+MODE_EXPLORE+POPULAR+"?"+Config.CLIENT_ID);
                apiVersion=2;
                new fetchingTrackInBackground().execute(Config.API_V2_URL+MODE_EXPLORE+POPULAR+"?"+Config.CLIENT_ID);
                break;
            case GET_BY_GENRES:
                break;
            case GET_BY_TAGS:
                break;
            case FALLBACK_API:
                Log.d(TAG_INTERNET , Config.API_URL+MODE_FILTER_TRACK+"?"+Config.CLIENT_ID+"&genre=electronics&limit=800");
                apiVersion =1;
                new fetchingTrackInBackground().execute(Config.API_URL+MODE_FILTER_TRACK+"?"+Config.CLIENT_ID+"&genre=electronics&limit=800");
                break;
        }
    }

    private class fetchingTrackInBackground extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... urlStr) {
            try {
                URL url = new URL(urlStr[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(6 * 1000);
                Log.d(TAG_INTERNET , "Connection code is " + urlConnection.getResponseCode());
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    Log.d(TAG_INTERNET, "<YAY> Data now streaming : " + urlConnection.getContentLength());
                    InputStream soundCloudInputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(soundCloudInputStream, "UTF-8"));
                    String line;
                    StringBuffer soundCloudJsonStrBuff = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        soundCloudJsonStrBuff.append(line);
                        soundCloudJsonStrBuff.append("\n");
                    }
                    Log.d(TAG_INTERNET, "API VERSION = " + apiVersion);
                    bufferedReader.close();
                    if(apiVersion == 2)
                    {
                        return optimizeJsonToList(soundCloudJsonStrBuff.toString());
                    }
                    else if(apiVersion == 1) {
                        return optimizeJsonToListFallback(soundCloudJsonStrBuff.toString());
                    }
                } else {
                    return NO_CONNECTION;
                }
            } catch (MalformedURLException malform) {
                Log.e(TAG_INTERNET, "<!URL>" + malform.toString());
                return URL_ERROR;
            } catch (Exception e) {
                Log.e(TAG_INTERNET, "<!URL>" + e.toString());
                return AN_ERROR_HAS_OCCRED;
            }
            return LOAD_SUCCESFULLY;
        }

        protected void onPostExecute(Integer code) {
            Log.e(TAG_INTERNET , "RETURN CODE IS : "+code);
            if(code == LOAD_SUCCESFULLY) MainActivity.setVisibilityOfComponent(MainActivity.LOAD_SUCCESS);
            if(code == NO_CONNECTION || code == URL_ERROR || code == AN_ERROR_HAS_OCCRED) MainActivity.setVisibilityOfComponent(MainActivity.ERROR_LOAD);
        }
    }


    private int optimizeJsonToList(String jsonString)
    {
        try {
            soundCloudJsonObject = new JSONObject(jsonString);
            tracks = soundCloudJsonObject.getJSONArray("tracks");
            JSONObject jsonObjectGetter = new JSONObject();
            SCTrack scTrackPointer = new SCTrack();

            for(int i=0; i< tracks.length() ; i++){
                jsonObjectGetter = tracks.getJSONObject(i);
                scTrackPointer.setSongTitle(jsonObjectGetter.getString("title"));
                scTrackPointer.setArtWorkURL(jsonObjectGetter.getString("artwork_url"));
                scTrackPointer.setGenre(jsonObjectGetter.getString("genre"));
                scTrackPointer.setDuration(jsonObjectGetter.getString("duration"));
                scTrackPointer.setTrackURL(jsonObjectGetter.getString("uri"));
                scTrackPointer.setUser(jsonObjectGetter.getJSONObject("user"));
                SCTrackList.TRACK.add(scTrackPointer);
                scTrackPointer = new SCTrack();
            }
        } catch (JSONException e) {
            Log.e(TAG_JSON , "<!JSON> Json optimize error");
            return JSON_OPTIMIZATION_ERROR;
        }
        return LOAD_SUCCESFULLY;
    }

    private int optimizeJsonToListFallback(String jsonString)
    {
        try {
            tracks = new JSONArray(jsonString);
            JSONObject jsonObjectGetter = new JSONObject();
            SCTrack scTrackPointer = new SCTrack();
            for(int i=0; i< tracks.length() ; i++){
                jsonObjectGetter = tracks.getJSONObject(i);
                scTrackPointer.setSongTitle(jsonObjectGetter.getString("title"));
                scTrackPointer.setArtWorkURL(jsonObjectGetter.getString("artwork_url"));
                scTrackPointer.setGenre(jsonObjectGetter.getString("genre"));
                scTrackPointer.setDuration(jsonObjectGetter.getString("duration"));
                scTrackPointer.setTrackURL(jsonObjectGetter.getString("uri"));
                scTrackPointer.setUser(jsonObjectGetter.getJSONObject("user"));
                SCTrackList.TRACK.add(scTrackPointer);
                scTrackPointer = new SCTrack();
            }
        } catch (JSONException e) {
            Log.e(TAG_JSON , "<!JSON> Json optimize error");
            return JSON_OPTIMIZATION_ERROR;
        }
        Log.d(TAG_JSON , "Covert successfully");
        return LOAD_SUCCESFULLY;
    }
}
