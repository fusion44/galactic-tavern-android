package space.galactictavern.app.ui.orgs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import space.galactictavern.app.R;


public class OrgsFragment extends Fragment {


    public OrgsFragment() {
        // Required empty public constructor
    }

    public static OrgsFragment newInstance() {
        OrgsFragment fragment = new OrgsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.generic_adapter_empty_screen, container, false);
    }
}
