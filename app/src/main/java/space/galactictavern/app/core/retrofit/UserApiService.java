package space.galactictavern.app.core.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import space.galactictavern.app.models.user.User;

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
    }
}
