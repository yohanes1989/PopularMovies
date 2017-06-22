package id.co.webpresso.yohanes.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.webpresso.yohanes.popularmovies.R;

/**
 * Utility class to help requesting from The Movie DB
 */
public final class MovieDbUtility {
    public static final String API_PATH = "https://api.themoviedb.org/3";
    public static final String MOVIE_PATH = "movie";

    private String apiKey;

    public MovieDbUtility(Context context) {
        apiKey = context.getResources().getString(R.string.themoviedb_api_key);
    }

    public class Movie {
        public Integer id;
        public String posterPath;
        public String overview;
        public String title;
        public Double voteAverage;
        public Integer voteCount;
        public Double popularity;
        public Date releaseDate;
        public Integer runtime;

        public String getPosterPath(String size) {
            return "http://image.tmdb.org/t/p/" + size + posterPath;
        }
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
     * @return Movies list
     */
    public Movie[] getMovies(String type) {
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
    public Movie getMovie(Integer movieId) {
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
     * Parse string from URL request to array of Movie
     * @param jsonResponse
     * @return
     * @throws JSONException
     */
    private Movie[] parseResultsFromJson(String jsonResponse) throws JSONException {
        final String resultsKey = "results";

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray results = jsonObject.getJSONArray(resultsKey);

        Movie[] movies = new Movie[results.length()];

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
    private Movie parseMovieFromJsonObject(JSONObject jsonObject) throws JSONException {
        final String idKey = "id";
        final String posterPathKey = "poster_path";
        final String overviewKey = "overview";
        final String titleKey = "title";
        final String voteAverageKey = "vote_average";
        final String voteCountKey = "vote_count";
        final String popularityKey = "popularity";
        final String releaseDateKey = "release_date";
        final String runtimeKey = "runtime";

        Movie movie = new Movie();
        movie.id = jsonObject.getInt(idKey);
        movie.posterPath = jsonObject.getString(posterPathKey);
        movie.overview = jsonObject.getString(overviewKey);
        movie.title = jsonObject.getString(titleKey);
        movie.voteAverage = jsonObject.getDouble(voteAverageKey);
        movie.voteCount = jsonObject.getInt(voteCountKey);
        movie.popularity = jsonObject.getDouble(popularityKey);

        // Convert release date to Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            movie.releaseDate = dateFormat.parse(jsonObject.getString(releaseDateKey));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (jsonObject.has(runtimeKey)) {
            movie.runtime = jsonObject.getInt(runtimeKey);
        }

        return movie;
    }
}
