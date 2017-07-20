package id.co.webpresso.yohanes.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.webpresso.yohanes.popularmovies.adapter.MovieReviewsAdapter;
import id.co.webpresso.yohanes.popularmovies.adapter.MovieTrailersAdapter;
import id.co.webpresso.yohanes.popularmovies.data.MovieContract;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

public class MovieDetailActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_DETAIL_LOADER_ID = 2;

    private Uri contentUri;
    private Cursor cursor;
    private Integer favoritedAt;
    private Trailers trailers;
    private MovieTrailersAdapter trailersAdapter;

    private Reviews reviews;
    private MovieReviewsAdapter reviewsAdapter;
    private CoordinatorLayout rootLayout;

    @BindView(R.id.tv_movie_detail_name) TextView movieNameTextView;
    @BindView(R.id.tv_movie_detail_poster) ImageView moviePosterImageView;
    @BindView(R.id.tv_movie_detail_release) TextView movieReleaseTextView;
    @BindView(R.id.tv_movie_detail_duration) TextView movieDurationTextView;
    @BindView(R.id.tv_movie_detail_rating) TextView movieRatingTextView;
    @BindView(R.id.tv_movie_detail_overview) TextView movieOverviewTextView;
    @BindView(R.id.pb_movie_detail) ProgressBar progressBar;
    @BindView(R.id.tv_movie_detail_error_message) TextView errorMessage;
    @BindView(R.id.btn_toggle_favorite) Button favoriteToggleButton;
    @BindView(R.id.movie_detail_trailers) View trailersLayout;
    @BindView(R.id.movie_detail_reviews) View reviewsLayout;
    @BindView(R.id.movie_detail_backdrop) ImageView movieBackdropImageView;
    @BindView(R.id.movie_detail_backdrop_layout) CollapsingToolbarLayout movieDetailBackdropLayout;

    class Trailers {
        @BindView(R.id.movie_detail_trailer_list) RecyclerView trailerListRecyclerView;
        @BindView(R.id.movie_detail_trailers_title) TextView trailersTitleTextView;
        @BindView(R.id.movie_detail_no_trailers) TextView noTrailersTextView;
    }

    class Reviews {
        @BindView(R.id.movie_detail_review_list) RecyclerView reviewListRecyclerView;
        @BindView(R.id.movie_detail_reviews_title) TextView reviewsTitleTextView;
        @BindView(R.id.movie_detail_no_reviews) TextView noReviewsTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        rootLayout = (CoordinatorLayout) findViewById(R.id.movie_detail_root_layout);

        ButterKnife.bind(this);

        // Trailers RecyclerView setup
        trailers = new Trailers();
        ButterKnife.bind(trailers, trailersLayout);

        LinearLayoutManager trailersLayoutManager = new LinearLayoutManager(this);

        trailersAdapter = new MovieTrailersAdapter(this);

        trailers.trailerListRecyclerView.setAdapter(trailersAdapter);
        trailers.trailerListRecyclerView.setLayoutManager(trailersLayoutManager);

        // Reviews RecyclerView setup
        reviews = new Reviews();
        ButterKnife.bind(reviews, reviewsLayout);

        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsLayoutManager.setAutoMeasureEnabled(true);

        reviewsAdapter = new MovieReviewsAdapter(this);

        reviews.reviewListRecyclerView.setAdapter(reviewsAdapter);
        reviews.reviewListRecyclerView.setLayoutManager(reviewsLayoutManager);


        contentUri = getIntent().getData();

        getSupportActionBar().setTitle(getResources().getString(R.string.movie_detail_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        renderProgress();
        getSupportLoaderManager().restartLoader(MOVIE_DETAIL_LOADER_ID, null, this);
    }

    /**
     * Render movie contents
     */
    public void renderMovie() {
        movieNameTextView.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_NAME)));

        try {
            Date releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_RELEASE_DATE)));
            movieReleaseTextView.setText(new SimpleDateFormat("dd MMMM yyyy").format(releaseDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        movieRatingTextView.setText(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_VOTE_AVERAGE)) + "/" + 10);
        changeRatingColor(movieRatingTextView);

        movieOverviewTextView.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_OVERVIEW)));

        String runtime = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_RUNTIME));

        if (runtime != null) {
            movieDurationTextView.setText(runtime + "mins");
        }

        Drawable favoriteIcon;
        String favoriteLabel;
        favoritedAt = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_FAVORITED_AT));

        if (favoritedAt == null || favoritedAt < 1) {
            favoriteIcon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_24dp);
            favoriteLabel = getString(R.string.favorite_text);
        } else {
            favoriteIcon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_24dp);
            favoriteLabel = getString(R.string.unfavorite_text);
        }

        favoriteToggleButton.setCompoundDrawablesWithIntrinsicBounds(favoriteIcon, null, null, null);
        favoriteToggleButton.setText(favoriteLabel);

        Picasso.with(this)
                .load(MovieDbUtility.getPosterPath(
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER_PATH))
                        , "w500"))
                .into(moviePosterImageView);

        Picasso.with(this)
                .load(MovieDbUtility.getPosterPath(
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER_PATH))
                        , "w600"))
                .into(movieBackdropImageView);

        // Initialized trailers
        if (!trailersAdapter.initialized) {
            new FetchTrailersAsyncTask().execute(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID)));
        }

        // Initialized reviews
        if (!reviewsAdapter.initialized) {
            new FetchReviewsAsyncTask().execute(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID)));
        }
    }

    /**
     * Show loading and hide presentation
     */
    public void renderProgress() {
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
    }

    /**
     * Toggle movie as favorite/unfavorite
     */
    public void toggleFavorite(View view) {
        String notificationMessage;

        final Uri updateContentUri = MovieContract.MovieEntry.MOVIE_CONTENT_URI
                .buildUpon()
                .appendPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry._ID)))
                .build();

        ContentValues contentValues = new ContentValues();
        final ContentValues undoContentValues = new ContentValues();

        long currentTimestamp = System.currentTimeMillis() / 1000L;

        if (favoritedAt == null || favoritedAt < 1) {
            notificationMessage = getString(R.string.favorite_notification);
            contentValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_FAVORITED_AT, currentTimestamp);
            undoContentValues.putNull(MovieContract.MovieEntry.MOVIE_COLUMN_FAVORITED_AT);
        } else {
            notificationMessage = getString(R.string.unfavorite_notification);
            contentValues.putNull(MovieContract.MovieEntry.MOVIE_COLUMN_FAVORITED_AT);
            undoContentValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_FAVORITED_AT, currentTimestamp);
        }

        getContentResolver().update(updateContentUri, contentValues, null, null);

        Snackbar notificationSnackbar = Snackbar.make(
                rootLayout,
                notificationMessage,
                Snackbar.LENGTH_LONG
            );

        notificationSnackbar.setAction(
                getString(R.string.undo_label),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getContentResolver().update(updateContentUri, undoContentValues, null, null);
                    }
                }
        );
        notificationSnackbar.setActionTextColor(ContextCompat.getColor(this, R.color.red));

        notificationSnackbar.show();
    }

    /**
     * Change rating background color based on vote average
     * @param tv
     */
    private void changeRatingColor(TextView tv) {
        Integer backgroundColor = null;
        Integer textColor = null;
        long voteAverage = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_VOTE_AVERAGE));

        if (voteAverage > 8) {
            backgroundColor = ContextCompat.getColor(this, R.color.goodRatingBackground);
            textColor = ContextCompat.getColor(this, R.color.goodRatingText);
        } else if (voteAverage > 6) {
            backgroundColor = ContextCompat.getColor(this, R.color.mediumRatingBackground);
            textColor = ContextCompat.getColor(this, R.color.mediumRatingText);
        } else {
            backgroundColor = ContextCompat.getColor(this, R.color.badRatingBackground);
            textColor = ContextCompat.getColor(this, R.color.badRatingText);
        }

        tv.setBackgroundColor(backgroundColor);
        tv.setTextColor(textColor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_DETAIL_LOADER_ID:
                return new CursorLoader(
                        this,
                        contentUri,
                        null,
                        null,
                        null,
                        null
                    );
            default:
                throw new RuntimeException("Loader not found.");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progressBar.setVisibility(View.GONE);

        this.cursor = data;

        if (this.cursor != null && this.cursor.moveToFirst()) {
            // Load more info from API
            MovieDbUtility.syncSpecificMovie(this, this.cursor.getInt(this.cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID)));

            renderMovie();
        } else {
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        renderProgress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class FetchTrailersAsyncTask extends AsyncTask<Integer, Void, JSONObject[]> {

        @Override
        protected JSONObject[] doInBackground(Integer... params) {
            MovieDbUtility movieDbUtility = new MovieDbUtility(MovieDetailActivity.this);

            return movieDbUtility.getTrailers(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject[] jsonObjects) {
            trailers.trailersTitleTextView.setText(
                    MovieDetailActivity.this.getResources().getQuantityString(
                            R.plurals.movie_detail_trailer_title,
                            jsonObjects.length,
                            jsonObjects.length)
                    );
            trailersAdapter.setTrailers(jsonObjects);

            if (jsonObjects.length > 0) {
                trailers.noTrailersTextView.setVisibility(View.GONE);
            } else {
                trailers.noTrailersTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private class FetchReviewsAsyncTask extends AsyncTask<Integer, Void, JSONObject[]> {

        @Override
        protected JSONObject[] doInBackground(Integer... params) {
            MovieDbUtility movieDbUtility = new MovieDbUtility(MovieDetailActivity.this);

            return movieDbUtility.getReviews(params[0]);
        }

        @Override
        protected void onPostExecute(JSONObject[] jsonObjects) {
            reviews.reviewsTitleTextView.setText(
                    MovieDetailActivity.this.getResources().getQuantityString(
                            R.plurals.movie_detail_trailer_review,
                            jsonObjects.length,
                            jsonObjects.length)
            );
            reviewsAdapter.setReviews(jsonObjects);

            if (jsonObjects.length > 0) {
                reviews.noReviewsTextView.setVisibility(View.GONE);
            } else {
                reviews.noReviewsTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
