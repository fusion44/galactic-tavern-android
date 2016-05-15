package space.galactictavern.app.stores.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import space.galactictavern.app.stores.db.tables.FavoritesTable;
import space.galactictavern.app.stores.db.tables.commlink.CommLinkModelTable;
import space.galactictavern.app.stores.db.tables.commlink.ContentBlock1Table;
import space.galactictavern.app.stores.db.tables.commlink.ContentBlock2Table;
import space.galactictavern.app.stores.db.tables.commlink.ContentBlock4Table;
import space.galactictavern.app.stores.db.tables.commlink.ContentWrapperTable;

/**
 * Android Content provider.
 */
@SuppressWarnings("ConstantConditions")
public class GtContentProvider extends ContentProvider {

    @NonNull
    public static final String AUTHORITY = "space.galactictavern.app.stores.db.gt_provider";

    private static final String PATH_COMM_LINK = "comm-link";
    public static final Uri URI_COMM_LINK = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINK).build();
    private static final int URI_MATCHER_CODE_COMM_LINK = 1;

    private static final String PATH_COMM_LINKS = "comm-links";
    public static final Uri URI_COMM_LINKS = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINKS).build();
    private static final int URI_MATCHER_CODE_COMM_LINKS = 2;

    private static final String PATH_COMM_LINK_WRAPPER_IDS = "comm-link-wrapper-ids";
    public static final Uri URI_COMM_LINK_WRAPPER_IDS = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINK_WRAPPER_IDS).build();
    private static final int URI_MATCHER_CODE_COMM_LINK_WRAPPER_IDS = 3;

    // Block 4
    private static final String PATH_COMM_LINK_WRAPPER_BLOCK4 = "wrapper-block-4";
    public static final Uri URI_COMM_LINK_WRAPPER_BLOCK4 = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINK_WRAPPER_BLOCK4).build();
    private static final int URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK4 = 4;

    // Block 2
    private static final String PATH_COMM_LINK_WRAPPER_BLOCK2 = "wrapper-block-2";
    public static final Uri URI_COMM_LINK_WRAPPER_BLOCK2 = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINK_WRAPPER_BLOCK2).build();
    private static final int URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK2 = 5;

    // Block 2
    private static final String PATH_COMM_LINK_WRAPPER_BLOCK1 = "wrapper-block-1";
    public static final Uri URI_COMM_LINK_WRAPPER_BLOCK1 = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_COMM_LINK_WRAPPER_BLOCK1).build();
    private static final int URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK1 = 6;

    // Favorites
    private static final String PATH_FAVORITE = "favorite";
    public static final Uri URI_FAVORITE = Uri.parse("content://" + AUTHORITY)
            .buildUpon().appendPath(PATH_FAVORITE).build();
    private static final int URI_MATCHER_CODE_FAVORITE = 7;


    private static final UriMatcher URI_MATCHER = new UriMatcher(0);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINK, URI_MATCHER_CODE_COMM_LINK);
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINKS, URI_MATCHER_CODE_COMM_LINKS);
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINK_WRAPPER_IDS, URI_MATCHER_CODE_COMM_LINK_WRAPPER_IDS);
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINK_WRAPPER_BLOCK4, URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK4);
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINK_WRAPPER_BLOCK2, URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK2);
        URI_MATCHER.addURI(AUTHORITY, PATH_COMM_LINK_WRAPPER_BLOCK1, URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK1);
        URI_MATCHER.addURI(AUTHORITY, PATH_FAVORITE, URI_MATCHER_CODE_FAVORITE);
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
            case URI_MATCHER_CODE_COMM_LINK:
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
            case URI_MATCHER_CODE_COMM_LINK_WRAPPER_IDS:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                ContentWrapperTable.TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
            case URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK4:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                ContentBlock4Table.TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
            case URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK2:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                ContentBlock2Table.TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
            case URI_MATCHER_CODE_COMM_LINK_WRAPPER_BLOCK1:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                ContentBlock1Table.TABLE,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
            case URI_MATCHER_CODE_FAVORITE:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                FavoritesTable.TABLE,
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
