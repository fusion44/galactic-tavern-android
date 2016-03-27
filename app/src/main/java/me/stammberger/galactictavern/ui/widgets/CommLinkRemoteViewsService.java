package me.stammberger.galactictavern.ui.widgets;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.core.Utility;
import me.stammberger.galactictavern.stores.db.GtContentProvider;
import me.stammberger.galactictavern.stores.db.tables.commlink.CommLinkModelTable;

/**
 * This Service is responsible to retrieve comm link data data from the {@link GtContentProvider}
 * and fill the ListView of the widget
 * <p>
 * Connection is made by {@link CommLinkWidget} during a widget update
 */
public class CommLinkRemoteViewsService extends RemoteViewsService {
    private static final int INDEX_COMM_LINK_ID = 0;
    private static final int INDEX_DATE_COL = 1;
    private static final int INDEX_TITLE_COL = 2;
    private static final String[] COMM_LINK_COLUMNS = {
            CommLinkModelTable.COLUMN_ID,
            CommLinkModelTable.COLUMN_PUBLISHED_DATE,
            CommLinkModelTable.COLUMN_TITLE
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                data = getContentResolver().query(GtContentProvider.URI_COMM_LINKS,
                        COMM_LINK_COLUMNS,
                        null,
                        null,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_comm_link_item);

                data.moveToPosition(position);

                String date = Utility.getFormattedRelativeTimeSpan(
                        getBaseContext(),
                        data.getLong(INDEX_DATE_COL));

                String title = data.getString(INDEX_TITLE_COL);

                views.setTextViewText(R.id.widgetItemDateTextView, date);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    views.setContentDescription(
                            R.id.widgetItemDateTextView,
                            getBaseContext().getString(R.string.content_desc_widget_date_text, date));
                }

                views.setTextViewText(R.id.widgetItemTitleTextView, title);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    views.setContentDescription(
                            R.id.widgetItemTitleTextView,
                            getBaseContext().getString(R.string.content_desc_widget_comm_link_title, title));
                }

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_comm_link_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(INDEX_COMM_LINK_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}