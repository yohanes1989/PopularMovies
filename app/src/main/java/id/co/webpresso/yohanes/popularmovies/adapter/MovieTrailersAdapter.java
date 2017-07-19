package id.co.webpresso.yohanes.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import id.co.webpresso.yohanes.popularmovies.R;

/**
 * Adapter for movie trailer
 */

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.TrailerViewHolder> {
    public boolean initialized = false;

    private Context context;
    private JSONObject[] trailers;

    public MovieTrailersAdapter(Context context) {
        this.context = context;
    }

    public void setTrailers(JSONObject[] trailers) {
        initialized = true;

        int youtubeTrailersCount = 0;

        try {
            for (JSONObject trailer : trailers) {
                if (trailer.getString("site").equals("YouTube")) {
                    youtubeTrailersCount += 1;
                }
            }

            this.trailers = new JSONObject[youtubeTrailersCount];

            youtubeTrailersCount = 0;
            for (JSONObject trailer : trailers) {
                if (trailer.getString("site").equals("YouTube")) {
                    this.trailers[youtubeTrailersCount] = trailer;
                    youtubeTrailersCount += 1;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.trailers = trailers;
        notifyDataSetChanged();
    }

    @Override
    public MovieTrailersAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View movieTrailerItemView = layoutInflater.inflate(R.layout.movie_trailer_item, parent, false);

        return new TrailerViewHolder(movieTrailerItemView);
    }

    @Override
    public void onBindViewHolder(MovieTrailersAdapter.TrailerViewHolder holder, int position) {
        JSONObject trailer = trailers[position];

        String buttonTitle = null;

        try {
            buttonTitle = trailer.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.trailerButton.setText(buttonTitle);
    }

    @Override
    public int getItemCount() {
        return this.trailers == null ? 0 : this.trailers.length;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        Button trailerButton;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            trailerButton = itemView.findViewById(R.id.btn_trailer_play);
            trailerButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            JSONObject trailer = trailers[getAdapterPosition()];

            Uri youtubeUri = null;

            try {
                String youtubePath = "https://www.youtube.com/watch";
                youtubeUri = Uri.parse(youtubePath)
                        .buildUpon()
                        .appendQueryParameter("v", trailer.getString("key"))
                        .build();
            } catch(JSONException e) {
                e.printStackTrace();
            }

            if (youtubeUri != null) {
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);

                if (youtubeIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(youtubeIntent);
                }
            }
        }
    }
}
