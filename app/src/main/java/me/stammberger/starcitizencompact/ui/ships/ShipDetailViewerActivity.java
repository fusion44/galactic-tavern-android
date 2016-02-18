package me.stammberger.starcitizencompact.ui.ships;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;

import me.stammberger.starcitizencompact.R;
import me.stammberger.starcitizencompact.SciApplication;
import me.stammberger.starcitizencompact.core.Utility;
import me.stammberger.starcitizencompact.models.ship.Ship;
import me.stammberger.starcitizencompact.stores.ShipStore;

/**
 * This class displays all the available data for a ship in an card interface
 */
public class ShipDetailViewerActivity extends AppCompatActivity {
    public static final String SHIP_ITEM = "ship_item";
    private Ship mShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_detail_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String id = getIntent().getStringExtra(SHIP_ITEM);
        mShip = ShipStore.get(SciApplication.getInstance().getRxFlux().getDispatcher()).getShipById(id);

        setupTitleCard();
        setupMeasurementCard();

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
}
