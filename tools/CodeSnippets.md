##### How to test GSON:
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
