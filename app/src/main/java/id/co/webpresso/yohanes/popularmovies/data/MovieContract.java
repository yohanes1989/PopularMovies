package id.co.webpresso.yohanes.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class to describe Movie database
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "id.co.webpresso.yohanes.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final String MOVIE_PATH = "movies";

        public static final Uri MOVIE_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(MOVIE_PATH)
                .build();

        public final static String SORT_POPULAR = "popular";
        public final static String SORT_TOP_RATED = "top_rated";
        public final static String SORT_FAVORITES = "favorites";

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_COLUMN_EXTERNAL_ID = "external_id";
        public static final String MOVIE_COLUMN_NAME = "name";
        public static final String MOVIE_COLUMN_POPULARITY = "popularity";
        public static final String MOVIE_COLUMN_VOTE_COUNT = "vote_count";
        public static final String MOVIE_COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_COLUMN_POSTER_PATH = "poster_path";
        public static final String MOVIE_COLUMN_OVERVIEW = "overview";
        public static final String MOVIE_COLUMN_RELEASE_DATE = "release_date";
        public static final String MOVIE_COLUMN_ADULT = "adult";
        public static final String MOVIE_COLUMN_RUNTIME = "runtime";
        public static final String MOVIE_COLUMN_FAVORITED_AT = "favorited_at";

        public static String getWhereQuery(String sort) {
            switch (sort) {
                case SORT_FAVORITES:
                    return MOVIE_COLUMN_FAVORITED_AT + " IS NOT NULL";
                default:
                    return null;
            }
        }

        public static String getSortQuery(String sort) {
            switch (sort) {
                case SORT_FAVORITES:
                    return MOVIE_COLUMN_FAVORITED_AT + " DESC";
                case SORT_POPULAR:
                    return MOVIE_COLUMN_POPULARITY + " DESC";
                case SORT_TOP_RATED:
                    return MOVIE_COLUMN_VOTE_AVERAGE + " DESC";
                default:
                    return null;
            }
        }
    }
}
