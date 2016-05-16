package com.urandom.utech.cardviewsoundcloudversion;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contain list of track and track plying state.
 * Created by nopphon on 5/4/16.
 */
public class SCTrackList {
    public static List<SCTrack> TRACK = new ArrayList<SCTrack>();
    private static long TRACK_PLAYING_NO = 0;
    private static boolean IS_PLAYING = false;

    public static void setIsPlaying(boolean isPlaying) {
        IS_PLAYING = isPlaying;
    }

    public static void setTrackPlayingNo(long trackPlayingNo) {
        TRACK_PLAYING_NO = trackPlayingNo;
    }
}
