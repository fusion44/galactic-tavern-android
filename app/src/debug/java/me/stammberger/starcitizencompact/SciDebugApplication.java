package me.stammberger.starcitizencompact;

import com.facebook.stetho.Stetho;

public class SciDebugApplication extends SciApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}