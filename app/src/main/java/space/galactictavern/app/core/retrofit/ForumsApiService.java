package space.galactictavern.app.core.retrofit;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import space.galactictavern.app.models.forums.ForumThread;
import space.galactictavern.app.models.forums.ForumThreadPostData;
import space.galactictavern.app.models.forums.ForumThreadPosts;
import space.galactictavern.app.models.forums.ForumThreads;
import space.galactictavern.app.models.forums.Forums;

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
     * Gets all {@link ForumThreadPosts} for the specified {@link ForumThread} id and data page.
     * <p>
     * Note: Currently the API's pagination feature is broken.
     * Start page and end page must be the same value!
     *
     * @param threadId  The ID of the Thread
     * @param startPage Start page of data
     * @param endPage   End page of data
     * @return Observable with the post data
     */
    @GET("?api_source=live&system=forums&action=posts&expedite=0&format=json")
    Observable<ForumThreadPosts> getPosts(
            @Query("target_id") long threadId,
            @Query("start_page") int startPage,
            @Query("end_page") int endPage);

    /**
     * Factory class to create and store the singleton
     */
    class Factory {
        private static ForumsApiService mService;

        /**
         * Get the service instance without logging enabled
         *
         * @return the api service without logging
         */
        public static synchronized ForumsApiService getInstance() {
            if (mService == null) {
                GsonBuilder b = new GsonBuilder();
                b.registerTypeAdapter(ForumThreadPostData.class,
                        (JsonDeserializer<ForumThreadPostData>) (json, typeOfT, context) -> {
                            ForumThreadPostData data = new ForumThreadPostData();
                            JsonObject o = json.getAsJsonObject();
                            data.postText = o.get("post_text").getAsString();
                            try {
                                data.postTime = o.get("post_time").getAsLong();
                            } catch (NumberFormatException e) {
                                data.postTime = 0L;
                            }
                            return data;
                        });

                Retrofit retrofit =
                        new Retrofit.Builder()
                                .addConverterFactory(GsonConverterFactory.create(b.create()))
                                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                .baseUrl(BASE_URL)
                                .build();
                mService = retrofit.create(ForumsApiService.class);

            }
            return mService;
        }
    }
}
