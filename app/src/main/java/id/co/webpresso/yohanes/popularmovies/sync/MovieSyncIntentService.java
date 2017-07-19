package id.co.webpresso.yohanes.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Intent Service dedicated to sync Movies
 */

public class MovieSyncIntentService extends IntentService {
    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String sort = intent.getStringExtra("SORT");

        // If sort is null, we want to fetch specific movie instead
        if (sort != null) {
            MovieSyncTask.syncMovies(this, sort);
        } else {
            int externalMovieId = intent.getIntExtra("EXTERNAL_MOVIE_ID", -1);
            if (externalMovieId > 0) {
                MovieSyncTask.syncMovie(this, externalMovieId);
            }
        }
    }
}
