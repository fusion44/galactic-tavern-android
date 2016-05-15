package space.galactictavern.app.stores.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import space.galactictavern.app.stores.db.tables.FavoritesTable;
import space.galactictavern.app.stores.db.tables.commlink.CommLinkModelTable;
import space.galactictavern.app.stores.db.tables.commlink.ContentBlock1Table;
import space.galactictavern.app.stores.db.tables.commlink.ContentBlock2Table;
import space.galactictavern.app.stores.db.tables.commlink.ContentBlock4Table;
import space.galactictavern.app.stores.db.tables.commlink.ContentWrapperTable;
import space.galactictavern.app.stores.db.tables.forums.ForumModelTable;
import space.galactictavern.app.stores.db.tables.user.UserSearchHistoryEntryTable;

/**
 * Base OpenHelper class as necessary by StorIO
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "sci_db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(CommLinkModelTable.getCreateTableQuery());
        db.execSQL(ContentBlock1Table.getCreateTableQuery());
        db.execSQL(ContentBlock2Table.getCreateTableQuery());
        db.execSQL(ContentBlock4Table.getCreateTableQuery());
        db.execSQL(ContentWrapperTable.getCreateTableQuery());
        db.execSQL(UserSearchHistoryEntryTable.getCreateTableQuery());
        db.execSQL(ForumModelTable.getCreateTableQuery());
        db.execSQL(FavoritesTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CommLinkModelTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentBlock1Table.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentBlock2Table.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentBlock4Table.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentWrapperTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UserSearchHistoryEntryTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ForumModelTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesTable.TABLE);
    }
}
