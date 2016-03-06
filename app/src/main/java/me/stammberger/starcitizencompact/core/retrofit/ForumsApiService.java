package me.stammberger.starcitizencompact.core.retrofit;

import me.stammberger.starcitizencompact.BuildConfig;
import me.stammberger.starcitizencompact.models.forums.ForumThreads;
import me.stammberger.starcitizencompact.models.forums.Forums;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ForumsApiService {
    String BASE_URL = "http://sc-api.com/";

    /**
     * Gets all Forums
     *
     * @return Observable which will be called once done loading
     */
    @GET("?api_source=live&system=forums&action=forums&expedite=0&format=json\n")
    Observable<Forums> getForums();

    /**
     * Gets all threads for the specified Forum and data page.
     * <p>
     * Note: Currently the API's pagination feature is broken.
     * Start page and end page must be the same value!
     *
     * @param forumId   The ID of the Forum
     * @param startPage Start page of forum data
     * @param endPage   End page of forum data
     * @return Observable for the forum thread data
     */
    @GET("?api_source=live&system=forums&action=threads&expedite=0&format=json")
    Observable<ForumThreads> getTreads(
            @Query("target_id") String forumId,
            @Query("start_page") int startPage,
            @Query("end_page") int endPage);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static ForumsApiService mService;
        private static ForumsApiService mLoggingService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized ForumsApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(ForumsApiService.class);

            }
            return mService;
        }

        /**
         * Get the service instance with logging enabled.
         * Logging is enabled in DEBUG builds only
         *
         * @return the api service with logging enabled if BuildConfig.DEBUG == true
         */
        public static synchronized ForumsApiService getInstanceWithFullLogging() {
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

                    mLoggingService = retrofit.create(ForumsApiService.class);
                }
            } else {
                return getInstance();
            }

            return mLoggingService;
        }
    }
}
