package com.urandom.utech.cardviewsoundcloudversion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

/**
 * This is an adapter
 * Created by nopphon on 4/18/16.
 */
public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackHolder> {

    private List<SCTrack> track;
    private Context context;


    TrackListAdapter(Context mcontext, List<SCTrack> newTrack) {
        context = mcontext;
        track = newTrack;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public TrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.inside_card, parent, false);
        TrackHolder trackView = new TrackHolder(v);
        return trackView;
    }

    @Override
    public void onBindViewHolder(TrackHolder holder, int position) {
        SCTrack track = (SCTrack) this.track.get(position);
        holder.trackTitle.setText(track.getSongTitle());
        if(MusicService.isPlaying() && MusicService.getPlayingTrackID().equalsIgnoreCase(track.getTrackID())){
            holder.trackTitle.setTextColor(Color.parseColor("#f10050"));
            ProgramStaticConstant.setTrackPlayingNo(position);
        }
        else{
            holder.trackTitle.setTextColor(Color.DKGRAY);
        }
        try {
            holder.trackOwner.setText(track.getUserName());
        } catch (JSONException e) {
            Log.e("ADAPTER" , "SET USER ERROR : "+e.toString());
        }

        if (track.getTrackGenre() == null || track.getTrackGenre().equalsIgnoreCase("")) {
            holder.genreIcon.setVisibility(View.GONE);
            holder.genre.setVisibility(View.GONE);
        } else {
            holder.genreIcon.setVisibility(View.VISIBLE);
            holder.genre.setVisibility(View.VISIBLE);
            holder.genre.setText(track.getTrackGenre());
        }

        holder.setPosition(position);

        holder.trackDuration.setText(track.getTrackDuraion());

        Picasso.with(context).load(R.drawable.ic_soundcloud_track).into(holder.sourceIcon);
        Picasso.with(context).load(track.getArtWorkURL()).placeholder(R.drawable.default_cover).into(holder.trackCover);
    }


    @Override
    public int getItemCount() {
        return track.size();
    }

    public class TrackHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView card;
        TextView trackTitle;
        TextView trackOwner;
        TextView trackDuration;
        ImageView trackCover;
        ImageView sourceIcon;

        ImageView genreIcon;
        TextView genre;
        int trackPosition;

        public TrackHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            card = (CardView) itemView.findViewById(R.id.card);
            trackTitle = (TextView) itemView.findViewById(R.id.track_title);
            trackDuration = (TextView) itemView.findViewById(R.id.track_duration);

            trackCover = (ImageView) itemView.findViewById(R.id.track_art_work);

            genreIcon = (ImageView) itemView.findViewById(R.id.genre_icon);
            genre = (TextView) itemView.findViewById(R.id.genre);

            trackOwner = (TextView) itemView.findViewById(R.id.track_owner);
            sourceIcon = (ImageView) itemView.findViewById(R.id.img_source);
        }

        public void setPosition(int pos)
        {
            trackPosition = pos;
        }

        @Override
        public void onClick(View v) {
            notifyDataSetChanged();
            context.startActivity(new Intent(context , NowPlaying.class));
            if(trackPosition != ProgramStaticConstant.getTrackPlayingNo() || !MusicService.isServiceExist()) {
                ProgramStaticConstant.setTrackPlayingNo(trackPosition);
                ProgramStaticConstant.musicService.setSong(trackPosition);
                ProgramStaticConstant.musicService.playSong();
                //context.startService(new Intent(context , MusicService.class).setAction(ProgramStaticConstant.ForegroundServiceAction.ACTION_PLAY));
            }
        }
    }
}


