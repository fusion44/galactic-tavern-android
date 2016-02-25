package me.stammberger.starcitizencompact.ui.users;

import android.os.Bundle;

import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.klinker.android.sliding.SlidingActivity;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.models.user.User;
import me.stammberger.starcitizencompact.stores.UserStore;

/**
 * This Activity will display all known user details. The behaviour is similar to the stock Android
 * contacts app. When dragging an Activity down it'll close automatically.
 */
public class UserDetailSlidingActivity extends SlidingActivity {
    public static final String USER_HANDLE = "user_handle";

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_user_detail);

        String handle = getIntent().getStringExtra(USER_HANDLE);

        Dispatcher d = SciApplication.getInstance().getRxFlux().getDispatcher();
        UserStore userStore = UserStore.get(d);
        User user = userStore.getUser(handle);
        if (user != null) {
            setTitle(user.data.handle);
        }
    }
}
