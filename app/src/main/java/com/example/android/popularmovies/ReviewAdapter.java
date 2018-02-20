package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.Data.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomViewHolder> {

    private Review[] reviews;

    public ReviewAdapter(Review[] reviewList) {
        reviews = reviewList;
    }

    public void setReviews(Review[] newReviewList) {
        reviews = newReviewList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int itemLayoutId = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(itemLayoutId, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviews.length;
    }

    public Review getItemAt(int index) {
        return reviews[index];
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView authorTV;
        TextView reviewTV;

        private CustomViewHolder(View reviewView) {
            super(reviewView);
            authorTV = reviewView.findViewById(R.id.author);
            reviewTV = reviewView.findViewById(R.id.review_text);
        }

        void bind(int index) {
            authorTV.setText(reviews[index].getAuthor());
            reviewTV.setText(reviews[index].getContent());
        }
    }
}
