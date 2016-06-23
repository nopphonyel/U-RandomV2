package com.urandom.utech.cardviewsoundcloudversion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nopphonyel on 6/19/16.
 */
public class FragmentQueue extends Fragment{

    public static RecyclerView trackQueue;
    public static TrackListAdapter trackQueueListAdapter;
    public static LinearLayoutManager linearLayoutManager;

    public static boolean WAS_CREATED = false;

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savBundle){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_track_queue , container , false);
        trackQueue = (RecyclerView) rootView.findViewById(R.id.queue_list);
        trackQueueListAdapter = new TrackListAdapter(getActivity() ,MusicService.que, TrackListAdapter.MODE_TRACK_QUEUE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        trackQueue.setAdapter(trackQueueListAdapter);
        trackQueue.setLayoutManager(linearLayoutManager);
        linearLayoutManager.scrollToPosition(ProgramStaticConstant.getTrackPlayingNo());
        WAS_CREATED = true;
        return rootView;
    }

    public static void updateComponent(){
        trackQueueListAdapter.notifyDataSetChanged();
    }
}
