package me.stammberger.starcitizencompact.core;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.stammberger.starcitizencompact.R;
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
     *
     * @param c         The Context
     * @param timestamp The unix timestamp
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
}
