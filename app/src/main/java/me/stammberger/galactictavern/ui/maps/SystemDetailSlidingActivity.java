package me.stammberger.galactictavern.ui.maps;

import android.os.Bundle;
import android.widget.TextView;

import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.Dispatcher;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;
import com.klinker.android.sliding.SlidingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.actions.Actions;
import me.stammberger.galactictavern.stores.OrganizationStore;

/**
 * This Activity will display all known user details. The behaviour is similar to the stock Android
 * contacts app. When dragging an Activity down it'll close automatically.
 */
public class SystemDetailSlidingActivity extends SlidingActivity implements RxViewDispatch {
    public static final String SYSTEM_CODE = "user_handle";
    private static final String TRACKING_SCREEN_SYSTEM_DETAIL_ACTIVITY = "SystemDetailActivity";
    @Bind(R.id.systemNameTextView)
    public TextView mSystemNameTextView;

    @Override
    protected void onResume() {
        GtApplication.getInstance().trackScreen(TRACKING_SCREEN_SYSTEM_DETAIL_ACTIVITY);
        super.onResume();
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setContent(R.layout.activity_system_detail);
        ButterKnife.bind(this);
        GtApplication.getInstance().getRxFlux().onActivityCreated(this, savedInstanceState);

        String code = getIntent().getStringExtra(SYSTEM_CODE);
        mSystemNameTextView.setText(code);
        Dispatcher d = GtApplication.getInstance().getRxFlux().getDispatcher();

    }

    /**
     * Loads the header image background if an organization is loaded
     */
    private void loadHeaderImage() {

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
