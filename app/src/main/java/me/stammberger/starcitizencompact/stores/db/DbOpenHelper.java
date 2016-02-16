package me.stammberger.starcitizencompact.stores.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import me.stammberger.starcitizencompact.stores.db.tables.commlink.CommLinkModelTable;
import me.stammberger.starcitizencompact.stores.db.tables.commlink.ContentBlock1Table;
import me.stammberger.starcitizencompact.stores.db.tables.commlink.ContentBlock2Table;
import me.stammberger.starcitizencompact.stores.db.tables.commlink.ContentBlock4Table;
import me.stammberger.starcitizencompact.stores.db.tables.commlink.ContentWrapperTable;

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
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CommLinkModelTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentBlock1Table.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentBlock2Table.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentBlock4Table.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ContentWrapperTable.TABLE);
    }
}
