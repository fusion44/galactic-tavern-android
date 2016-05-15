package space.galactictavern.app.core;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.pixplicity.easyprefs.library.Prefs;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import space.galactictavern.app.R;
import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.ui.MainActivity;
import space.galactictavern.app.ui.commlinks.CommLinkReaderActivity;
import timber.log.Timber;

/**
 * Class for all global utility methods
 */
public class Utility {
    /**
     * The URL to rsi.com
     */
    public static final String RSI_BASE_URL = "https://robertsspaceindustries.com/";

    /**
     * The Notification ID for all notifications related to comm links
     */
    private static final int NOTIFICATION_ID_COMM_LINK = 2210;

    /**
     * Request ID for resolving case play services not installed
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 986;

    public static void buildCommLinkNotification(Context context, List<CommLinkModel> commLinkModels) {
        boolean showNotifications = Prefs.getBoolean(
                context.getString(R.string.pref_key_new_comm_link_notifications),
                true);

        if (showNotifications) {
            if (commLinkModels.size() == 1) {
                // if show notifications is turned on and we have
                // one new comm link -> use a detailed notification
                CommLinkModel m = commLinkModels.get(0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.ic_comm_link_black_24dp);
                builder.setContentTitle(m.getTitle());
                builder.setContentText(m.getSummary());

                Intent activityIntent = new Intent(context, CommLinkReaderActivity.class);
                activityIntent.putExtra(CommLinkReaderActivity.COMM_LINK_ITEM, m.getCommLinkId());
                activityIntent.setAction(CommLinkReaderActivity.ACTION_COMM_LINK_NOTIFICATION_CLICK);
                PendingIntent pi = PendingIntent.getActivity(context, 0, activityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pi);

                Notification n = builder.build();
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_COMM_LINK, n);
            } else if (commLinkModels.size() > 1) {
                // if show notifications is turned on and we have
                // more than one new comm link -> use an inbox style notification

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(
                        context.getString(R.string.notification_big_content_title));
                inboxStyle.setSummaryText(context.getString(
                        R.string.notification_summary_text,
                        commLinkModels.size()));
                for (CommLinkModel model : commLinkModels) {
                    inboxStyle.addLine(model.getTitle());
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.drawable.ic_comm_link_black_24dp);
                builder.setStyle(inboxStyle);

                Intent activityIntent = new Intent(context, MainActivity.class);
                PendingIntent pi = PendingIntent.getActivity(
                        context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pi);

                Notification n = builder.build();
                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_COMM_LINK, n);
            }
        }
    }

    /**
     * Clears all current notifications
     *
     * @param context The current app context
     */
    public static void cancelNotifications(Context context) {
        NotificationManagerCompat.from(context).cancelAll();
    }

    /**
     * Extracts the comm link id from an url
     *
     * @param source Comm link model to extract the id from
     * @return the id of the comm link
     */
    public static int getId(String source) {
        source = source.replace("https://robertsspaceindustries.com/comm-link/", "");
        String[] spl = source.split("/");
        return Integer.parseInt(spl[1].split("-")[0]);
    }

    /**
     * Generates a single String from a List of strings for easy storage in a database
     *
     * @param data      The data to store as String
     * @param separator The separator to separate the data with
     * @return the String for storage in DB
     */
    public static String generateDbStringFromStringList(List<String> data, String separator) {
        String contentDb = "";
        for (int i = 0; i < data.size(); i++) {
            String s = data.get(i);
            if (i + 1 < data.size()) {
                contentDb += s + separator;
            } else {
                contentDb += s;
            }
        }
        return contentDb;
    }

    /**
     * Parses a database String to a List
     *
     * @param data      The database String to parse
     * @param separator The separator where the String will be split
     * @return an ArrayList of Strings
     */
    public static List<String> parseStringListFromDbString(String data, String separator) {
        ArrayList<String> content = new ArrayList<>();
        Collections.addAll(content, data.split(separator));
        return content;
    }

