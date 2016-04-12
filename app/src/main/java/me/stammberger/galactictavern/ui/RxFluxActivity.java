package me.stammberger.galactictavern.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hardsoftstudio.rxflux.RxFlux;
import com.hardsoftstudio.rxflux.action.RxError;
import com.hardsoftstudio.rxflux.dispatcher.RxViewDispatch;
import com.hardsoftstudio.rxflux.store.RxStoreChange;

import me.stammberger.galactictavern.GtApplication;


@SuppressLint("Registered") // Never instantiated directly
public class RxFluxActivity extends AppCompatActivity implements RxViewDispatch {
    protected RxFlux mRxFlux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRxFlux = GtApplication.getInstance().getRxFlux();
        mRxFlux.onActivityCreated(this, getIntent().getExtras());
    }


    @Override
    protected void onStart() {
        mRxFlux.onActivityStarted(this);
        super.onStart();
    }

    @Override
    protected void onPause() {
        mRxFlux.onActivityPaused(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        mRxFlux.onActivityStopped(this);
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mRxFlux.onActivitySaveInstanceState(this, outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        mRxFlux.onActivityResumed(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mRxFlux.onActivityDestroyed(this);
        super.onDestroy();
    }

    @Override
    public void onRxStoreChanged(RxStoreChange change) {

    }

    @Override
    public void onRxError(RxError error) {

    }

    @Override
    public void onRxViewRegistered() {

    }

    @Override
    public void onRxViewUnRegistered() {

    }

    @Override
    public void onRxStoresRegister() {

    }
}
