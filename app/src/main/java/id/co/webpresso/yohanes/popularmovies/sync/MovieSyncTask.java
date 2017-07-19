package id.co.webpresso.yohanes.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import id.co.webpresso.yohanes.popularmovies.data.MovieContract;
import id.co.webpresso.yohanes.popularmovies.utilities.MovieDbUtility;

/**
 * Helper class to perform movies syncing tasks
 */

public class MovieSyncTask {
    /**
     * Task to handle Movies syncing
     * @param context
     * @param sort Type of sort to fetch from TheMovieDB.org
     */
    synchronized public static void syncMovies(Context context, String sort) {
        try {
            MovieDbUtility movieDbUtility = new MovieDbUtility(context);

            ContentValues[] movies = movieDbUtility.getMovies(sort);

            if (movies != null && movies.length > 0) {
                ContentResolver contentResolver = context.getContentResolver();

                contentResolver.bulkInsert(MovieContract.MovieEntry.MOVIE_CONTENT_URI, movies);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sync specific movie
     * @param context
     * @param externalMovieId Movie ID to fetch
     */
    synchronized public static void syncMovie(Context context, int externalMovieId) {
        try {
            MovieDbUtility movieDbUtility = new MovieDbUtility(context);

            // Fetch movie detail
            ContentValues movie = movieDbUtility.getMovie(externalMovieId);

            ContentResolver contentResolver = context.getContentResolver();

            if (movie != null) {
                contentResolver.insert(MovieContract.MovieEntry.MOVIE_CONTENT_URI, movie);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
