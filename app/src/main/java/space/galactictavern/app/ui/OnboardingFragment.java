package space.galactictavern.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixplicity.easyprefs.library.Prefs;

import butterknife.ButterKnife;
import butterknife.OnClick;
import space.galactictavern.app.GtApplication;
import space.galactictavern.app.R;

/**
 * Very simple fragment to ask the user for statistics collection.
 * This will be shown at first run.
 */
public class OnboardingFragment extends Fragment {
    public OnboardingFragment() {
        // Required empty public constructor
    }

    public static OnboardingFragment newInstance() {
        return new OnboardingFragment();
    }

    @OnClick(R.id.okButton)
    public void okButtonClick() {
        GtApplication.getInstance().setTrackingEnabled(true);
        Prefs.putBoolean(getString(R.string.pref_key_tracking), true);
        ((MainActivity) getActivity()).onboardingFinished();
    }

    @OnClick(R.id.noButton)
    public void noButtonClick() {
        GtApplication.getInstance().setTrackingEnabled(false);
        Prefs.putBoolean(getString(R.string.pref_key_tracking), false);
        ((MainActivity) getActivity()).onboardingFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_onboarding, container, false);
        ButterKnife.bind(this, v);
        return v;
    }
}
