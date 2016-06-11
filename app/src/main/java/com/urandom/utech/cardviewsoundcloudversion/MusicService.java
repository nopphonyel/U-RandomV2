package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Service file
 * Created by tewlyhackyizz on 21-May-16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{
    private static String playingTrackID = new String();
    private static final String TAG_SERVICE = "MusicService.class";
    public static MediaPlayer player;
    public static ArrayList<SCTrack> que;
    private int songPosition = 0;
    private final IBinder musicBind = new MusicBinder();
    private static boolean IS_PLAYING = false;
    public static SCTrack playingTrack;

    protected Notification notification;

    public static boolean imNotReadyForPlaying = true;

    public static int lastPosition = 0;

    private static boolean IS_SERVICE_EXIST = false;

    public static boolean isPlaying() {
        return IS_PLAYING;
    }

    public static void setIsPlaying(boolean isPlaying) {
        IS_PLAYING = isPlaying;
    }

    public static boolean isServiceExist() {
        return IS_SERVICE_EXIST;
    }

    /**
     * When service created IS_SERVICE_EXIST will be true
     * @param isServiceExist
     */
    public static void setIsServiceExist(boolean isServiceExist) {
        IS_SERVICE_EXIST = isServiceExist;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCreate() {
        Log.e(TAG_SERVICE, "Created SERVICE");
        setIsServiceExist(true);
        super.onCreate();
        player = new MediaPlayer();
        songPosition = 0;
        initMusicPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ProgramStaticConstant.ForegroundServiceAction.ACTION_PLAY_PREVIOUS)) {
            Log.i(TAG_SERVICE, "Clicked Previous");
            backForword();
        } else if (intent.getAction().equals(ProgramStaticConstant.ForegroundServiceAction.ACTION_PAUSE)) {
            Log.i(TAG_SERVICE, "Clicked Pause");
            if (isPlaying()) {
                pauseMusic();
            } else {
                unpauseMusic();
            }
        } else if (intent.getAction().equals(ProgramStaticConstant.ForegroundServiceAction.ACTION_PLAY_NEXT)) {
            Log.i(TAG_SERVICE, "Clicked Next");
            fastForword();
        } else if (intent.getAction().equals(ProgramStaticConstant.ForegroundServiceAction.STOP_SERVICE)){
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    public void showNotification() {
        Intent notificationIntent = new Intent(this, NowPlaying.class);
        notificationIntent.setAction(ProgramStaticConstant.ForegroundServiceAction.ACTION_NOW_PLAYING);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent previousIntent = new Intent(this, MusicService.class);
        previousIntent.setAction(ProgramStaticConstant.ForegroundServiceAction.ACTION_PLAY_PREVIOUS);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.setAction(ProgramStaticConstant.ForegroundServiceAction.ACTION_PAUSE);
        PendingIntent ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ProgramStaticConstant.ForegroundServiceAction.ACTION_PLAY_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Intent stopIntent = new Intent(this , MusicService.class);
        stopIntent.setAction(ProgramStaticConstant.ForegroundServiceAction.STOP_SERVICE);
        PendingIntent pstopIntent = PendingIntent.getService(this, 0 , stopIntent , 0);
        try {
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(playingTrack.getSongTitle())
                    .setContentText(playingTrack.getUserName())
                    .setSmallIcon(R.drawable.ic_toolbar)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play/Pause", ppauseIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(playingTrack.getSongTitle())
                    .setContentText("Error to get USER_NAME")
                    .setSmallIcon(R.drawable.ic_toolbar)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(android.R.drawable.ic_media_previous, "Previous", ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play/Pause", ppauseIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next", pnextIntent)
                    .build();
        }
        startForeground(ProgramStaticConstant.ForegroundServiceAction.FOREGROUND_SERVICE, notification);

    }

    public boolean onUnBind(Intent intent) {
        player.stop();
        player.release();
        setIsServiceExist(false);
        return false;
    }

    /**
     * Get playing track ID
     * @return
     */
    public static String getPlayingTrackID() {
        return playingTrackID;
    }


    public void playSong() {
        if (songPosition < que.size() && songPosition >= 0) {
            player.reset();
            imNotReadyForPlaying = true;
            setIsPlaying(true);
            Log.e(TAG_SERVICE, "preparing track");
            SCTrack playSong = que.get(songPosition);
            playingTrack = playSong;
            playingTrackID = playSong.getTrackID();

            MainActivity.updateFloatingActionButton();
            if (NowPlaying.ableToUpdateComponent) {
                NowPlaying.updateComponent();
            }

            showNotification();
            String url = playSong.getTrackURL() + "/stream" + "?" + Config.CLIENT_ID;
            Log.d(TAG_SERVICE, url);
            try {
                player.setDataSource(url);
                player.prepareAsync();
            } catch (IllegalArgumentException e) {
                Log.e("MusicService", "Error for somthing" + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                Log.e("MusicService", "Error for somthing" + e.toString());
            } catch (IllegalStateException e) {
                Log.e("MusicService", "Error for somthing" + e.toString());
            }
        } else {
            if (songPosition > que.size()) {
                stopMusic();
            } else if (songPosition < 0) {
                fastForword();
            }
        }
    }

    public void fastForword() {
        ProgramStaticConstant.setTrackPlayingNo(songPosition + 1);
        setSong(ProgramStaticConstant.getTrackPlayingNo()); //Set songPosition variable

        updateTrackAdapter();
        playSong();
    }

    public void backForword() {
        if (player.getCurrentPosition() <= 3000) {
            ProgramStaticConstant.setTrackPlayingNo(songPosition - 1);
            setSong(ProgramStaticConstant.getTrackPlayingNo()); //Set songPosition variable

            updateTrackAdapter();
            playSong();
        } else {
            gotoMusic(0);
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        fastForword();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG_SERVICE, "Error playing Track skip to position " + songPosition);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG_SERVICE, "Playing track");
        imNotReadyForPlaying = false;
        NowPlaying.updateComponent();
        mp.start();
    }

    public static void gotoMusic(int newPosition) {
        player.seekTo(newPosition);
    }

    public void stopMusic() {
        ProgramStaticConstant.setTrackPlayingNo(-1);
        setIsPlaying(false);
        MainActivity.updateFloatingActionButton();
        updateTrackAdapter();
        player.stop();
    }

    public void pauseMusic() {
        Log.e(TAG_SERVICE, "Pause");
        setIsPlaying(false);
        lastPosition = player.getCurrentPosition();
        NowPlaying.updateComponent();
        player.pause();
    }

    public void unpauseMusic() {
        Log.e(TAG_SERVICE, "Unpause");
        setIsPlaying(true);
        gotoMusic(lastPosition);
        NowPlaying.updateComponent();
        player.start();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void stopRunning(){
        IS_SERVICE_EXIST = false;
        stopMusic();
        stopSelf();
    }

    private void updateTrackAdapter(){
        if(MainActivity.MAIN_ACTIVITY_WAS_CREATED)
            MainActivity.trackListAdapter.notifyDataSetChanged();
        if(FavoriteActivity.FAVORITE_ACTIVITY_WAS_CREATED)
            FavoriteActivity.favoriteTrackListAdapter.notifyDataSetChanged();
    }

    public static void setList(ArrayList<SCTrack> track) {
        que = track;
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setSong(int songIndex) {
        songPosition = songIndex;
    }

    public int getSongPosition() {
        return songPosition;
    }
}
