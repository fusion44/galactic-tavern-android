package me.stammberger.starcitizeninformer.ui.ships;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.SciApplication;
import me.stammberger.starcitizeninformer.core.Utility;
import me.stammberger.starcitizeninformer.stores.ShipStore;


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
                ShipStore.get(SciApplication.getInstance().getRxFlux().getDispatcher());

        for (String manufacturer : shipStore.getAllShips().manufacturers) {
            Button btn = (Button) inflater.inflate(R.layout.fragment_ship_list_filter_item, null);
            btn.setTag(ITEM_AVAILABLE);
            btn.setText(manufacturer);
            btn.setOnClickListener(this);

            if (currentFilter != null && currentFilter.contains(manufacturer)) {
                btn.setTag(ITEM_FILTER);
                mFlowLayoutFilterItems.addView(btn);
            } else {
                btn.setTag(ITEM_AVAILABLE);
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
                            for (View v : Utility.getViewsByTag(mFlowLayoutFilterItems, ITEM_FILTER)) {
                                Button button = (Button) v;
                                list.add(button.getText().toString());
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

    @Override
    public void onClick(View v) {
        if (v.getTag().equals(ITEM_AVAILABLE)) {
            mFlowLayoutAvailableItems.removeView(v);
            mFlowLayoutFilterItems.addView(v);
            v.setTag(ITEM_FILTER);

        } else {
            mFlowLayoutFilterItems.removeView(v);
            mFlowLayoutAvailableItems.addView(v);
            v.setTag(ITEM_AVAILABLE);
        }

        checkNoneView();
    }

    private void checkNoneView() {
        if (mFlowLayoutAvailableItems.findViewWithTag(ITEM_AVAILABLE) == null) {
            mFlowLayoutAvailableItems.addView(mNoneView);
        } else {
            if (mNoneView.getParent() == mFlowLayoutAvailableItems) {
                ((FlowLayout) mNoneView.getParent()).removeView(mNoneView);
            }
        }

        if (mFlowLayoutFilterItems.findViewWithTag(ITEM_FILTER) == null) {
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
