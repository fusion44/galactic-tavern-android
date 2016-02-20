package me.stammberger.starcitizencompact.core.retrofit;

import me.stammberger.starcitizencompact.BuildConfig;
import me.stammberger.starcitizencompact.models.ship.ShipData;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Retrofit interface for getting ships from the API
 */
public interface ShipApiService {
    String BASE_URL = "http://fusion44.bitbucket.org/";

    /**
     * Gets all available ships
     *
     * @return Observable which will be called once done loading
     */
    @GET("/sci/ships/ships_all.json")
    Observable<ShipData> getShips();

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static ShipApiService mService;
        private static ShipApiService mLoggingService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized ShipApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(ShipApiService.class);

            }
            return mService;
        }

        /**
         * Get the service instance with logging enabled.
         * Logging is enabled in DEBUG builds only
         *
         * @return the api service with logging enabled if BuildConfig.DEBUG == true
         */
        public static synchronized ShipApiService getInstanceWithFullLogging() {
            if (BuildConfig.DEBUG) {
                if (mLoggingService == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient httpClient = new OkHttpClient().newBuilder().addInterceptor(logging).build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(httpClient)
                            .build();

                    mLoggingService = retrofit.create(ShipApiService.class);
                }
            } else {
                return getInstance();
            }

            return mLoggingService;
        }
    }
}
