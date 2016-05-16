package space.galactictavern.app.ui.users;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.klinker.android.sliding.SlidingActivity;
import com.neovisionaries.i18n.CountryCode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.actions.Actions;
import space.galactictavern.app.actions.Keys;
import space.galactictavern.app.models.orgs.Organization;
import space.galactictavern.app.models.user.User;
import space.galactictavern.app.models.user.UserOrganizationObject;
import space.galactictavern.app.stores.OrganizationStore;
import space.galactictavern.app.stores.UserStore;

/**
 * This Activity will display all known user details. The behaviour is similar to the stock Android
 * contacts app. When dragging an Activity down it'll close automatically.
 */
public class UserDetailSlidingActivity extends SlidingActivity implements RxViewDispatch {
    public static final String USER_HANDLE = "user_handle";
    private static final String TRACKING_SCREEN_USER_DETAIL_ACTIVITY = "UserDetailActivity";
    @BindView(R.id.userDetailCitizenNumberTextView)
    TextView mCitizenNumberTextView;
    @BindView(R.id.userDetailCountryTextView)
    TextView mCountryTextView;
    @BindView(R.id.userDetailCountryFlagImageView)
    ImageView mCountryFlagImageView;
    @BindView(R.id.userDetailAvatarRoundedImageView)
    ImageView mAvatarImageView;
    @BindView(R.id.userDetailTitleImageView)
    ImageView mUserTitleImageView;
    @BindView(R.id.userDetailTitleTextView)
    TextView mUserDetailTitleTextView;
    @BindView(R.id.userDetailBioTextView)
    TextView mUserBioTextView;
    @BindView(R.id.userDetailDiscussionsTextView)
    TextView mDiscussionsTextView;
    @BindView(R.id.userDetailPostsTextView)
    TextView mPostsTextView;

    private OrganizationStore mOrganizationStore;
    private User mUser;
    private Organization mOrganization;

    @Override
    protected void onResume() {
        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_USER_DETAIL_ACTIVITY);
        super.onResume();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_user_detail);
        ButterKnife.bind(this);
        GtApplication.getInstance().getRxFlux().onActivityCreated(this, savedInstanceState);

        String handle = getIntent().getStringExtra(USER_HANDLE);

        Dispatcher d = GtApplication.getInstance().getRxFlux().getDispatcher();
        mOrganizationStore = OrganizationStore.get(d);
        UserStore userStore = UserStore.get(d);
        mUser = userStore.getUser(handle);
        if (mUser != null) {
            if (mUser.data.userOrganizationObjects != null) {
                boolean requested = false;
                for (UserOrganizationObject userOrganizationObject : mUser.data.userOrganizationObjects) {
                    if (userOrganizationObject.sid == null) {
                        // for some users the API return organizations with null data
                        continue;
                    }

                    mOrganization = mOrganizationStore.getOrganization(userOrganizationObject.sid);
                    if (mOrganization != null) {
                        loadHeaderImage();
                    } else {
                        if (!requested) {
                            // we only want the image of the first organization
                            GtApplication.getInstance().getActionCreator().getOrganizationById(userOrganizationObject.sid);
                            requested = true;
                        }
                    }
                }
            }

            setTitle(mUser.data.handle);

            mCitizenNumberTextView.setText(mUser.data.citizenNumber);

            Glide.with(this)
                    .load(mUser.data.avatar)
                    .into(mAvatarImageView);

            List<CountryCode> byName;
            String countryText = "";
            // Workaround for a bug in the API
            // Some users have their country displayed in the region field
            if (mUser.data.country != null) {
                byName = CountryCode.findByName(".*" + mUser.data.country + ".*");
                countryText = mUser.data.country;
            } else {
                byName = CountryCode.findByName(".*" + mUser.data.region + ".*");
            }

            if (mUser.data.region != null) {
                countryText += mUser.data.region;
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
                    .load(mUser.data.titleImage)
                    .into(mUserTitleImageView);
            mUserDetailTitleTextView.setText(mUser.data.title);

            if (mUser.data.bio != null && !mUser.data.bio.equals("")) {
                mUserBioTextView.setText(Html.fromHtml(mUser.data.bio));
            }

            if (mUser.data.discussionCount != null) {
                mDiscussionsTextView.setText(String.valueOf(mUser.data.discussionCount));
            }

            if (mUser.data.postCount != null) {
                mPostsTextView.setText(String.valueOf(mUser.data.postCount));
            }
        }
    }

    /**
     * Loads the header image background if an organization is loaded
     */
    private void loadHeaderImage() {
        if (mOrganization != null) {
            // check if we have at least one usable image - if not return
            String url = mOrganization.data.coverImage;
            if (url == null || url.equals("")) {
                url = mOrganization.data.logo;
            }
            if (url == null || url.equals("")) {
                return;
            }

            Glide.with(this)
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            setImage(resource);
                        }
                    });
        }
    }

    /**
     * Called by RxFlux whenever a RxStore has received data.
     *
     * @param change The change model with the data
     */
    @Override
    public void onRxStoreChanged(RxStoreChange change) {
        switch (change.getStoreId()) {
            case OrganizationStore.ID:
                switch (change.getRxAction().getType()) {
                    case Actions.GET_ORGANIZATION_BY_ID:
                        Organization o = (Organization) change.getRxAction()
                                .getData().get(Keys.ORGANIZATION_DATA);
                        if (mUser.data.userOrganizationObjects.get(0).sid.equals(o.data.sid)) {
                            mOrganization = o;
                            loadHeaderImage();
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onRxError(RxError error) {

    }

    @Override
    public void onRxViewRegistered() {

    }

    @Override
    public void onRxViewUnRegistered() {

    }

    @Override
    public void onRxStoresRegister() {

    }
}
