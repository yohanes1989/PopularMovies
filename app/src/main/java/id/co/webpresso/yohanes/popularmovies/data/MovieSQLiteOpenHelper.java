package id.co.webpresso.yohanes.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class to open connection to SQLite
 */

public class MovieSQLiteOpenHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "movies.db";
    private final static int DATABASE_VERSION = 1;

    public MovieSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "(" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_NAME + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_POPULARITY + " REAL, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_VOTE_AVERAGE + " REAL, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_VOTE_COUNT + " INTEGER, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_POSTER_PATH + " TEXT, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_RELEASE_DATE + " TEXT, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_ADULT + " INTEGER, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_RUNTIME + " INTEGER, " +
                MovieContract.MovieEntry.MOVIE_COLUMN_FAVORITED_AT + " INTEGER, " +
                "UNIQUE (" + MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID + ") ON CONFLICT REPLACE" +
                ");";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String dropIfExistsQuery = "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(dropIfExistsQuery);

        onCreate(sqLiteDatabase);
    }
}
