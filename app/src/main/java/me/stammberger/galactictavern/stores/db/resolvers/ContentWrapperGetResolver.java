package me.stammberger.galactictavern.stores.db.resolvers;


import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;

import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.models.commlink.ContentBlock1;
import me.stammberger.galactictavern.models.commlink.ContentBlock2;
import me.stammberger.galactictavern.models.commlink.ContentBlock4;
import me.stammberger.galactictavern.models.commlink.Wrapper;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock1Table;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock2Table;
import me.stammberger.galactictavern.stores.db.tables.commlink.ContentBlock4Table;

public class ContentWrapperGetResolver extends DefaultGetResolver<Wrapper> {

    @NonNull
    @Override
    public Wrapper mapFromCursor(@NonNull Cursor cursor) {
        final StorIOSQLite storIOSQLite = GtApplication.getInstance().getStorIOSQLite();

        Wrapper wrapper = new Wrapper();

        wrapper.id = cursor.getLong(cursor.getColumnIndex("_id"));
        wrapper.commLinkId = cursor.getLong(cursor.getColumnIndex("comm_link_id"));
        wrapper.contentBlock1DbId = cursor.getLong(cursor.getColumnIndex("id_block_1"));
        wrapper.setContentBlock1(getBlock1(storIOSQLite, wrapper.contentBlock1DbId));
        wrapper.contentBlock2DbId = cursor.getLong(cursor.getColumnIndex("id_block_2"));
        wrapper.setContentBlock2(getBlock2(storIOSQLite, wrapper.contentBlock2DbId));
        wrapper.contentBlock4DbId = cursor.getLong(cursor.getColumnIndex("id_block_4"));
        wrapper.setContentBlock4(getBlock4(storIOSQLite, wrapper.contentBlock4DbId));

        /*
        Not sure why this never really worked. Keeping it here for reference.
        The action in .subscribe was called, but mapFromCursor would never return??
        Fetching data from database in getBlockX as blocking

        Observable.zip(
                getBlock1(storIOSQLite, wrapper),
                getBlock2(storIOSQLite, wrapper),
                getBlock4(storIOSQLite, wrapper),
                (contentBlock1, contentBlock2, contentBlock4) -> {
                    HashMap<String, Object> map = new HashMap<>(3);
                    map.put(BLOCK_1_KEY, contentBlock1);
                    map.put(BLOCK_2_KEY, contentBlock2);
                    map.put(BLOCK_4_KEY, contentBlock4);
                    return map;
                })
                .toBlocking()
                .subscribe((map) -> {
                    Timber.d("got results");
            if (map.get(BLOCK_1_KEY) instanceof ContentBlock1) {
                wrapper.setContentBlock1((ContentBlock1) map.get(BLOCK_1_KEY));
            }
            if (map.get(BLOCK_2_KEY) instanceof ContentBlock2) {
                wrapper.setContentBlock2((ContentBlock2) map.get(BLOCK_2_KEY));
            }
            if (map.get(BLOCK_4_KEY) instanceof ContentBlock4) {
                wrapper.setContentBlock4((ContentBlock4) map.get(BLOCK_4_KEY));
            }
        });*/

        return wrapper;
    }

    /**
     * Get block 1 Observable
     */
    private ContentBlock1 getBlock1(StorIOSQLite storIOSQLite, Long id) {
        Query q = Query.builder()
                .table(ContentBlock1Table.TABLE)
                .where(ContentBlock1Table.COLUMN_ID + " = ?")
                .whereArgs(id)
                .build();

        return storIOSQLite.get()
                .object(ContentBlock1.class)
                .withQuery(q)
                .prepare()
                .executeAsBlocking();
    }

    /**
     * Get block 2 Observable
     */
    private ContentBlock2 getBlock2(StorIOSQLite storIOSQLite, Long id) {
        Query q = Query.builder()
                .table(ContentBlock2Table.TABLE)
                .where(ContentBlock2Table.COLUMN_ID + " = ?")
                .whereArgs(id)
                .build();

        return storIOSQLite.get()
                .object(ContentBlock2.class)
                .withQuery(q)
                .prepare()
                .executeAsBlocking();
    }

    /**
     * Get block 4 Observable
     */
    private ContentBlock4 getBlock4(StorIOSQLite storIOSQLite, Long id) {
        Query q = Query.builder()
                .table(ContentBlock4Table.TABLE)
                .where(ContentBlock4Table.COLUMN_ID + " = ?")
                .whereArgs(id)
                .build();

        return storIOSQLite.get()
                .object(ContentBlock4.class)
                .withQuery(q)
                .prepare()
                .executeAsBlocking();
    }
}