    /**
     * Finds all views with a specific tag.
     * Idea from: http://stackoverflow.com/questions/5062264/find-all-views-with-tag
     *
     * @param root     the root view
     * @param tagId    id of the tag
     * @param tagValue the tag to search for
     * @return a ArrayList with the found Views. Empty if no View has been found.
     */
    public static List<View> getViewsByTag(ViewGroup root, int tagId, String tagValue) {
        ArrayList<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tagId, tagValue));
            }

            final Object tagObj = child.getTag(tagId);
            if (tagObj != null && tagObj.equals(tagValue)) {
                views.add(child);
            }
        }
        return views;
    }

    /**
     * Gets the long manufacturer name for the specified short manufacturer name.
     *
     * @param c         Android Context
     * @param shortName Short name for the manufacturer
     * @return long name for the manufacturer as a string. Empty string if not found.
     */
    public static String getFullManufacturerName(Context c, String shortName) {
        switch (shortName) {
            case "AEGS":
                return c.getString(R.string.sc_manufacturer_aegs);
            case "ANVL":
                return c.getString(R.string.sc_manufacturer_anvl);
            case "BANU":
                return c.getString(R.string.sc_manufacturer_banu);
            case "CNOU":
                return c.getString(R.string.sc_manufacturer_cnou);
            case "CRSD":
                return c.getString(R.string.sc_manufacturer_crsd);
            case "DRAK":
                return c.getString(R.string.sc_manufacturer_drak);
            case "ESPERIA":
                return c.getString(R.string.sc_manufacturer_esperia);
            case "KRGR":
                return c.getString(R.string.sc_manufacturer_krgr);
            case "MISC":
                return c.getString(R.string.sc_manufacturer_misc);
            case "ORIG":
                return c.getString(R.string.sc_manufacturer_orig);
            case "RSI":
                return c.getString(R.string.sc_manufacturer_rsi);
            case "VANDUUL":
                return c.getString(R.string.sc_manufacturer_vanduul);
            case "XIAN":
                return c.getString(R.string.sc_manufacturer_xian);
            default:
                Timber.d("Unknown manufacturer: %s", shortName);
                return "";
        }
    }

    /**
     * Converts a unix timestamp to a human readable String.
     * The output will be relative to current device time
     * <p>
     * Input must be in milliseconds!
     * Unix timestamp is in seconds but Javas timestamp is in milliseconds!
     *
     * @param c         Android Context
     * @param timestamp Java timestamp in milliseconds.
     * @return String in the format "1 hour ago" and "1 week ago"
     */
    public static String getFormattedRelativeTimeSpan(Context c, Long timestamp) {
        DateTime dt = new DateTime(timestamp);
        CharSequence formattedDate = DateUtils.getRelativeTimeSpanString(c, dt);
        return formattedDate.toString();
    }

    /**
     * Gets the forum section name for the specified forum ID
     *
     * @param c       Context for accessing Resources
     * @param forumId Id of the Forum
     * @return the corresponding section name as a String
     */
    public static String getForumSectionForForumId(Context c, String forumId) {
        switch (forumId) {
            case "recruiting-station":
            case "new-recruits":
            case "official-announcements":
            case "general-chat":
            case "arena-commander":
            case "question-answer":
            case "game-ideas":
            case "live-service-notifications":
                return c.getString(R.string.forum_section_star_citizen);
            case "star-citizen-role-play":
            case "fan-art":
            case "fan-fiction":
            case "fan-sites":
            case "modding":
            case "the-next-great-starship":
            case "guilds-squadrons":
            case "gathering-meet-ups":
            case "fan-art-fiction":
                return c.getString(R.string.forum_section_star_citizen_community);
            case "ask-a-developer":
                return c.getString(R.string.forum_section_cig);
            default:
                return c.getString(R.string.unknown);
        }
    }

    /**
     * Returns true if the network is available or about to become available.
     *
     * @param c Context used to get the ConnectivityManager
     * @return true if the network is available
     */
    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(FragmentActivity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.d("Device not supported");
            }
            return false;
        }
        return true;
    }
}
