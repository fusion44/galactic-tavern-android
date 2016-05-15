package space.galactictavern.app.ui.maps;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.pixplicity.easyprefs.library.Prefs;

import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;
import space.galactictavern.app.stores.StarmapStore;
import space.galactictavern.mapcore.map.GtStarMap;
import space.galactictavern.mapcore.map.data.StarMapData;
import space.galactictavern.mapcore.map.data.SystemsResultset;

/**
 * Fragment for displaying starmap data.
 * <p>
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends AndroidFragmentApplication implements GtStarMap.SystemSelectedCallback {
    public View mLoadingIndicator;
    private PopupWindow mCurrentPopupWindow;
    private StarMapData mBootUpData;
    private GtStarMap mGtStarMap;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        FrameLayout starMapView = (FrameLayout) v.findViewById(R.id.starMapFrameLayout);
        mLoadingIndicator = v.findViewById(R.id.loadingIndicator);
        String mapState = Prefs.getString(getString(R.string.pref_key_maps_camera_state), "");
        mGtStarMap = new GtStarMap(this, mapState);
        View view = initializeForView(mGtStarMap);
        starMapView.addView(view);

        mBootUpData = StarmapStore.get(GtApplication.getInstance().getRxFlux().getDispatcher())
                .getBootUpData();
        if (mBootUpData.data == null) {
            GtApplication.getInstance().getActionCreator().getStarMapBootUpData();
        } else {
            setupStarMap();
        }
        return v;
    }

    private void setupStarMap() {
        if (mBootUpData != null) {
            mLoadingIndicator.setVisibility(View.GONE);
            mGtStarMap.setBootupData(mBootUpData);
        }
    }

    @Override
    public void onSystemSelected(int systemId, int x, int y) {
        getActivity().runOnUiThread(() -> {
            if (mCurrentPopupWindow != null) {
                mCurrentPopupWindow.dismiss();
                mCurrentPopupWindow = null;
            }

            LayoutInflater inflater = (LayoutInflater)
                    getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View popView = inflater.inflate(R.layout.fragment_map_system_detail_popup, null, false);
            Button button = (Button) popView.findViewById(R.id.systemPopupOpenDetailViewButton);
            button.setOnClickListener(v -> {
                Intent i = new Intent(getActivity(), SystemDetailSlidingActivity.class);
                i.putExtra(SystemDetailSlidingActivity.SYSTEM_CODE, systemId);
                startActivity(i);
            });

            SystemsResultset system = mBootUpData.data.systemHashMap.get(systemId);
            TextView tv = (TextView) popView.findViewById(R.id.systemPopupNameDetailTextView);
            tv.setText(system.name);
            tv = (TextView) popView.findViewById(R.id.systemPopupAffiliationTextView);
            tv.setText(system.affiliation.get(0).name);
            tv.setBackgroundColor(Color.parseColor(system.affiliation.get(0).color));
            tv = (TextView) popView.findViewById(R.id.systemPopupAggregatedSize);
            tv.setText(getString(R.string.dist_light_years, system.aggregatedSize));
            tv = (TextView) popView.findViewById(R.id.systemPopupDescriptionTextView);
            tv.setMovementMethod(new ScrollingMovementMethod());
            tv.setText(system.description);

            // offset the popupwindow based on whether the selected system
            // is positioned on the left or right side of the screen
            int offset = 500;
            int x2;
            if (x > 0) {
                x2 = x - offset;
            } else {
                x2 = x + offset;
            }

            mCurrentPopupWindow = new PopupWindow(popView);
            mCurrentPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mCurrentPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mCurrentPopupWindow.setAnimationStyle(R.style.AppTheme_PopupWindowAnimation);
            mCurrentPopupWindow.setFocusable(false);
            mCurrentPopupWindow.setOutsideTouchable(false);
            mCurrentPopupWindow.update();
            mCurrentPopupWindow.showAtLocation(getView(), Gravity.CENTER, x2, y);
        });
    }

    @Override
    public void onPause() {
        Prefs.putString(getString(R.string.pref_key_maps_camera_state), mGtStarMap.getState());
        super.onPause();
    }

    @Override
    public void onTap(int x, int y) {
        getActivity().runOnUiThread(() -> {
            if (mCurrentPopupWindow != null) {
                mCurrentPopupWindow.dismiss();
                mCurrentPopupWindow = null;
            }
        });
    }

    public void setStarMapData(StarMapData starmapData) {
        this.mBootUpData = starmapData;
        setupStarMap();
    }
}
