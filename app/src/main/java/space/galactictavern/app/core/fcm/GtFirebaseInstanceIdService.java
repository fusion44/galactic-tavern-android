package space.galactictavern.app.core.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.pixplicity.easyprefs.library.Prefs;

import space.galactictavern.app.Secrets;
import space.galactictavern.app.core.Utility;
import space.galactictavern.app.core.retrofit.FcmApiService;
import space.galactictavern.app.models.common.StandardResponse;

public class GtFirebaseInstanceIdService extends FirebaseInstanceIdService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        try {

            // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
            String token = FirebaseInstanceId.getInstance().getToken();
            StandardResponse resp = FcmApiService.Factory.getInstance()
                    .registerDevice(Secrets.GT_API_SECRET, token, "").toBlocking().first();
            if (!resp.successful) {
                throw new Exception("Failed to complete token refresh: " + resp.err, null);
            }

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            Prefs.putBoolean(Utility.PREFS_FCM_DEVICE_REGISTRATION_TOKEN_SENT, true);
        } catch (Exception e) {
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            Prefs.putBoolean(Utility.PREFS_FCM_DEVICE_REGISTRATION_TOKEN_SENT, false);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Utility.FCM_DEVICE_REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
