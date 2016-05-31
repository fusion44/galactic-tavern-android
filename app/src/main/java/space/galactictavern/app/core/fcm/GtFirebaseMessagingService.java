package space.galactictavern.app.core.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import space.galactictavern.app.core.Utility;
import space.galactictavern.app.models.commlink.CommLinkModel;
import timber.log.Timber;

public class GtFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("Got message from %s", remoteMessage.getFrom());

        String dat = remoteMessage.getData().get("CommLink");
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        CommLinkModel m = gson.fromJson(dat, CommLinkModel.class);

        if (remoteMessage.getFrom().startsWith("/topics/")) {
            // Not yet!
            Timber.d("message received from some topic.");
        } else {
            ArrayList<CommLinkModel> commLinkModels = new ArrayList<>(1);
            commLinkModels.add(m);
            Utility.buildCommLinkNotification(this, commLinkModels);
        }
    }
}
