package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Service file
 * Created by tewlyhackyizz on 21-May-16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{
    private static final String TAG_SERVICE = "MusicService.class";
    private MediaPlayer player;
    private ArrayList<SCTrack> que;
    private int songPosition = 0;
    private final IBinder musicBind = new MusicBinder();
    private Notification notification;

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onCreate(){
        Log.e(TAG_SERVICE , "Created SERVICE");
        super.onCreate();
        player = new MediaPlayer();
        songPosition = 0;
        initMusicPlayer();
    }

    public boolean onUnBind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playSong(){
        if(ProgramStaticConstant.getTrackPlayingNo() < ProgramStaticConstant.TRACK.size()) {
            player.reset();
            ProgramStaticConstant.setIsPlaying(true);
            MainActivity.updateFloatingActionButton();
            Log.e(TAG_SERVICE, "preparing track");
            SCTrack playSong = que.get(songPosition);
            String url = playSong.getTrackURL() +"/stream" + "?" + Config.CLIENT_ID;
            Log.d(TAG_SERVICE , url);
            try {
                player.setDataSource(url);
                player.prepareAsync();
                notification = new NotificationCompat.Builder(this).setContentTitle(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getSongTitle())
                        .setTicker("U-Random")
                        .setContentText(ProgramStaticConstant.TRACK.get(ProgramStaticConstant.getTrackPlayingNo()).getUserName())
                        .setSmallIcon(R.drawable.ic_toolbar)
                        .setOngoing(true).build();
            } catch (IllegalArgumentException e) {
                Log.e("MusicService", "Error for somthing" + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                Log.e("MusicService", "Error for somthing" + e.toString());
            } catch (IllegalStateException e) {
                Log.e("MusicService", "Error for somthing" + e.toString());
            }   catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            stopMusic();
        }
    }

    public void fastForword(){
        ProgramStaticConstant.setTrackPlayingNo(songPosition+1);
        setSong(ProgramStaticConstant.getTrackPlayingNo());
        NowPlaying.updateComponent();
        MainActivity.trackListAdapter.notifyDataSetChanged();
        playSong();
    }

    public void backForword(){
        ProgramStaticConstant.setTrackPlayingNo(songPosition-1);
        setSong(ProgramStaticConstant.getTrackPlayingNo());
        NowPlaying.updateComponent();
        MainActivity.trackListAdapter.notifyDataSetChanged();
        playSong();
    }

    @Override
    public void onCompletion(MediaPlayer mp){
        fastForword();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        fastForword();
        Log.e(TAG_SERVICE , "Error playing Track skip to position " + songPosition);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG_SERVICE , "Playing track");
        mp.start();
    }

    public void stopMusic(){
        ProgramStaticConstant.setTrackPlayingNo(-1);
        ProgramStaticConstant.setIsPlaying(false);
        MainActivity.updateFloatingActionButton();
        MainActivity.trackListAdapter.notifyDataSetChanged();
        player.stop();
    }

    public void pauseMusic(){
        player.pause();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<SCTrack> track){
        que=track;
    }

    public class MusicBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    public void setSong(int songIndex){
        songPosition = songIndex;
    }

    public int getSongPosition(){
        return songPosition;
    }


}
