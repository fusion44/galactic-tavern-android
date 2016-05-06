package me.stammberger.galactictavern.core.gcm;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import me.stammberger.galactictavern.core.Utility;
import me.stammberger.galactictavern.models.commlink.CommLinkModel;
import timber.log.Timber;

public class GtGcmListenerService extends GcmListenerService {

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Timber.d("Got message from %s", from);

        String dat = data.getString("CommLink");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CommLinkModel m = gson.fromJson(dat, CommLinkModel.class);

        if (from.startsWith("/topics/")) {
            // Not yet!
            Timber.d("message received from some topic.");
        } else {
            ArrayList<CommLinkModel> commLinkModels = new ArrayList<>(1);
            commLinkModels.add(m);
            Utility.buildCommLinkNotification(this, commLinkModels);
        }
    }
}