package com.urandom.utech.cardviewsoundcloudversion;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Fragment that display user's favorite track into a card list through recyclerView
 * Created by nopphonyel on 6/13/16.
 */
public class FragmentFavorite extends Fragment {

    public static final int ON_LOAD = 1, ERROR_LOAD = 2, LOAD_SUCCESS = 3;

    public static boolean FAVORITE_ACTIVITY_WAS_CREATED = false;
    protected static TrackObject trackManagement;
    private static RecyclerView trackList;
    private RecyclerView.LayoutManager trackListLayoutPotrait, trackListLayoutLandscape;
    public static TrackListAdapter favoriteTrackListAdapter;
    private static ProgressBar spinner;
    private static TextView loadingText;
    public static boolean WAS_CREATED = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savBundle) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_favorite, container, false);
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar);
        loadingText = (TextView) rootView.findViewById(R.id.loading_text);

        favoriteTrackListAdapter = new TrackListAdapter(getActivity(), ProgramStaticConstant.FAVORITE_TRACK , TrackListAdapter.MODE_FAVORITE);
        FAVORITE_ACTIVITY_WAS_CREATED = true;

        //Create Tracklist object
        trackList = (RecyclerView) rootView.findViewById(R.id.list_track);
        trackListLayoutPotrait = new LinearLayoutManager(getActivity());
        trackListLayoutLandscape = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        //Create trackManagement odject for fetching track or import track list to recyclerView
        trackManagement = new TrackObject();

        getFavoriteTrack();

        //Check Orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            trackList.setLayoutManager(trackListLayoutPotrait);
        else trackList.setLayoutManager(trackListLayoutLandscape);

        trackList.setAdapter(favoriteTrackListAdapter);
        WAS_CREATED = true;
        return rootView;
    }

    /**
     * Import favorite track into ProgramStaticConstant.FAVORITE_TRACK
     */
    public void getFavoriteTrack() {
        ProgramStaticConstant.FAVORITE_TRACK.clear();
        trackManagement.getFavoriteTrack();
        //shuffleTrack();
    }

    public static void setVisibilityOfComponent(int id, String extraReport) {
        if (FAVORITE_ACTIVITY_WAS_CREATED) {
            if (id == LOAD_SUCCESS) {
                spinner.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                trackList.setVisibility(View.VISIBLE);
            } else if (id == ERROR_LOAD) {
                loadingText.setText("Error : " + extraReport);
            } else if (id == ON_LOAD) {
                spinner.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                loadingText.setText("Importing favorite track");
                trackList.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            trackList.setLayoutManager(trackListLayoutPotrait);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            trackList.setLayoutManager(trackListLayoutLandscape);
        }
    }
}
