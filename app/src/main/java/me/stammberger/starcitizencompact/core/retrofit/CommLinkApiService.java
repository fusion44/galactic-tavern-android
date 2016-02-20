package me.stammberger.starcitizencompact.core.retrofit;

import me.stammberger.starcitizencompact.BuildConfig;
import me.stammberger.starcitizencompact.models.commlink.CommLinkModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Retrofit interface for getting the custom comm link content
 */
public interface CommLinkApiService {
    String BASE_URL = "http://fusion44.bitbucket.org/";

    /**
     * Gets the content from the repository
     *
     * @param id of the comm link
     * @return Observable which will be called once done loading
     */
    @GET("/sci/comm-links/{id}.json")
    Observable<CommLinkModel> getCommLink(@Path("id") int id);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static CommLinkApiService mService;
        private static CommLinkApiService mLoggingService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized CommLinkApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(CommLinkApiService.class);

            }
            return mService;
        }

        /**
         * Get the service instance with logging enabled.
         * Logging is enabled in DEBUG builds only
         *
         * @return the api service with logging enabled if BuildConfig.DEBUG == true
         */
        public static synchronized CommLinkApiService getInstanceWithFullLogging() {
            if (BuildConfig.DEBUG) {
                if (mLoggingService == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient httpClient = new OkHttpClient().newBuilder().addInterceptor(logging).build();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://fusion44.bitbucket.org/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .client(httpClient)
                            .build();

                    mLoggingService = retrofit.create(CommLinkApiService.class);
                }
            } else {
                return getInstance();
            }

            return mLoggingService;
        }
    }
}