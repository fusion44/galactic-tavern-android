package space.galactictavern.app.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import space.galactictavern.app.R;
import space.galactictavern.app.ui.commlinks.CommLinkReaderActivity;
import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 * <p>
 * This widget will display the comm links currently in database.
 */
public class CommLinkWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = context.getString(R.string.widget_header_text);

        // See documentation https://developer.android.com/guide/topics/appwidgets/
        Intent intent = new Intent(context, CommLinkRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_comm_link);
        rv.setTextViewText(R.id.widgetHeader, widgetText);
        rv.setRemoteAdapter(R.id.widgetCommLinkList, intent);

        // launch Activity intent
        Intent activityIntent = new Intent(context, CommLinkReaderActivity.class);
        activityIntent.setAction(CommLinkReaderActivity.ACTION_COMM_LINK_WIDGET_CLICK);
        activityIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))); // Why again? Typo in Android docs?
        PendingIntent pi = PendingIntent.getActivity(context, 0, activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.widgetCommLinkList, pi);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.d("Update comm link widgets");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

