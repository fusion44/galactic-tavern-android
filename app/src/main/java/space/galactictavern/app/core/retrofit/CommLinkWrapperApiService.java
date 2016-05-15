package space.galactictavern.app.core.retrofit;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import space.galactictavern.app.models.commlink.CommLinkModel;
import space.galactictavern.app.models.commlink.Wrapper;

/**
 * Retrofit interface for getting the custom comm link content
 */
public interface CommLinkWrapperApiService {
    String BASE_URL = "https://galactic-tavern.firebaseapp.com/";
    //String BASE_URL = "http://192.168.178.95:8000/";

    /**
     * Gets the wrappers for the {@link CommLinkModel}
     *
     * @param id of the comm link
     * @return Observable which will be called once done loading
     */
    @GET("/v1/sci/comm-links/{id}.json")
    Observable<List<Wrapper>> getCommLinkWrappers(@Path("id") Long id);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static CommLinkWrapperApiService mService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized CommLinkWrapperApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(CommLinkWrapperApiService.class);

            }
            return mService;
        }
    }
}