package me.stammberger.starcitizeninformer.stores.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkContentPartTable;
import me.stammberger.starcitizeninformer.stores.db.tables.CommLinkTable;

/**
 * Base OpenHelper class as necessary by StorIO
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(@NonNull Context context) {
        super(context, "sci_db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(CommLinkTable.getCreateTableQuery());
        db.execSQL(CommLinkContentPartTable.getCreateTableQuery());
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CommLinkTable.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CommLinkContentPartTable.TABLE);
    }
}
