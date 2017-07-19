package id.co.webpresso.yohanes.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Content Provider for Movies
 */

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_DETAIL = 101;

    private static UriMatcher uriMatcher = buildUriMatcher();

    private MovieSQLiteOpenHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final String contentAuthority = MovieContract.CONTENT_AUTHORITY;
        final String moviePath = MovieContract.MovieEntry.MOVIE_PATH;

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Match movie index
        uriMatcher.addURI(contentAuthority, moviePath, CODE_MOVIE);

        // Match movie detail
        uriMatcher.addURI(contentAuthority, moviePath + "/#", CODE_MOVIE_DETAIL);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieSQLiteOpenHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        int match = uriMatcher.match(uri);

        Cursor cursor;

        switch (match) {
            case CODE_MOVIE: {
                String page = uri.getQueryParameter("page");
                page = page == null?"1":page;

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1,
                        "0, 20"
                );

                break;
            }

            case CODE_MOVIE_DETAIL: {
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String _id = uri.getPathSegments().get(1);

                cursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        strings,
                        MovieContract.MovieEntry._ID + " = ? ",
                        new String[]{_id},
                        null,
                        null,
                        s1
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Uri " + uri + " not found.");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int match = uriMatcher.match(uri);

        int rowInserted = 0;

        switch (match) {
            case CODE_MOVIE:
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        int updatedRows = db.update(
                                MovieContract.MovieEntry.TABLE_NAME,
                                value,
                                MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID + " = ?",
                                new String[]{value.getAsString(MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID)});

                        if (updatedRows < 1) {
                            long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);

                            if (_id != -1) {
                                rowInserted++;
                            }
                        }
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int match = uriMatcher.match(uri);

        Uri newContentUri;

        switch (match) {
            case CODE_MOVIE: {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                long _id;
                String updateWhere = MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID + " = ?";
                String[] updateWhereArgs = new String[]{contentValues.getAsString(MovieContract.MovieEntry.MOVIE_COLUMN_EXTERNAL_ID)};

                // Insert or update if exists
                int updatedRows = db.update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        updateWhere,
                        updateWhereArgs);

                if (updatedRows < 1) {
                    _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                } else {
                    Cursor cursor = db.query(
                            MovieContract.MovieEntry.TABLE_NAME,
                            new String[]{MovieContract.MovieEntry._ID},
                            updateWhere,
                            updateWhereArgs,
                            null,
                            null,
                            null);

                    if (cursor.moveToFirst()) {
                        _id = cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                    } else {
                        _id = -1;
                    }
                }

                if (_id > 0) {
                    newContentUri = ContentUris.withAppendedId(uri, _id);
                } else {
                    throw new SQLException("Failed to insert into Uri: " + uri);
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Uri: " + uri +" not found");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return newContentUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int match = uriMatcher.match(uri);

        int updatedRows = 0;

        switch (match) {
            case CODE_MOVIE_DETAIL: {
                String _id = uri.getPathSegments().get(1);

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                updatedRows = db.update(MovieContract.MovieEntry.TABLE_NAME, contentValues, MovieContract.MovieEntry._ID + " = ?", new String[]{_id});

                if (updatedRows < 1) {
                    throw new SQLException("Failed to update into Uri: " + uri);
                }

                break;
            }

            default:
                throw new UnsupportedOperationException("Uri: " + uri +" not found");
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return updatedRows;
    }
}
