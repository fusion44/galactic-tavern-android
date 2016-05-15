package me.stammberger.galactictavern.ui.maps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.stores.StarmapStore;
import me.stammberger.starcitizencompact.map.GtStarMap;
import me.stammberger.starcitizencompact.map.data.StarMapData;

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
    private FrameLayout mStarMapView;
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
        mStarMapView = (FrameLayout) v.findViewById(R.id.starMapFrameLayout);
        mLoadingIndicator = v.findViewById(R.id.loadingIndicator);
        mGtStarMap = new GtStarMap(this);
        View view = initializeForView(mGtStarMap);
        mStarMapView.addView(view);

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
    public void onSystemSelected(String systemCode, int x, int y) {
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
                i.putExtra(SystemDetailSlidingActivity.SYSTEM_CODE, systemCode);
                startActivity(i);
            });
            TextView tv = (TextView) popView.findViewById(R.id.systemPopupNameDetailTextView);
            tv.setText(systemCode);
            mCurrentPopupWindow = new PopupWindow(
                    popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mCurrentPopupWindow.setAnimationStyle(R.style.AppTheme_PopupWindowAnimation);

            // offset the popupwindow based on whether the selected system
            // is positioned on the left or right side of the screen
            int offset = 200;
            int x2;
            if (x > 0) {
                x2 = x - offset;
            } else {
                x2 = x + offset;
            }

            mCurrentPopupWindow.setFocusable(false);
            mCurrentPopupWindow.setOutsideTouchable(false);
            mCurrentPopupWindow.showAtLocation(getView(), Gravity.CENTER, x2, y);
        });
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
