package com.urandom.utech.cardviewsoundcloudversion;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
        holder.trackOwner.setText(track.getUserName());

        if (track.getTrackGenre() == null || track.getTrackGenre().equalsIgnoreCase("")) {
            holder.genreIcon.setVisibility(View.GONE);
            holder.genre.setVisibility(View.GONE);
        } else {
            holder.genreIcon.setVisibility(View.VISIBLE);
            holder.genre.setVisibility(View.VISIBLE);
            holder.genre.setText(track.getTrackGenre());
        }

        holder.trackDuration.setText(track.getTrackDuraion());

        Picasso.with(context).load(R.drawable.ic_soundcloud_track).into(holder.sourceIcon);
        Picasso.with(context).load(track.getArtWorkURL()).placeholder(R.drawable.default_cover).into(holder.trackCover);
    }


    @Override
    public int getItemCount() {
        return track.size();
    }

    public class TrackHolder extends RecyclerView.ViewHolder{
        CardView card;
        TextView trackTitle;
        TextView trackOwner;
        TextView trackDuration;
        ImageView trackCover;
        ImageView sourceIcon;

        ImageView genreIcon;
        TextView genre;


        public TrackHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            card = (CardView) itemView.findViewById(R.id.card);
            trackTitle = (TextView) itemView.findViewById(R.id.track_title);
            trackDuration = (TextView) itemView.findViewById(R.id.track_duration);

            trackCover = (ImageView) itemView.findViewById(R.id.track_art_work);

            genreIcon = (ImageView) itemView.findViewById(R.id.genre_icon);
            genre = (TextView) itemView.findViewById(R.id.genre);

            trackOwner = (TextView) itemView.findViewById(R.id.track_owner);
            sourceIcon = (ImageView) itemView.findViewById(R.id.img_source);
        }

    }
}


