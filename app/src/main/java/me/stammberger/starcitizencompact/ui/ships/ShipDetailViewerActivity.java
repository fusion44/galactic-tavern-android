package me.stammberger.starcitizencompact.ui.ships;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.core.Utility;
import me.stammberger.starcitizencompact.models.ship.Ship;
import me.stammberger.starcitizencompact.models.ship.ShipData;
import me.stammberger.starcitizencompact.stores.ShipStore;

/**
 * This class displays all the available data for a ship in an card interface
 */
public class ShipDetailViewerActivity extends AppCompatActivity implements RequestListener<String, GlideDrawable> {
    public static final String SHIP_ITEM = "ship_item";
    private Ship mShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_detail_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        String id = getIntent().getStringExtra(SHIP_ITEM);
        mShip = ShipStore.get(SciApplication.getInstance().getRxFlux().getDispatcher()).getShipById(id);

        ImageView backdropView = (ImageView) findViewById(R.id.activityShipDetailViewerBackdrop);
        Glide.with(this)
                .load(Utility.RSI_BASE_URL + mShip.shipimgsrc)
                .listener(this)
                .into(backdropView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /**
             * See {@link CommLinkReaderActivity} for a description
             */
            supportPostponeEnterTransition();
            backdropView.setTransitionName(mShip.shipimgsmall);
        }

        setupTitleCard();
        setupMeasurementCard();
        setupStructuralCard();
        setupPropulsionCard();
        setupHardpointsCard();
        setupModularCard();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Fills the title card information displays
     */
    private void setupTitleCard() {
        TextView tv = (TextView) findViewById(R.id.shipDetailCardTitleTextView);
        tv.setText(mShip.titlecontainer.title);

        tv = (TextView) findViewById(R.id.shipDetailCardSubtitleTextView);
        tv.setText(getString(R.string.subtitle_by_manufacturer,
                Utility.getFullManufacturerName(this, mShip.titlecontainer.manufacturer)));

        tv = (TextView) findViewById(R.id.shipDetailCardDescriptionTextView);
        tv.setText(Html.fromHtml(mShip.titlecontainer.description));

        tv = (TextView) findViewById(R.id.shipDetailCardProductionStateTextView);
        tv.setText(mShip.titlecontainer.production);

        tv = (TextView) findViewById(R.id.shipDetailCardRoleTextView);
        tv.setText(mShip.titlecontainer.role);
    }

    /**
     * Fills the measurement card information displays
     */
    private void setupMeasurementCard() {
        TextView tv = (TextView) findViewById(R.id.shipDetailCardBeamTextView);
        tv.setText(mShip.measurementcontainer.beam);

        tv = (TextView) findViewById(R.id.shipDetailCardHeightTextView);
        tv.setText(mShip.measurementcontainer.height);

        tv = (TextView) findViewById(R.id.shipDetailCardLengthTextView);
        tv.setText(mShip.measurementcontainer.length);

        tv = (TextView) findViewById(R.id.shipDetailCardMassTextView);
        tv.setText(mShip.measurementcontainer.mass);
    }

