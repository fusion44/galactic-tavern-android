package space.galactictavern.app;

import com.facebook.stetho.Stetho;

public class GtDebugApplication extends GtApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
