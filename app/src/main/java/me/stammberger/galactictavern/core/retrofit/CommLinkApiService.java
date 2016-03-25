package me.stammberger.galactictavern.core.retrofit;

import me.stammberger.galactictavern.models.commlink.CommLinkModel;
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
    //String BASE_URL = "http://192.168.178.95:8000/";

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