    /**
     * Fills the structural card information display
     */
    private void setupStructuralCard() {
        TextView tv = (TextView) findViewById(R.id.shipDetailCardCargoCapacityTextView);
        tv.setText(mShip.structuralcontainer.cargocapacity);

        tv = (TextView) findViewById(R.id.shipDetailCardMaxCrewTextView);
        tv.setText(mShip.structuralcontainer.maxcrew);

        tv = (TextView) findViewById(R.id.shipDetailCardMaxPowerPlantSizeTextView);
        tv.setText(mShip.structuralcontainer.maxpowerplant);

        tv = (TextView) findViewById(R.id.shipDetailCardFactoryPowerPlantTextView);
        tv.setText(mShip.structuralcontainer.factorypowerplant);

        tv = (TextView) findViewById(R.id.shipDetailCardPrimaryThrustersTextView);
        tv.setText(mShip.structuralcontainer.maxprimarythruster);

        tv = (TextView) findViewById(R.id.shipDetailCardFactoryEngineTextView);
        tv.setText(mShip.structuralcontainer.factorythruster);

        tv = (TextView) findViewById(R.id.shipDetailCardManeuveringThrustersTextView);
        tv.setText(mShip.structuralcontainer.maneuveringthrusters);

        tv = (TextView) findViewById(R.id.shipDetailCardFactoryManeuveringThrustersTextView);
        tv.setText(mShip.structuralcontainer.factorymaneuveringthrusters);

        tv = (TextView) findViewById(R.id.shipDetailCardFactoryMaxShieldTextView);
        tv.setText(mShip.structuralcontainer.maxshield);

        tv = (TextView) findViewById(R.id.shipDetailCardFactoryShieldTextView);
        tv.setText(mShip.structuralcontainer.shield);
    }

    /**
     * Fills the propulsion card information display
     */
    @SuppressLint("InflateParams")
    private void setupPropulsionCard() {
        LinearLayout root = (LinearLayout) findViewById(R.id.shipDetailCardPropulsionContentLinearLayout);
        ShipData allShips = ShipStore.get(SciApplication.getInstance().getRxFlux().getDispatcher())
                .getAllShips();

        for (int i = 0; i < mShip.propulsionscontainer.additionals.size(); i++) {
            String left = allShips.statboxHeaders.propulsionscontainerheader.additionals.get(i);
            String right = mShip.propulsionscontainer.additionals.get(i);

            View v = getLayoutInflater().inflate(R.layout.activity_ship_detail_card_row_item, null);

            // left row item is the item description, i.e. "Maneuvering"
            TextView tv = (TextView) v.findViewById(R.id.shipDetailCardLeftRowItem);
            tv.setText(left);

            tv = (TextView) v.findViewById(R.id.shipDetailCardRightRowItem);
            tv.setText(right);

            root.addView(v);
        }
    }

    /**
     * Fills the structural card information display
     */
    private void setupHardpointsCard() {
        TextView tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC1TextView);
        tv.setText(mShip.hardpointscontainer.hpC1);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC2TextView);
        tv.setText(mShip.hardpointscontainer.hpC2);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC3TextView);
        tv.setText(mShip.hardpointscontainer.hpC3);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC4TextView);
        tv.setText(mShip.hardpointscontainer.hpC4);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC5TextView);
        tv.setText(mShip.hardpointscontainer.hpC5);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC6TextView);
        tv.setText(mShip.hardpointscontainer.hpC6);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC7TextView);
        tv.setText(mShip.hardpointscontainer.hpC7);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC8TextView);
        tv.setText(mShip.hardpointscontainer.hpC8);

        tv = (TextView) findViewById(R.id.shipDetailCardHardpointsC9TextView);
        tv.setText(mShip.hardpointscontainer.hpC9);
    }

    /**
     * Fills the additional card information display
     */
    @SuppressLint("InflateParams")
    private void setupModularCard() {
        LinearLayout root = (LinearLayout) findViewById(R.id.shipDetailCardModularContentLinearLayout);
        ShipData allShips = ShipStore.get(SciApplication.getInstance().getRxFlux().getDispatcher())
                .getAllShips();

        for (int i = 0; i < mShip.modularcontainer.additionals.size(); i++) {
            String left = allShips.statboxHeaders.modularcontainerheader.additionals.get(i);
            String right = mShip.modularcontainer.additionals.get(i);

            View v = getLayoutInflater().inflate(R.layout.activity_ship_detail_card_row_item, null);

            TextView tv = (TextView) v.findViewById(R.id.shipDetailCardLeftRowItem);
            tv.setText(left);

            tv = (TextView) v.findViewById(R.id.shipDetailCardRightRowItem);
            tv.setText(right);

            root.addView(v);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportStartPostponedEnterTransition();
        }

        return false;
    }
}
