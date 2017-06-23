package id.co.webpresso.yohanes.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import id.co.webpresso.yohanes.popularmovies.R;
import id.co.webpresso.yohanes.popularmovies.model.Movie;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

/**
 * Adapter for Movie index
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {
    public Movie[] movies;
    private Context context;

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
        return (movies == null)?0:movies.length;
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
     * Set new set of Movie data
     * @param movies
     */
    public void setMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    /**
     * Interface to be implemented for handling click
     */
    public interface MovieClickHandlerInterface {
        void onMovieClick(Movie movie);
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
            movieClickHandler.onMovieClick(movies[getAdapterPosition()]);
        }

        /**
         * Function to be called within onBindViewHolder
         */
        void bind() {
            // Use Picasso to load movie poster
            Picasso.with(context)
                    .load(movies[getAdapterPosition()].getPosterPath("w500"))
                    .into(moviePosterImageView);
        }
    }
}
