package me.stammberger.galactictavern.core.retrofit;

import me.stammberger.galactictavern.BuildConfig;
import me.stammberger.galactictavern.models.commlink.CommLinkModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Single;

/**
 * Retrofit interface for getting the custom comm link content
 */
public interface CommLinkApiService {
    //String BASE_URL = "https://galactictavern.appspot.com";
    //String BASE_URL = "http://192.168.178.95:8080/";
    String BASE_URL = "http://10.0.2.2:8080/";

    /**
     * Gets the content from the repository
     *
     * @param secret       the API secret
     * @param lastCommLink of the last loaded comm link. Zero if none where loaded yet.
     *                     Used for pagination. API will load comm links older than this one.
     * @param maxResults   Max results returned
     * @return Observable which will be called once done loading
     */
    @GET("/v1/get_comm_links")
    Observable<CommLinkModel[]> getCommLinks(
            @Query("secret") String secret,
            @Query("last_comm_link") long lastCommLink,
            @Query("max_results") int maxResults);

    /**
     * Get a single comm link via ID
     *
     * @param secret the API secret
     * @param id     of the comm link
     * @return a Single with the {@link CommLinkModel}
     */
    @GET("/v1/get_comm_link")
    Single<CommLinkModel> getCommLink(
            @Query("secret") String secret,
            @Query("id") long id);

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

        public static synchronized CommLinkApiService getInstanceWithFullLogging() {
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

                    mLoggingService = retrofit.create(CommLinkApiService.class);
                }
            } else {
                return getInstance();
            }

            return mLoggingService;
        }
    }
}