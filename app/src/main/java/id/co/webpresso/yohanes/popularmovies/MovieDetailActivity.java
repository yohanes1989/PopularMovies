package id.co.webpresso.yohanes.popularmovies;

import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import id.co.webpresso.yohanes.popularmovies.model.Movie;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

public class MovieDetailActivity extends AppCompatActivity {
    private Movie movie;
    private TextView movieNameTextView;
    private ImageView moviePosterImageView;
    private TextView movieYearTextView;
    private TextView movieDurationTextView;
    private TextView movieRatingTextView;
    private TextView movieOverviewTextView;
    private ProgressBar progressBar;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        progressBar = (ProgressBar) findViewById(R.id.pb_movie_detail);
        errorMessage = (TextView) findViewById(R.id.tv_movie_detail_error_message);
        movieNameTextView = (TextView) findViewById(R.id.tv_movie_detail_name);
        moviePosterImageView = (ImageView) findViewById(R.id.tv_movie_detail_poster);
        movieYearTextView = (TextView) findViewById(R.id.tv_movie_detail_year);
        movieDurationTextView = (TextView) findViewById(R.id.tv_movie_detail_duration);
        movieRatingTextView = (TextView) findViewById(R.id.tv_movie_detail_rating);
        movieOverviewTextView = (TextView) findViewById(R.id.tv_movie_detail_overview);

        movie = getIntent().getParcelableExtra("MOVIE");

        getSupportActionBar().setTitle(getResources().getString(R.string.movie_detail_title));

        new FetchMovieTask().execute(movie.id);
    }

    /**
     * Render movie contents
     */
    public void renderMovie() {
        movieNameTextView.setText(movie.title);
        movieYearTextView.setText(new SimpleDateFormat("yyyy").format(movie.getReleaseDate()));

        movieRatingTextView.setText(movie.voteAverage + "/" + 10);
        changeRatingColor(movieRatingTextView);

        movieOverviewTextView.setText(movie.overview);
        movieDurationTextView.setText(movie.runtime + "mins");

        // Use Picasso to load movie poster
        Picasso.with(this)
                .load(movie.getPosterPath("w500"))
                .into(moviePosterImageView);
    }

    /**
     * Show loading and hide presentation
     */
    public void renderProgress() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void changeRatingColor(TextView tv) {
        Integer backgroundColor = null;
        Integer textColor = null;

        if (movie.voteAverage > 8) {
            backgroundColor = ContextCompat.getColor(this, R.color.goodRatingBackground);
            textColor = ContextCompat.getColor(this, R.color.goodRatingText);
        } else if (movie.voteAverage > 6) {
            backgroundColor = ContextCompat.getColor(this, R.color.mediumRatingBackground);
            textColor = ContextCompat.getColor(this, R.color.mediumRatingText);
        } else {
            backgroundColor = ContextCompat.getColor(this, R.color.badRatingBackground);
            textColor = ContextCompat.getColor(this, R.color.badRatingText);
        }

        tv.setBackgroundColor(backgroundColor);
        tv.setTextColor(textColor);
    }

    /**
     * Background async task to fetch specific movie by ID
     */
    class FetchMovieTask extends AsyncTask<Integer, Void, Movie> {
        @Override
        protected void onPreExecute() {
            renderProgress();
        }

        @Override
        protected Movie doInBackground(Integer... movieIds) {
            MovieDbUtility movieDbUtility = new MovieDbUtility(MovieDetailActivity.this);

            return movieDbUtility.getMovie(movieIds[0]);
        }

        @Override
        protected void onPostExecute(Movie movie) {
            progressBar.setVisibility(View.INVISIBLE);

            MovieDetailActivity.this.movie = movie;

            if (MovieDetailActivity.this.movie != null) {
                renderMovie();
            } else {
                errorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
