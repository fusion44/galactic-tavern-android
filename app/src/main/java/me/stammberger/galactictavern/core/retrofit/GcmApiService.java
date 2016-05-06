package me.stammberger.galactictavern.core.retrofit;

import me.stammberger.galactictavern.models.common.StandardResponse;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Retrofit interface for managing this devices GCM subscription
 */
public interface GcmApiService {
    String BASE_URL = "https://galactictavern.appspot.com";

    /**
     * Registers the device with the service
     *
     * @param secret the API secret
     * @param token  Registration token of the device
     * @return Observable which will be called once done loading
     */
    @POST("/v1/gcm_backend/register_device")
    Observable<StandardResponse> registerDevice(
            @Query("secret") String secret,
            @Query("token") String token);

    /**
     * Unregisters the device with the service
     *
     * @param secret the API secret
     * @param token  Registration token of the device
     * @return Observable which will be called once done loading
     */
    @POST("/v1/gcm_backend/unregister_device")
    Observable<String> unregisterDevice(
            @Query("secret") String secret,
            @Query("token") String token);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static GcmApiService mService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized GcmApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(GcmApiService.class);

            }
            return mService;
        }
    }
}