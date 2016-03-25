package me.stammberger.galactictavern.core.retrofit;

import me.stammberger.galactictavern.models.ship.ShipData;
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
    }
}
