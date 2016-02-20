package me.stammberger.starcitizencompact.core.retrofit;

import me.stammberger.starcitizencompact.BuildConfig;
import me.stammberger.starcitizencompact.models.user.User;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Retrofit interface for getting users from www.sc-api.com
 */
public interface UserApiService {
    String BASE_URL = "http://sc-api.com/";

    /**
     * Searches for a user by his handle
     *
     * @return Observable which will be called once done loading
     */
    @GET("/?api_source=cache&start_date=&end_date=&system=accounts&action=full_profile&format=json")
    Observable<User> getUser(@Query("target_id") String id);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static UserApiService mService;
        private static UserApiService mLoggingService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized UserApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(UserApiService.class);

            }
            return mService;
        }

        /**
         * Get the service instance with logging enabled.
         * Logging is enabled in DEBUG builds only
         *
         * @return the api service with logging enabled if BuildConfig.DEBUG == true
         */
        public static synchronized UserApiService getInstanceWithFullLogging() {
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

                    mLoggingService = retrofit.create(UserApiService.class);
                }
            } else {
                return getInstance();
            }

            return mLoggingService;
        }
    }
}
