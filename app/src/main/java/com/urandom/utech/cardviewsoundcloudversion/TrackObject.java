package com.urandom.utech.cardviewsoundcloudversion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is a track list management for track list
 * Created by nopphon on 4/25/16.
 */
public class TrackObject implements Serializable {

    public static final String TAG_INTERNET = "internet", TAG_JSON = "json", TAG_FILE_IMPORT = "importObj", TAG_FILE_EXPORT = "exportObj";

    public static final int URL_ERROR = 9001, NO_CONNECTION = 9002, LOAD_SUCCESFULLY = 1001, AN_ERROR_HAS_OCCRED = 9999;
    private static final int JSON_OPTIMIZATION_ERROR = 9003;

    public static final int GET_BY_TAGS = 701, GET_BY_POPULAR = 702, GET_BY_GENRES = 703;
    public static final int GET_BY_POPULAR_RANDOM_OFFSET = 704;
    public static final int GET_BY_POPULAR_CHART = 705;
    public static final int GET_BY_ELECTRONIC_POPULAR_CHART = 706;
    public static final int FALLBACK_API = 4001;

    protected String extraReport;

    protected static final File ROOT = android.os.Environment.getExternalStorageDirectory();
    protected static final File DIRECTORY_PATH = new File(ROOT.getAbsolutePath() + "/urandom-data");
    protected static final String FILE_NAME = "fav_uran.URobj";

    private double apiVersion = 2;

    private JSONArray tracks;

    //Get json data following by choice.
    public void getTrack(int choice, String genre, String kind) {
        switch (choice) {
            /*case GET_BY_POPULAR_RANDOM_OFFSET:
                apiVersion = 2;
                new fetchingTrackInBackground().execute(Config.API_V2_URL + ParamTrack.MODE_EXPLORE + ParamTrack.POPULAR + "?" + Config.CLIENT_ID + ParamTrack.PARAM_OFFSET + getRandomOffset() + ParamTrack.PARAM_LIMIT + 200);
                break;
            case GET_BY_POPULAR:
                apiVersion = 2;
                new fetchingTrackInBackground().execute(Config.API_V2_URL + ParamTrack.MODE_EXPLORE + ParamTrack.POPULAR + "?" + Config.CLIENT_ID);
                break;
            case GET_BY_GENRES:
                break;
            case GET_BY_TAGS:
                break;
            case FALLBACK_API:
                apiVersion = 1;
                new fetchingTrackInBackground().execute(Config.API_URL + ParamTrack.MODE_FILTER_TRACK + "?" + Config.CLIENT_ID + "&genre=electronics&limit=800");
                break;*/
            case GET_BY_POPULAR_CHART:
                apiVersion = 2.1;
                new fetchingTrackInBackground().execute(Config.API_V2_URL + ParamTrack.MODE_CHART + kind + ParamTrack.GENRE + genre + "&" + Config.CLIENT_ID + ParamTrack.PARAM_OFFSET + getRandomChartOffset() + ParamTrack.PARAM_LIMIT + 50);
                break;
            case GET_BY_ELECTRONIC_POPULAR_CHART:
                apiVersion = 2.1;
                new fetchingTrackInBackground().execute(Config.API_V2_URL + ParamTrack.MODE_CHART + kind + ParamTrack.GENRE + ParamTrack.GenreList.ELECTRONIC + "&" + Config.CLIENT_ID + ParamTrack.PARAM_OFFSET + getRandomChartOffset() + ParamTrack.PARAM_LIMIT + 50);
                break;
        }
    }

    public void getFavoriteTrack() {
        new ImportFavoriteTrack().execute();
        if (optimizeHashMapToFavoriteArrayList() == LOAD_SUCCESFULLY) {
            FragmentFavorite.setVisibilityOfComponent(FragmentFavorite.LOAD_SUCCESS, null);
        }
        //new importFavoriteTrack().execute();
    }

    public void saveFavoriteTrack() {
        Log.d(TAG_FILE_EXPORT, "Saving");
        new WriteFavorite().execute();
    }

