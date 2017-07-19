package id.co.webpresso.yohanes.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import id.co.webpresso.yohanes.popularmovies.R;
import us.feras.mdv.MarkdownView;

/**
 * Adapter for movie trailer
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ReviewViewHolder> {
    public boolean initialized = false;

    private Context context;
    private JSONObject[] reviews;

    public MovieReviewsAdapter(Context context) {
        this.context = context;
    }

    public void setReviews(JSONObject[] reviews) {
        initialized = true;

        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public MovieReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View movieReviewItemView = layoutInflater.inflate(R.layout.movie_review_item, parent, false);

        return new ReviewViewHolder(movieReviewItemView);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.ReviewViewHolder holder, int position) {
        JSONObject review = reviews[position];

        try {
            holder.reviewAuthorTextView.setText(context.getString(R.string.movie_detail_review_author) + ": " + review.getString("author"));
            holder.reviewBodyMarkdownView.loadMarkdown(review.getString("content"), "file:///android_asset/markdown.css");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.reviews == null ? 0 : this.reviews.length;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        MarkdownView reviewBodyMarkdownView;
        TextView reviewAuthorTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            reviewBodyMarkdownView = itemView.findViewById(R.id.tv_review_body);
            reviewAuthorTextView = itemView.findViewById(R.id.tv_review_author);
        }
    }
}
