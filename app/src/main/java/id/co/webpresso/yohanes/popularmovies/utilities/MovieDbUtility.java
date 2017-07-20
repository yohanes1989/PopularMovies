package id.co.webpresso.yohanes.popularmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;

import id.co.webpresso.yohanes.popularmovies.R;
import id.co.webpresso.yohanes.popularmovies.data.MovieContract;
import id.co.webpresso.yohanes.popularmovies.sync.MovieSyncIntentService;

/**
 * Utility class to help requesting from The Movie DB
 */
public final class MovieDbUtility {
    public static final String API_PATH = "https://api.themoviedb.org/3";
    public static final String MOVIE_PATH = "movie";

    private String apiKey;

    public static String getPosterPath(String posterPath, String size) {
        return "http://image.tmdb.org/t/p/" + size + posterPath;
    }

    public static void startSyncImmediately(Context context, String sort) {
        Intent intent = new Intent(context, MovieSyncIntentService.class);
        intent.putExtra("SORT", sort);

        context.startService(intent);
    }

    public static void syncSpecificMovie(Context context, int externalMovieId) {
        Intent intent = new Intent(context, MovieSyncIntentService.class);
        intent.putExtra("EXTERNAL_MOVIE_ID", externalMovieId);

        context.startService(intent);
    }

    public MovieDbUtility(Context context) {
        apiKey = context.getResources().getString(R.string.themoviedb_api_key);
    }

    /**
     * @return API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Helper method to get builder to build query upon
     * @return Builder with API base path and key
     */
    public Uri.Builder getUriBuilder() {
        Uri.Builder builder = Uri
                .parse(API_PATH)
                .buildUpon()
                .appendQueryParameter("api_key", getApiKey());

        return builder;
    }

    /**
     * Get the movies
     * @param type Type of movie list to fetch
     * @return Movies list as ContentValues
     */
    public ContentValues[] getMovies(String type) {
        Uri uri = getUriBuilder()
                .appendPath(MOVIE_PATH)
                .appendPath(type)
                .build();

        URL url = NetworkUtility.getURLFromUri(uri);

        String response = null;

        try {
            response = NetworkUtility.getHttpResponseFromRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                return parseResultsFromJson(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get single movie
     * @param movieId Id of movie to fetch from MovieDB
     * @return Movies list
     */
    public ContentValues getMovie(Integer movieId) {
        Uri uri = getUriBuilder()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(movieId))
                .build();

        URL url = NetworkUtility.getURLFromUri(uri);

        String response = null;

        try {
            response = NetworkUtility.getHttpResponseFromRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                JSONObject movieJsonObject = new JSONObject(response);

                return parseMovieFromJsonObject(movieJsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Helper function to fetch trailers of specific movie
     * @param externalMovieId
     * @return
     */
    public JSONObject[] getTrailers(int externalMovieId) {
        final String resultsKey = "results";

        Uri uri = getUriBuilder()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(externalMovieId))
                .appendPath("videos")
                .build();

        URL url = NetworkUtility.getURLFromUri(uri);

        String response = null;

        JSONObject[] trailers = new JSONObject[]{};

        try {
            response = NetworkUtility.getHttpResponseFromRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                JSONObject trailerObject = new JSONObject(response);

                JSONArray results = trailerObject.getJSONArray(resultsKey);
                trailers = new JSONObject[results.length()];

                for (int i = 0; i < results.length(); i += 1) {
                    trailers[i] = results.getJSONObject(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return trailers;
    }

    /**
     * Helper function to fetch reviews of specific movie
     * @param externalMovieId
     * @return
     */
    public JSONObject[] getReviews(int externalMovieId) {
        final String resultsKey = "results";

        Uri uri = getUriBuilder()
                .appendPath(MOVIE_PATH)
                .appendPath(String.valueOf(externalMovieId))
                .appendPath("reviews")
                .build();

        URL url = NetworkUtility.getURLFromUri(uri);

        String response = null;

        JSONObject[] reviews = new JSONObject[]{};

        try {
            response = NetworkUtility.getHttpResponseFromRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response != null) {
            try {
                JSONObject reviewObject = new JSONObject(response);

                JSONArray results = reviewObject.getJSONArray(resultsKey);
                reviews = new JSONObject[results.length()];

                for (int i = 0; i < results.length(); i += 1) {
                    reviews[i] = results.getJSONObject(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return reviews;
    }

    /**
     * Parse string from URL request to array of Movie
     * @param jsonResponse
     * @return
     * @throws JSONException
     */
    private ContentValues[] parseResultsFromJson(String jsonResponse) throws JSONException {
        final String resultsKey = "results";

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray results = jsonObject.getJSONArray(resultsKey);

        ContentValues[] movies = new ContentValues[results.length()];

        for (int i=0; i < results.length(); i+=1) {
            JSONObject result = results.getJSONObject(i);

            movies[i] = parseMovieFromJsonObject(result);
        }

        return movies;
    }

    /**
     * Parse JSONObject to Movie object
     * @param jsonObject
     * @return
     */
    private ContentValues parseMovieFromJsonObject(JSONObject jsonObject) throws JSONException {
        final String idKey = "id";
        final String posterPathKey = "poster_path";
        final String backdropPathKey = "backdrop_path";
        final String overviewKey = "overview";
        final String titleKey = "title";
        final String voteAverageKey = "vote_average";
        final String voteCountKey = "vote_count";
        final String popularityKey = "popularity";
        final String releaseDateKey = "release_date";
        final String runtimeKey = "runtime";

        ContentValues movie = new ContentValues();
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID, jsonObject.getInt(idKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER_PATH, jsonObject.getString(posterPathKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_BACKDROP_PATH, jsonObject.getString(backdropPathKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_OVERVIEW, jsonObject.getString(overviewKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_NAME, jsonObject.getString(titleKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_VOTE_AVERAGE, jsonObject.getDouble(voteAverageKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_VOTE_COUNT, jsonObject.getInt(voteCountKey));
        movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_RELEASE_DATE, jsonObject.getString(releaseDateKey));

        if (jsonObject.has(popularityKey)) {
            movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_POPULARITY, jsonObject.getDouble(popularityKey));
        }

        if (jsonObject.has(runtimeKey)) {
            movie.put(MovieContract.MovieEntry.MOVIE_COLUMN_RUNTIME, jsonObject.getString(runtimeKey));
        }

        return movie;
    }
}
