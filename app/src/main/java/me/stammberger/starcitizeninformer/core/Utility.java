package me.stammberger.starcitizeninformer.core;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @param root the root view
     * @param tag  the tag to search for
     * @return a ArrayList with the found Views. Empty if no View has been found.
     */
    public static List<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }
}
