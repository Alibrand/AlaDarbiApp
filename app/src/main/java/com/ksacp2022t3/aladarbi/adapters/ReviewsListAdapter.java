package com.ksacp2022t3.aladarbi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.aladarbi.R;
import com.ksacp2022t3.aladarbi.RideDetailsActivity;
import com.ksacp2022t3.aladarbi.models.Review;
import com.ksacp2022t3.aladarbi.models.Ride;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewsListAdapter extends RecyclerView.Adapter<ReviewItem> {
    List<Review> reviewList;
    Context context;

    public ReviewsListAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review,parent,false);

        return new ReviewItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewItem holder, int position) {
        Review review=reviewList.get(position);
        holder.txt_name.setText(review.getName());
        holder.txt_comment.setText(review.getComment());
        holder.rating.setRating((float)review.getRate());

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}

class ReviewItem extends RecyclerView.ViewHolder
{
    TextView txt_name,txt_comment;
    RatingBar rating;

    public ReviewItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_comment=itemView.findViewById(R.id.txt_comment);
        rating=itemView.findViewById(R.id.rating);

    }
}
