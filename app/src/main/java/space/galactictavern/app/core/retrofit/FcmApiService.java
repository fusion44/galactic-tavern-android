package space.galactictavern.app.core.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import space.galactictavern.app.models.common.StandardResponse;

/**
 * Retrofit interface for managing this devices FCM subscription
 */
public interface FcmApiService {
    String BASE_URL = "https://galactictavern.appspot.com";

    /**
     * Registers the device with the service
     *
     * @param secret the API secret
     * @param token  Registration token of the device
     * @return Observable which will be called once done loading
     */
    @POST("/v1/fcm_backend/register_device")
    Observable<StandardResponse> registerDevice(
            @Query("secret") String secret,
            @Query("token") String token,
            @Query("device_info") String info);

    /**
     * Unregisters the device with the service
     *
     * @param secret the API secret
     * @param token  Registration token of the device
     * @return Observable which will be called once done loading
     */
    @POST("/v1/fcm_backend/unregister_device")
    Observable<String> unregisterDevice(
            @Query("secret") String secret,
            @Query("token") String token);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static FcmApiService mService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized FcmApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(FcmApiService.class);

            }
            return mService;
        }
    }
}