package com.urandom.utech.cardviewsoundcloudversion;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

/**
 * Created by nopphonyel on 6/19/16.
 */
public class FragmentMusicDetail extends Fragment implements View.OnClickListener{

    protected static TextView trackTitle;
    protected static TextView trackOwner;
    protected static ImageView cover;
    protected static ImageView loveBtn;
    protected static Context context;

    public static boolean FRAGMENT_WAS_CREATED = false;

    protected TrackObject trackManagement;

    private static final String TAG_FRAGMENT_DETAIL = "Fragment Detail";

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savBundle){
        Log.e(TAG_FRAGMENT_DETAIL, "FRAGMENT HAS CREATED");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_track_detail , container , false);
        loveBtn = (ImageView) rootView.findViewById(R.id.loveButton);
        trackTitle = (TextView) rootView.findViewById(R.id.playing_track_title);
        trackOwner = (TextView) rootView.findViewById(R.id.playing_track_owner);
        cover = (ImageView) rootView.findViewById(R.id.playing_track_cover);
        context = getActivity();

        trackManagement = new TrackObject();

        loveBtn.setOnClickListener(this);
        FRAGMENT_WAS_CREATED = true;
        updateComponent();
        return rootView;
    }

    public static void updateComponent() {
        Log.e(TAG_FRAGMENT_DETAIL, "UPDATING COMPONENT");
        //Set all component to match data
        trackTitle.setText(MusicService.playingTrack.getSongTitle());
        try {
            trackOwner.setText(MusicService.playingTrack.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Picasso.with(context).load(MusicService.playingTrack.getLargeArtWorkURL()).placeholder(R.drawable.default_cover).into(cover);

        //Check track is in a favorite collection
        if (ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.containsKey(MusicService.playingTrack.getTrackID())) {
            loveBtn.setImageResource(R.mipmap.ic_action_love);
        } else {
            loveBtn.setImageResource(R.mipmap.ic_action_unlove);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == loveBtn) {
            //Click to add this track to favorite
            if (ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.containsKey(MusicService.playingTrack.getTrackID())) {
                loveBtn.setImageResource(R.mipmap.ic_action_unlove);
                ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.remove(MusicService.playingTrack.getTrackID());
            }
            //Click to remove this track from favorite
            else {
                loveBtn.setImageResource(R.mipmap.ic_action_love);
                ProgramStaticConstant.FAVORITE_TRACK_HASH_MAP.put(MusicService.playingTrack.getTrackID(), MusicService.playingTrack);
            }
            trackManagement.saveFavoriteTrack();
            if(FragmentFavorite.FAVORITE_ACTIVITY_WAS_CREATED)
                FragmentFavorite.trackManagement.getFavoriteTrack();
            FragmentFavorite.favoriteTrackListAdapter.notifyDataSetChanged();
        }
    }
}
