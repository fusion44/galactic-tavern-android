package me.stammberger.starcitizeninformer.ui.commlinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import me.stammberger.starcitizeninformer.R;
import me.stammberger.starcitizeninformer.models.CommLinkModel;

/**
 * This Activity will display {@link CommLinkModel.CommLinkContentPart} in an RecyclerView
 * with appropriate styling to make it visually pleasing and easy to read
 */
public class CommLinkReaderActivity extends AppCompatActivity {
    public static final String COMM_LINK_ITEM = "comm_link_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_link_reader_native);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CommLinkModel commLink = getIntent().getParcelableExtra(COMM_LINK_ITEM);
        CommLinkReaderAdapter cmra = new CommLinkReaderAdapter(commLink);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commLinkContentRecyclerView);
        recyclerView.setAdapter(cmra);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        cmra.notifyDataSetChanged();
    }
}
