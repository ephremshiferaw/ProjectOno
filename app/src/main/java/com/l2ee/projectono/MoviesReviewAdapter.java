package com.l2ee.projectono;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libraries.core.Review;

import java.util.ArrayList;

public class MoviesReviewAdapter extends BaseAdapter {

    ArrayList<Review> reviews;
    Context mContext;
    LayoutInflater inflater;

    public MoviesReviewAdapter(Context c) {
        mContext = c;
        reviews = new ArrayList<Review>();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public void setReviews(ArrayList<Review> reviews) {
        if (reviews != null)
            this.reviews = reviews;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {

        return reviews.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.movie_review_item, null);
        }
        Review review = reviews.get(position);
        // set image based on selected text
        TextView content = (TextView) convertView
                .findViewById(R.id.content);
        TextView author = (TextView) convertView
                .findViewById(R.id.author);
        author.setText(review.getAuthor());
        content.setText(review.getContent());
        return convertView;
    }
}
