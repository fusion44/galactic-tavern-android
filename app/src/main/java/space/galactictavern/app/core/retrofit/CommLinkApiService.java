package space.galactictavern.app.core.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import space.galactictavern.app.models.commlink.CommLinkModel;

/**
 * Retrofit interface for getting the custom comm link content
 */
public interface CommLinkApiService {
    String BASE_URL = "https://galactictavern.appspot.com";
    //String BASE_URL = "http://192.168.178.95:8080/";
    //String BASE_URL = "http://10.0.2.2:8080/"; // connect from emulator to server on host machine

    /**
     * Gets the content from the repository
     *
     * @param secret                the API secret
     * @param lastCommLinkPublished Published time of the last received comm link
     *                              Used for pagination. API will load comm links older than this one.
     * @param maxResults            Max results returned
     * @return Observable which will be called once done loading
     */
    @GET("/v1/get_comm_links")
    Observable<CommLinkModel[]> getCommLinks(
            @Query("secret") String secret,
            @Query("last_comm_link") long lastCommLinkPublished,
            @Query("max_results") int maxResults);

    /**
     * Get a single comm link via ID
     *
     * @param secret the API secret
     * @param id     of the comm link
     * @return a Single with the {@link CommLinkModel}
     */
    @GET("/v1/get_comm_link")
    Observable<CommLinkModel> getCommLink(
            @Query("secret") String secret,
            @Query("id") long id);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static CommLinkApiService mService;

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
    }
}