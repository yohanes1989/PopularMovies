package id.co.webpresso.yohanes.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import id.co.webpresso.yohanes.popularmovies.R;
import id.co.webpresso.yohanes.popularmovies.data.MovieContract;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

/**
 * Adapter for Movie index
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    private Context context;
    private Cursor cursor;

    /**
     * Store the click handler
     */
    private final MovieClickHandlerInterface movieClickHandler;

    public MoviesAdapter(Context context, MovieClickHandlerInterface clickHandler) {
        movieClickHandler = clickHandler;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return (cursor == null)?0:cursor.getCount();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View movieIndexListView = layoutInflater.inflate(R.layout.movie_index_list, parent, false);

        MovieViewHolder vh = new MovieViewHolder(movieIndexListView);

        return vh;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind();
    }

    /**
     * Update Cursor
     * @param cursor
     */
    public void updateCursor(Cursor cursor) {
        if (this.cursor != null)
            this.cursor.close();

        this.cursor = cursor;
        notifyDataSetChanged();
    }

    /**
     * Interface to be implemented for handling click
     */
    public interface MovieClickHandlerInterface {
        void onMovieClick(long movieId);
    }

    /**
     * View holder who is responsible for individual Movie list
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        ImageView moviePosterImageView;

        MovieViewHolder(View view) {
            super(view);
            moviePosterImageView = view.findViewById(R.id.iv_movie_index_list_poster);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());

            int idColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
            long movieId = cursor.getInt(idColumnIndex);

            movieClickHandler.onMovieClick(movieId);
        }

        /**
         * Function to be called within onBindViewHolder
         */
        void bind() {
            cursor.moveToPosition(getAdapterPosition());

            int posterPathColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER_PATH);
            // Use Picasso to load movie poster
            Picasso.with(context)
                    .load(MovieDbUtility.getPosterPath(cursor.getString(posterPathColumnIndex), "w500"))
                    .into(moviePosterImageView);
        }
    }
}
