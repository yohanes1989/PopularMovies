package id.co.webpresso.yohanes.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import id.co.webpresso.yohanes.popularmovies.adapter.MoviesAdapter;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

public class MovieIndexActivity extends AppCompatActivity
    implements MoviesAdapter.MovieClickHandlerInterface {
    private final static String TAG = MovieIndexActivity.class.getSimpleName();
    private final static String STATE_SORT = "movieSort";
    private final static String SORT_POPULAR = "popular";
    private final static String SORT_TOP_RATED = "top_rated";

    private ProgressBar progressBar;
    private TextView errorMessage;
    private Menu menuView;
    private RecyclerView movieIndexView;
    private MoviesAdapter moviesAdapter;

    /**
     * Variable to hold current movie sort
     */
    private String currentSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_index);

        progressBar = (ProgressBar) findViewById(R.id.pb_movie_index);
        errorMessage = (TextView) findViewById(R.id.tv_movie_index_error_message);

        moviesAdapter = new MoviesAdapter(getApplicationContext(), this);
        GridLayoutManager moviesLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.movie_index_columns));

        movieIndexView = (RecyclerView) findViewById(R.id.rv_movie_index);
        movieIndexView.setHasFixedSize(true);
        movieIndexView.setLayoutManager(moviesLayoutManager);
        movieIndexView.setAdapter(moviesAdapter);

        // TODO: Save the state so when user comes back it shows movies with correct sort
        currentSort = SORT_POPULAR;
        loadMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuView = menu;
        getMenuInflater().inflate(R.menu.sort_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Integer selectedItemId = item.getItemId();

        switch (selectedItemId) {
            case R.id.action_sort_popular_desc:
                currentSort = SORT_POPULAR;
                loadMovies();
                return true;
            case R.id.action_sort_rating_desc:
                currentSort = SORT_TOP_RATED;
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
    public void onMovieClick(Integer movieId) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("MOVIE_ID", movieId);

        startActivity(intent);
    }

    /**
     * Show loading and hide presentation
     */
    public void renderProgress() {
        movieIndexView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Load movies based on currentSort
     */
    public void loadMovies() {
        // TODO: Check option menu so user knows where they are
        //menuView.findItem(R.id.action_sort_popular_desc).setChecked(true);
        //menuView.findItem(R.id.action_sort_popular_desc).setIcon(android.R.drawable.checkbox_on_background);

        switch (currentSort) {
            case SORT_POPULAR:
                getSupportActionBar().setTitle(getResources().getString(R.string.movie_index_popularity_title));
                break;
            case SORT_TOP_RATED:
                getSupportActionBar().setTitle(getResources().getString(R.string.movie_index_top_rated_title));
                break;
        }

        new FetchMoviesTask().execute(currentSort);
    }

    /**
     * Background asynk task to fetch movies
     */
    class FetchMoviesTask extends AsyncTask<String, Void, MovieDbUtility.Movie[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            renderProgress();
        }

        @Override
        protected MovieDbUtility.Movie[] doInBackground(String... strings) {
            MovieDbUtility movieDbUtility = new MovieDbUtility(MovieIndexActivity.this);

            return movieDbUtility.getMovies(strings[0]);
        }

        @Override
        protected void onPostExecute(MovieDbUtility.Movie[] movies) {
            progressBar.setVisibility(View.INVISIBLE);
            movieIndexView.setVisibility(View.VISIBLE);
            moviesAdapter.setMovies(movies);

            if (movies == null) {
                errorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
