package space.galactictavern.app.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import space.galactictavern.app.R;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.stores.db.GtContentProvider;
import space.galactictavern.app.stores.db.tables.commlink.CommLinkModelTable;
import space.galactictavern.app.ui.commlinks.CommLinkReaderActivity;

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
            CommLinkModelTable.COLUMN_COMM_LINK_ID,
            CommLinkModelTable.COLUMN_PUBLISHED_DATE,
            CommLinkModelTable.COLUMN_TITLE
    };

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetAdapter(getApplicationContext());
    }

    public static class WidgetAdapter implements RemoteViewsFactory {
        private final Context mContext;
        private Cursor data = null;

        WidgetAdapter(Context c) {
            mContext = c;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            if (data != null) {
                data.close();
            }
            final long identityToken = Binder.clearCallingIdentity();

            data = mContext.getContentResolver().query(GtContentProvider.URI_COMM_LINKS,
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
            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_comm_link_item);

            data.moveToPosition(position);

            String date = Utility.getFormattedRelativeTimeSpan(mContext, data.getLong(INDEX_DATE_COL));

            String title = data.getString(INDEX_TITLE_COL);

            views.setTextViewText(R.id.widgetItemDateTextView, date);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                views.setContentDescription(
                        R.id.widgetItemDateTextView,
                        mContext.getString(R.string.content_desc_widget_date_text, date));
            }

            views.setTextViewText(R.id.widgetItemTitleTextView, title);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                views.setContentDescription(
                        R.id.widgetItemTitleTextView,
                        mContext.getString(R.string.content_desc_widget_comm_link_title, title));
            }

            Bundle extras = new Bundle();
            extras.putLong(CommLinkReaderActivity.COMM_LINK_ITEM, data.getLong(INDEX_COMM_LINK_ID));
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            views.setOnClickFillInIntent(R.id.widgetItemTitleTextView, fillInIntent);

            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return new RemoteViews(mContext.getPackageName(), R.layout.widget_comm_link_item);
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
    }
}
