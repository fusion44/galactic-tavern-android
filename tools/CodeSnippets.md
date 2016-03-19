# Code Snippets
Useful code snippets are kept here for future reference.

#### How to test GSON:
Useful if Retrofit has problems with GSON. It's easier to debug the problem this way.
``` java ```

    run("http://fusion44.bitbucket.org/sci/ships/ships_all.json");
    void run(String url) {
        OkHttpClient c = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        c.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                ShipData d = gson.fromJson(response.body().string(), ShipData.class);
                Timber.d(d.toString());
            }
        });
    }

#### How to draw a divider via xml
Add this to a layout:
``` xml ```

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_marginBottom="@dimen/divider_margin_vertical"
        android:layout_marginTop="@dimen/divider_margin_vertical"
        android:background="@color/divider" />

#### Retrofit API Service with Logging
User something like this:
``` java ```

        /**
         * Get the service instance with logging enabled.
         *
         * @return the api service with logging enabled if BuildConfig.DEBUG == true
         */
        public static synchronized ShipApiService getInstanceWithFullLogging() {
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

                    mLoggingService = retrofit.create(ShipApiService.class);
                }
            } else {
                return getInstance();
            }

            return mLoggingService;
        }
    }