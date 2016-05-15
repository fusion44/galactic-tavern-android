package me.stammberger.galactictavern.core.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import me.stammberger.starcitizencompact.map.data.StarMapData;
import me.stammberger.starcitizencompact.map.data.Thumbnail;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Retrofit interface for getting data related to the star map
 */
public interface StarMapService {
    String BASE_URL = "http://fusion44.bitbucket.org";

    /**
     * Gets the base starmap boot-up data
     *
     * @return Observable which will be called once done loading
     */
    @GET("/sci/starmap/bootup.json")
    Observable<StarMapData> getBootupData();

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static StarMapService mService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized StarMapService getInstance() {
            if (mService == null) {
                GsonBuilder builder = new GsonBuilder();
                builder.registerTypeAdapter(Thumbnail.class, new ThumbnailsDeserializer());
                Gson gson = builder.create();

                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(StarMapService.class);

            }
            return mService;
        }
    }

    class ThumbnailsDeserializer implements JsonDeserializer<Thumbnail> {
        @Override
        public Thumbnail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            // TODO: handle gracefully. For example add a default image.
            return new Thumbnail();
        }
    }
}
