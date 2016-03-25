package me.stammberger.galactictavern.ui.ships;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.galactictavern.GtApplication;
import me.stammberger.galactictavern.R;
import me.stammberger.galactictavern.core.Utility;
import me.stammberger.galactictavern.stores.ShipStore;


public class ShipFilterDialog extends DialogFragment implements View.OnClickListener {
    public static final String KEY_FILTER = "key_filter";

    private static final String ITEM_AVAILABLE = "availableItem";
    private static final String ITEM_FILTER = "filterItem";

    private ShipFilterDialogClickListener mListener;
    private FlowLayout mFlowLayoutFilterItems;
    private FlowLayout mFlowLayoutAvailableItems;
    private TextView mNoneView;

    public static ShipFilterDialog newInstance(ShipFilterDialogClickListener listener,
                                               ArrayList<String> filter) {
        ShipFilterDialog frag = new ShipFilterDialog();
        Bundle b = new Bundle();
        b.putStringArrayList(KEY_FILTER, filter);
        frag.setArguments(b);
        frag.setListener(listener);
        return frag;
    }

    public void setListener(ShipFilterDialogClickListener listener) {
        mListener = listener;
    }

    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_ship_list_filter_dlg, null);

        ArrayList<String> currentFilter = getArguments().getStringArrayList(KEY_FILTER);

        mFlowLayoutFilterItems =
                (FlowLayout) view.findViewById(R.id.shipFilterFlowLayoutFilterItems);
        mFlowLayoutAvailableItems =
                (FlowLayout) view.findViewById(R.id.shipFilterFlowLayoutAvailableItems);

        mNoneView = (TextView) inflater.inflate(R.layout.fragment_ship_list_filter_none, null);
        mNoneView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        ShipStore shipStore =
                ShipStore.get(GtApplication.getInstance().getRxFlux().getDispatcher());

        Button btn;
        setupFavoriteFilterBtn(inflater, currentFilter);
        for (String manufacturer : shipStore.getAllShips().manufacturers) {
            btn = inflateFilterButton(inflater, manufacturer);

            if (currentFilter != null && currentFilter.contains(manufacturer)) {
                btn.setTag(R.id.ship_list_filter_type_tag, ITEM_FILTER);
                mFlowLayoutFilterItems.addView(btn);
            } else {
                btn.setTag(R.id.ship_list_filter_type_tag, ITEM_AVAILABLE);
                mFlowLayoutAvailableItems.addView(btn);
            }
        }

        checkNoneView();

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_ship_list_filter)
                .setIcon(R.drawable.ic_filter_white_24dp)
                .setView(view)
                .setPositiveButton(R.string.alert_dialog_ok,
                        (dialog, whichButton) -> {
                            ArrayList<String> list = new ArrayList<>();
                            for (View v : Utility.getViewsByTag(mFlowLayoutFilterItems,
                                    R.id.ship_list_filter_type_tag,
                                    ITEM_FILTER)) {
                                Button button = (Button) v;
                                list.add((String) button.getTag(R.id.ship_list_filter_manufacturer_tag));
                            }
                            mListener.doPositiveClick(list);
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        (dialog, whichButton) -> {
                            mListener.doNegativeClick();
                        }
                );

        return b.create();
    }

    /**
     * Inflate the Favorite button. This button is different from the other buttons which are
     * for a single manufacturer each. The fav button possibly covers multiple manufacturers
     * und thus must be treated differently when creating the list.
     *
     * @param inflater      The Android LayoutInflater instance
     * @param currentFilter The current filter to check whether the Favorite filter is currently set
     */
    private void setupFavoriteFilterBtn(LayoutInflater inflater, ArrayList<String> currentFilter) {
        Button btn = inflateFilterButton(inflater, getString(R.string.ship_filter_dialog_fav_button));

        if (currentFilter != null &&
                currentFilter.contains(getString(R.string.ship_filter_dialog_fav_button))) {
            btn.setTag(R.id.ship_list_filter_type_tag, ITEM_FILTER);
            mFlowLayoutFilterItems.addView(btn);
        } else {
            btn.setTag(R.id.ship_list_filter_type_tag, ITEM_AVAILABLE);
            mFlowLayoutAvailableItems.addView(btn);
        }
    }

    /**
     * Shortcut for creating a filter button
     *
     * @param inflater     The Android LayoutInflater instance
     * @param manufacturer Manufacturer name for the Button. This is also buttons display text
     * @return The Button
     */
    @NonNull
    private Button inflateFilterButton(LayoutInflater inflater, String manufacturer) {
        Button btn = (Button) inflater.inflate(R.layout.fragment_ship_list_filter_item, null);
        btn.setText(manufacturer);
        btn.setTag(R.id.ship_list_filter_manufacturer_tag, manufacturer);
        btn.setOnClickListener(this);
        return btn;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag(R.id.ship_list_filter_type_tag).equals(ITEM_AVAILABLE)) {
            mFlowLayoutAvailableItems.removeView(v);
            mFlowLayoutFilterItems.addView(v);
            v.setTag(R.id.ship_list_filter_type_tag, ITEM_FILTER);
        } else {
            mFlowLayoutFilterItems.removeView(v);
            mFlowLayoutAvailableItems.addView(v);
            v.setTag(R.id.ship_list_filter_type_tag, ITEM_AVAILABLE);
        }

        checkNoneView();
    }

    /**
     * Checks if one of the lists is empty and thus an empty view must be displayed
     */
    private void checkNoneView() {
        if (Utility.getViewsByTag(mFlowLayoutAvailableItems,
                R.id.ship_list_filter_type_tag, ITEM_AVAILABLE).size() == 0) {
            mFlowLayoutAvailableItems.addView(mNoneView);
        } else {
            if (mNoneView.getParent() == mFlowLayoutAvailableItems) {
                ((FlowLayout) mNoneView.getParent()).removeView(mNoneView);
            }
        }

        if (Utility.getViewsByTag(mFlowLayoutFilterItems,
                R.id.ship_list_filter_type_tag, ITEM_FILTER).size() == 0) {
            mFlowLayoutFilterItems.addView(mNoneView);
        } else {
            if (mNoneView.getParent() == mFlowLayoutFilterItems) {
                ((FlowLayout) mNoneView.getParent()).removeView(mNoneView);
            }
        }
    }

    public interface ShipFilterDialogClickListener {
        void doPositiveClick(List<String> filter);

        void doNegativeClick();
    }
}
