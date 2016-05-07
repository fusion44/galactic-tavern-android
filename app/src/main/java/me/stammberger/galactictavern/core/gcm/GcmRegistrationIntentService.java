package me.stammberger.galactictavern.core.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;

import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.Secrets;
import me.stammberger.galactictavern.core.retrofit.GcmApiService;
import me.stammberger.galactictavern.models.common.StandardResponse;

public class GcmRegistrationIntentService extends IntentService {

    public static final String DEVICE_REGISTRATION_TOKEN_SENT = "DEVICE_REG_ID_SENT";
    public static final String DEVICE_REGISTRATION_COMPLETE = "DEVICE_REG_COMPLETE";
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            Prefs.putBoolean(DEVICE_REGISTRATION_TOKEN_SENT, true);
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            Prefs.putBoolean(DEVICE_REGISTRATION_TOKEN_SENT, false);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(DEVICE_REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to backend server.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) throws Exception {
        // http://stackoverflow.com/questions/3213205/how-to-detect-system-information-like-os-or-device-type
        String s = "Device-infos:";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";

        StandardResponse resp = GcmApiService.Factory.getInstance()
                .registerDevice(Secrets.GT_API_SECRET, token, s).toBlocking().first();
        if (!resp.successful) {
            throw new Exception("Failed to complete token refresh: " + resp.err, null);
        }
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}