package me.stammberger.galactictavern.core.retrofit;

import me.stammberger.galactictavern.models.orgs.Organization;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Retrofit interface for getting organizations from www.sc-api.com
 */
public interface OrganizationApiService {
    String BASE_URL = "http://sc-api.com/";

    /**
     * Searches for an org by its id
     *
     * @return Observable which will be called once done loading
     */
    @GET("/?api_source=cache&start_date=&end_date=&system=organizations&action=single_organization&format=json")
    Observable<Organization> getOrganization(@Query("target_id") String id);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static OrganizationApiService mService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized OrganizationApiService getInstance() {
            if (mService == null) {
                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(OrganizationApiService.class);

            }
            return mService;
        }
    }
}
