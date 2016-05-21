package com.urandom.utech.cardviewsoundcloudversion;

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
    private static int TRACK_PLAYING_NO = 0;
    private static boolean IS_PLAYING = false;

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
}
