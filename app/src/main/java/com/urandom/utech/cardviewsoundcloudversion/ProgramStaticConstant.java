package com.urandom.utech.cardviewsoundcloudversion;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class contain list of track and track plying state.
 * Created by nopphon on 5/4/16.
 */
public class ProgramStaticConstant {
    public static ArrayList<SCTrack> TRACK = new ArrayList<SCTrack>();
    public static HashMap<String , SCTrack> FAVORITE_TRACK = new HashMap<String, SCTrack>();
    private static int TRACK_PLAYING_NO = -1;
    private static boolean IS_PLAYING = false;

    public static final String TAG_PLAYING = new String("NowPlaying.class");
    public static final String TAG_BIND_SERVICE = new String("ServiceConnection");

    private static boolean musicBound=false;

    public static int getTrackPlayingNo() {
        return TRACK_PLAYING_NO;
    }

    public static boolean isPlaying() {
        return IS_PLAYING;
    }

    public static void setIsPlaying(boolean isPlaying) {
        IS_PLAYING = isPlaying;
    }

    public static void setTrackPlayingNo(int trackPlayingNo) {
        TRACK_PLAYING_NO = trackPlayingNo;
    }

    public static MusicService musicService;
    //Create new ServiceConnection Interface variable
    public static ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(ProgramStaticConstant.TRACK);
            Log.d(TAG_BIND_SERVICE,"Service now connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            Log.d(TAG_BIND_SERVICE,"Service now disconnected");
        }
    };
}