    private class fetchingTrackInBackground extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... urlStr) {
            Log.d(TAG_INTERNET, "LINK:" + urlStr[0]);
            try {
                URL url = new URL(urlStr[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(6 * 1000);
                Log.d(TAG_INTERNET, "Connection code is " + urlConnection.getResponseCode());
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200) {
                    Log.d(TAG_INTERNET, "<YAY> Data now streaming : " + urlConnection.getContentLength());
                    InputStream soundCloudInputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(soundCloudInputStream, "UTF-8"), 8);
                    String line;
                    StringBuffer soundCloudJsonStrBuff = new StringBuffer();
                    while ((line = bufferedReader.readLine()) != null) {
                        soundCloudJsonStrBuff.append(line + "\n");
                    }
                    Log.d(TAG_INTERNET, "API VERSION = " + apiVersion);
                    bufferedReader.close();

                    if (apiVersion == 2 || apiVersion == 2.1) {
                        return optimizeJsonToList(soundCloudJsonStrBuff.toString());
                    } else if (apiVersion == 1) {
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
            Log.e(TAG_INTERNET, "RETURN CODE IS : " + code);
            if (code == LOAD_SUCCESFULLY)
                FragmentRandom.setVisibilityOfComponent(FragmentRandom.LOAD_SUCCESS);
            if (code == NO_CONNECTION || code == URL_ERROR || code == AN_ERROR_HAS_OCCRED)
                FragmentRandom.setVisibilityOfComponent(FragmentRandom.ERROR_LOAD);
        }
    }

    private class ImportFavoriteTrack extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            openFile(new File(DIRECTORY_PATH + "/" + FILE_NAME));
            return optimizeHashMapToFavoriteArrayList();
        }

        protected void onPostExecute(Integer code) {
            if (code == LOAD_SUCCESFULLY) {
                FragmentFavorite.setVisibilityOfComponent(FragmentFavorite.LOAD_SUCCESS, null);
            } else if (code == AN_ERROR_HAS_OCCRED) {
                FragmentFavorite.setVisibilityOfComponent(FragmentFavorite.ERROR_LOAD, extraReport);
            }
        }

        protected void openFile(File file) {
            try {
                FileReader readedFile = new FileReader(file);
                BufferedReader buffReader = new BufferedReader(readedFile);
                String line;
                JSONObject jsonObjectGetter;
                SCTrack scTrackPointer;
                Log.d(TAG_FILE_IMPORT , "Importing");
                while ((line = buffReader.readLine()) != null) {
                    scTrackPointer = new SCTrack();
                    try {
                        jsonObjectGetter = new JSONObject(line);
                        scTrackPointer.setMainObject(jsonObjectGetter);
                        scTrackPointer.setSongTitle(jsonObjectGetter.getString("title"));
                        scTrackPointer.setArtWorkURL(jsonObjectGetter.getString("artwork_url"));
                        scTrackPointer.setGenre(jsonObjectGetter.getString("genre"));
                        scTrackPointer.setDuration(jsonObjectGetter.getString("duration"));
                        scTrackPointer.setTrackURL(jsonObjectGetter.getString("uri"));
                        scTrackPointer.setUser(jsonObjectGetter.getJSONObject("user"));
                        scTrackPointer.setTrackID(jsonObjectGetter.getString("id"));
                        if(!ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.containsKey(scTrackPointer.getTrackID())){
                            Log.d(TAG_FILE_IMPORT , "Importing track to HASH MAP");
                            ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.put(scTrackPointer.getTrackID() , scTrackPointer);
                        }
                        scTrackPointer = new SCTrack();
                        Log.d(TAG_FILE_IMPORT , "Imported " + line);
                    } catch (JSONException e) {
                        Log.e(TAG_FILE_IMPORT , "<!> FAILED to import track " + line);
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println(e.toString());
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }

    }

    private class WriteFavorite extends AsyncTask<String, Integer, Integer> implements Serializable {

        private static final long serialVersionUID = 6902068848094397669L;

        @Override
        protected Integer doInBackground(String... params) {
            try {
                Log.d(TAG_FILE_EXPORT, "Directoty path is exist was " + DIRECTORY_PATH.exists());
                if (!DIRECTORY_PATH.exists()) {
                    Log.d(TAG_FILE_EXPORT, "File not exist creating directory and file");
                    DIRECTORY_PATH.mkdirs();
                }
                /*FileOutputStream fileOut = new FileOutputStream(DIRECTORY_PATH.getPath() + "/fav_urandom.uobj");
                ObjectOutputStream oos = new ObjectOutputStream(fileOut);
                SavedObject savedObject = new SavedObject(ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP);
                oos.writeObject(savedObject);*/
                printStreamToFile(new FileOutputStream(DIRECTORY_PATH.getPath() + "/" + FILE_NAME));
            } catch (FileNotFoundException ex) {
                extraReport = ex.toString();
                return TrackObject.AN_ERROR_HAS_OCCRED;
            }
            return TrackObject.LOAD_SUCCESFULLY;
        }

        protected void onPostExecute(Integer code) {
            if (code == TrackObject.LOAD_SUCCESFULLY) {
                Log.d(TrackObject.TAG_FILE_EXPORT, "saved successfully");
            } else if (code == TrackObject.AN_ERROR_HAS_OCCRED) {
                Log.e(TrackObject.TAG_FILE_EXPORT, "favorite save failed" + extraReport);
            }
        }

        private void printStreamToFile(FileOutputStream fileOutputStream) {
            PrintStream oos = new PrintStream(fileOutputStream);
            String title, duration, trackURL, artworkURL, streamURL, genre;
            JSONObject user;
            for (String id : ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.keySet()) {
                oos.println(ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.get(id).getJSONMainObject().toString());
                Log.d(TAG_FILE_EXPORT , ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.get(id).getJSONMainObject().toString());
            }
            oos.close();
        }
    }

    private int optimizeJsonToList(String jsonString) {
        try {
            JSONObject soundCloudJsonObject = new JSONObject(jsonString);
            if (apiVersion == 2) tracks = soundCloudJsonObject.getJSONArray("tracks");
            else if (apiVersion == 2.1) tracks = soundCloudJsonObject.getJSONArray("collection");
            Log.e(TAG_JSON, "DATA SIZE : " + tracks.length());
            JSONObject jsonObjectGetter = new JSONObject();
            SCTrack scTrackPointer = new SCTrack();

            for (int i = 0; i < tracks.length(); i++) {
                jsonObjectGetter = tracks.getJSONObject(i);
                if (apiVersion == 2.1) jsonObjectGetter = jsonObjectGetter.getJSONObject("track");
                scTrackPointer.setMainObject(jsonObjectGetter);
                scTrackPointer.setSongTitle(jsonObjectGetter.getString("title"));
                scTrackPointer.setArtWorkURL(jsonObjectGetter.getString("artwork_url"));
                scTrackPointer.setGenre(jsonObjectGetter.getString("genre"));
                scTrackPointer.setDuration(jsonObjectGetter.getString("duration"));
                scTrackPointer.setTrackURL(jsonObjectGetter.getString("uri"));
                scTrackPointer.setUser(jsonObjectGetter.getJSONObject("user"));
                scTrackPointer.setTrackID(jsonObjectGetter.getString("id"));
                ProgramStaticConstant.TRACK.add(scTrackPointer);
                scTrackPointer = new SCTrack();
            }
        } catch (JSONException e) {
            Log.e(TAG_JSON, "<!JSON> Json optimize error");
            Log.e(TAG_JSON, e.toString());
            return JSON_OPTIMIZATION_ERROR;
        }
        return LOAD_SUCCESFULLY;
    }

    private int optimizeJsonToListFallback(String jsonString) {
        try {
            tracks = new JSONArray(jsonString);
            JSONObject jsonObjectGetter = new JSONObject();
            SCTrack scTrackPointer = new SCTrack();
            for (int i = 0; i < tracks.length(); i++) {
                jsonObjectGetter = tracks.getJSONObject(i);
                scTrackPointer.setMainObject(jsonObjectGetter);
                scTrackPointer.setSongTitle(jsonObjectGetter.getString("title"));
                scTrackPointer.setArtWorkURL(jsonObjectGetter.getString("artwork_url"));
                scTrackPointer.setGenre(jsonObjectGetter.getString("genre"));
                scTrackPointer.setDuration(jsonObjectGetter.getString("duration"));
                scTrackPointer.setStreamURL(jsonObjectGetter.getString("uri") + "stream");
                scTrackPointer.setTrackURL(jsonObjectGetter.getString("uri"));
                scTrackPointer.setUser(jsonObjectGetter.getJSONObject("user"));
                ProgramStaticConstant.TRACK.add(scTrackPointer);
                scTrackPointer = new SCTrack();
            }
        } catch (JSONException e) {
            Log.e(TAG_JSON, "<!JSON> Json fallback optimize error");
            Log.e(TAG_JSON, e.toString());
            return JSON_OPTIMIZATION_ERROR;
        }
        Log.d(TAG_JSON, "Covert successfully");
        return LOAD_SUCCESFULLY;
    }

    private int optimizeHashMapToFavoriteArrayList() {
        ProgramStaticConstant.FAVORITE_TRACK.clear();
        for (String id : ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.keySet()) {
            ProgramStaticConstant.FAVORITE_TRACK.add(ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.get(id));
        }
        return LOAD_SUCCESFULLY;
    }

    public long getRandomOffset() {
        return (long) (Math.random() * 1000);
    }

    public long getRandomChartOffset() {
        long randNum = (long) (Math.random() * 100);
        Log.e(TAG_INTERNET, "RAND_NUM == " + randNum);
        return randNum;
    }
}
