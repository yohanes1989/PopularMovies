package id.co.webpresso.yohanes.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import id.co.webpresso.yohanes.popularmovies.adapter.MoviesAdapter;
import id.co.webpresso.yohanes.popularmovies.data.MovieContract;
import id.co.webpresso.yohanes.popularmovies.sync.MovieSyncIntentService;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

public class MovieIndexActivity extends AppCompatActivity
    implements MoviesAdapter.MovieClickHandlerInterface, LoaderManager.LoaderCallbacks<Cursor> {
    private final static String TAG = MovieIndexActivity.class.getSimpleName();
    private final static String STATE_SORT = "movieSort";
    private final static int MOVIE_LOADER_ID = 1;

    /**
     * Variable to hold current movie sort
     */
    private static String currentSort;

    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextView noFavMessage;
    private RecyclerView movieIndexView;
    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_index);

        progressBar = (ProgressBar) findViewById(R.id.pb_movie_index);
        errorMessage = (TextView) findViewById(R.id.tv_movie_index_error_message);
        noFavMessage = (TextView) findViewById(R.id.tv_movie_index_no_favorite);

        moviesAdapter = new MoviesAdapter(getApplicationContext(), this);
        GridLayoutManager moviesLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.movie_index_columns));

        movieIndexView = (RecyclerView) findViewById(R.id.rv_movie_index);
        movieIndexView.setHasFixedSize(true);
        movieIndexView.setLayoutManager(moviesLayoutManager);
        movieIndexView.setAdapter(moviesAdapter);

        if (savedInstanceState != null) {
            currentSort = savedInstanceState.getString(STATE_SORT);
        } else {
            currentSort = (currentSort != null)?currentSort: MovieContract.MovieEntry.SORT_POPULAR;
        }

        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.action_sort_favorites:
                currentSort = MovieContract.MovieEntry.SORT_FAVORITES;
                loadMovies();
                return true;
            case R.id.action_sort_popular_desc:
                currentSort = MovieContract.MovieEntry.SORT_POPULAR;
                loadMovies();
                return true;
            case R.id.action_sort_rating_desc:
                currentSort = MovieContract.MovieEntry.SORT_TOP_RATED;
                loadMovies();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_SORT, currentSort);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMovieClick(long movieId) {
        Uri movieDetailUri = MovieContract.MovieEntry.MOVIE_CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(movieId))
                .build();

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.setData(movieDetailUri);

        startActivity(intent);
    }

    /**
     * Show loading and hide presentation
     */
    public void renderProgress() {
        movieIndexView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.GONE);
        noFavMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Load movies based on currentSort
     */
    public void loadMovies() {
        switch (currentSort) {
            case MovieContract.MovieEntry.SORT_FAVORITES:
                getSupportActionBar().setTitle(getResources().getString(R.string.movie_index_favorites_title));
                break;
            case MovieContract.MovieEntry.SORT_POPULAR:
                getSupportActionBar().setTitle(getResources().getString(R.string.movie_index_popularity_title));
                break;
            case MovieContract.MovieEntry.SORT_TOP_RATED:
                getSupportActionBar().setTitle(getResources().getString(R.string.movie_index_top_rated_title));
                break;
        }

        renderProgress();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

        if (currentSort != MovieContract.MovieEntry.SORT_FAVORITES) {
            MovieDbUtility.startSyncImmediately(this, currentSort);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MOVIE_LOADER_ID:
                Uri MOVIE_URI = MovieContract.MovieEntry.MOVIE_CONTENT_URI;

                return new CursorLoader(
                        this,
                        MOVIE_URI,
                        null,
                        MovieContract.MovieEntry.getWhereQuery(currentSort),
                        null,
                        MovieContract.MovieEntry.getSortQuery(currentSort)
                    );

            default:
                throw new RuntimeException("Loader not found.");
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieIndexView.setVisibility(View.VISIBLE);
        moviesAdapter.updateCursor(data);

        if (data.getCount() == 0 && currentSort == MovieContract.MovieEntry.SORT_FAVORITES) {
            noFavMessage.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);

        movieIndexView.smoothScrollToPosition(0);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.updateCursor(null);
    }
}
