package me.stammberger.galactictavern.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import me.stammberger.galactictavern.R;
import me.stammberger.starcitizencompact.map.GtStarMap;
import me.stammberger.starcitizencompact.map.data.SystemsResultset;
import timber.log.Timber;

/**
 * Fragment for displaying starmap data.
 * <p>
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends AndroidFragmentApplication implements GtStarMap.StatusCallback, GtStarMap.SystemSelectedCallback {
    public View mLoadingIndicator;

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
        GtStarMap gtStarMap = new GtStarMap(this, this);
        View view = initializeForView(gtStarMap);
        starMapView.addView(view);
        return v;
    }

    @Override
    public void onStartedLoading() {
    }

    @Override
    public void onFinishedLoading() {
        Handler mainHandler = new Handler(getActivity().getMainLooper());
        Runnable myRunnable = () -> mLoadingIndicator.setVisibility(View.GONE);
        mainHandler.post(myRunnable);
    }

    @Override
    public void onError(String error) {
    }

    @Override
    public void onSystemSelected(SystemsResultset s) {
        Timber.d("Selected system %s", s.code);
    }
}
