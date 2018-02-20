package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.Data.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.CustomViewHolder> {

    private Trailer[] trailers;
    private Context context;

    public TrailerAdapter (Trailer[] trailerList) {
        trailers = trailerList;
    }

    public void setTrailers(Trailer[] newTrailerList) {
        trailers = newTrailerList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int itemLayoutId = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(itemLayoutId, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailers.length;
    }

    public Trailer getItemAt(int index) {
        return trailers[index];
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView trailerTv;
        ImageView playIV;

        public CustomViewHolder(View trailerView) {
            super(trailerView);
            trailerTv = trailerView.findViewById(R.id.video_title);
            playIV = trailerView.findViewById(R.id.iv_play);
            trailerView.setOnClickListener(this);
        }

        void bind(int index) {
            trailerTv.setText(trailers[index].getName());
            if (trailers[index].isPlaceholder()) {
                playIV.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Trailer trailer = trailers[getAdapterPosition()];
            if (trailer.isPlaceholder()) {
                return;
            }

            // open trailer on browser
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey()));
            context.startActivity(i);
        }
    }
}
