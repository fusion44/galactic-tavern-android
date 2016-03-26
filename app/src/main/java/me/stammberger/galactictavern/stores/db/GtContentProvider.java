package me.stammberger.galactictavern.stores.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import me.stammberger.galactictavern.stores.db.tables.commlink.CommLinkModelTable;

/**
 * Android Content provider.
 */
@SuppressWarnings("ConstantConditions")
public class GtContentProvider extends ContentProvider {

    @NonNull
    public static final String AUTHORITY = "me.stammberger.galactictavern.stores.db.gt_provider";

    private static final String PATH_COMM_LINKS = "comm-links";
    public static final Uri URI_COMM_LINKS = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINKS).build();
    private static final int URI_MATCHER_CODE_COMM_LINKS = 1;
    private static final UriMatcher URI_MATCHER = new UriMatcher(1);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINKS, URI_MATCHER_CODE_COMM_LINKS);
    }

    SQLiteOpenHelper sqLiteOpenHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        if (getContext() == null) {
            throw new NullPointerException("Context cannot be null");
        }

        sqLiteOpenHelper = new DbOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_COMM_LINKS:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                CommLinkModelTable.TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );

            default:
                return null;
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final long insertedId;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_COMM_LINKS:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                CommLinkModelTable.TABLE,
                                null,
                                values
                        );
                break;

            default:
                return null;
        }

        if (insertedId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, insertedId);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int numberOfRowsAffected;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_COMM_LINKS:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                CommLinkModelTable.TABLE,
                                values,
                                selection,
                                selectionArgs
                        );
                break;

            default:
                return 0;
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsAffected;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int numberOfRowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_COMM_LINKS:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                CommLinkModelTable.TABLE,
                                selection,
                                selectionArgs
                        );
                break;

            default:
                return 0;
        }

        if (numberOfRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}
