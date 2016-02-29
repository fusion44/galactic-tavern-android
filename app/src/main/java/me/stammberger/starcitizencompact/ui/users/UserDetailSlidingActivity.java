package me.stammberger.starcitizencompact.ui.users;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.klinker.android.sliding.SlidingActivity;
import com.neovisionaries.i18n.CountryCode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @Bind(R.id.userDetailCitizenNumberTextView)
    TextView mCitizenNumberTextView;
    @Bind(R.id.userDetailCountryTextView)
    TextView mCountryTextView;
    @Bind(R.id.userDetailCountryFlagImageView)
    ImageView mCountryFlagImageView;
    @Bind(R.id.userDetailAvatarRoundedImageView)
    ImageView mAvatarImageView;
    @Bind(R.id.userDetailTitleImageView)
    ImageView mUserTitleImageView;
    @Bind(R.id.userDetailTitleTextView)
    TextView mUserDetailTitleTextView;
    @Bind(R.id.userDetailBioTextView)
    TextView mUserBioTextView;
    @Bind(R.id.userDetailDiscussionsTextView)
    TextView mDiscussionsTextView;
    @Bind(R.id.userDetailPostsTextView)
    TextView mPostsTextView;

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        String handle = getIntent().getStringExtra(USER_HANDLE);

        Dispatcher d = SciApplication.getInstance().getRxFlux().getDispatcher();
        UserStore userStore = UserStore.get(d);
        User user = userStore.getUser(handle);
        if (user != null) {
            setTitle(user.data.handle);

            mCitizenNumberTextView.setText(user.data.citizenNumber);

            Glide.with(this)
                    .load(user.data.avatar)
                    .into(mAvatarImageView);

            List<CountryCode> byName;
            String countryText = "";
            // Workaround for a bug in the API
            // Some users have their country displayed in the region field
            if (user.data.country != null) {
                byName = CountryCode.findByName(".*" + user.data.country + ".*");
                countryText = user.data.country;
            } else {
                byName = CountryCode.findByName(".*" + user.data.region + ".*");
            }

            if (user.data.region != null) {
                countryText += user.data.region;
            }

            mCountryTextView.setText(countryText);

            if (byName != null) {
                for (CountryCode countryCode : byName) {
                    String code = countryCode.getAlpha2().toLowerCase();
                    String url = "http://fusion44.bitbucket.org/sci/flags/flags_iso/48/" + code + ".png";
                    Glide.with(this)
                            .load(url)
                            .into(mCountryFlagImageView);
                }
            }

            Glide.with(this)
                    .load(user.data.titleImage)
                    .into(mUserTitleImageView);
            mUserDetailTitleTextView.setText(user.data.title);

            if (user.data.bio != null && !user.data.bio.equals("")) {
                mUserBioTextView.setText(Html.fromHtml(user.data.bio));
            }

            if (user.data.discussionCount != null) {
                mDiscussionsTextView.setText(String.valueOf(user.data.discussionCount));
            }

            if (user.data.postCount != null) {
                mPostsTextView.setText(String.valueOf(user.data.postCount));
            }
        }
    }
}
