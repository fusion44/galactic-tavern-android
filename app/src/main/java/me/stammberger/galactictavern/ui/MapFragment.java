package me.stammberger.galactictavern.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import me.stammberger.galactictavern.R;
import me.stammberger.starcitizencompact.map.GtStarMap;
import timber.log.Timber;

/**
 * Fragment for displaying starmap data.
 * <p>
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends AndroidFragmentApplication {
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
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        FrameLayout starMapView = (FrameLayout) v.findViewById(R.id.starMapFrameLayout);
        GtStarMap gtStarMap = new GtStarMap(new GtStarMap.StatusCallback() {
            @Override
            public void onStartedLoading() {
                Timber.d("started loading");
            }

            @Override
            public void onFinishedLoading() {
                Timber.d("finished loading");
            }

            @Override
            public void onError(String error) {

            }
        });
        View view = initializeForView(gtStarMap);
        starMapView.addView(view);
        return v;
    }
}
