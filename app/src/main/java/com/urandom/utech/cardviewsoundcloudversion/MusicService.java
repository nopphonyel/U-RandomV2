package com.urandom.utech.cardviewsoundcloudversion;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by OS8 on 21-May-16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener,MediaPlayer.OnCompletionListener{
    private MediaPlayer player;
    private ArrayList<SCTrack> que;
    private int songPosition = 0;
    private final IBinder musicBind = new MusicBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public boolean onUnBind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playSong(){
        player.reset();

        SCTrack playSong = que.get(songPosition);

        String trackID = playSong.getTrackID();

        String url = playSong.getStreamURL()+"?"+Config.CLIENT_ID;
        try{
            player.setDataSource(url);
            player.prepareAsync();
        }catch(IllegalArgumentException e){
            Log.e("MusicService","Error for somthing" + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }catch (SecurityException e){
            Log.e("MusicService","Error for somthing" + e.toString());
        }catch (IllegalStateException e){
            Log.e("MusicService","Error for somthing" + e.toString());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
        songPosition = 0;
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


}
