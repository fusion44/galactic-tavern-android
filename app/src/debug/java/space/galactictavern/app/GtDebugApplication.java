package space.galactictavern.app;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

public class GtDebugApplication extends GtApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        LeakCanary.install(this);
    }
}
