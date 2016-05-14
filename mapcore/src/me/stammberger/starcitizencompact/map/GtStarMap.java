package me.stammberger.starcitizencompact.map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;

import me.stammberger.starcitizencompact.map.data.StarMapData;
import me.stammberger.starcitizencompact.map.data.SystemsResultset;
import me.stammberger.starcitizencompact.map.data.Thumbnail;
import me.stammberger.starcitizencompact.map.screens.BaseScreen;
import me.stammberger.starcitizencompact.map.screens.MainScreen;

public class GtStarMap extends Game implements GestureDetector.GestureListener {
    public static StarMapData mapData;
    public static Vector2 origin;
    BaseScreen mCurrentScreen;
    GestureDetector mGestureDetector;
    StatusCallback mStatusCallback;
    SystemSelectedCallback mSystemSelectedCallback;

    public GtStarMap(StatusCallback callback, SystemSelectedCallback systemSelectedCallback) {
        super();

        if (callback == null || systemSelectedCallback == null) {
            throw new NullPointerException("Callback must not be null");
        }

        mStatusCallback = callback;
        mSystemSelectedCallback = systemSelectedCallback;
    }

    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }
        return out.toString();
    }

    @Override
    public void create() {
        mStatusCallback.onStartedLoading();
        mCurrentScreen = new MainScreen(mSystemSelectedCallback);
        setScreen(mCurrentScreen);

        try {
            InputStream connection =
                    new URL("http://fusion44.bitbucket.org/sci/starmap/bootup.json").openStream();
            String json = fromStream(connection);
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Thumbnail.class, new ThumbnailsDeserializer());
            Gson gson = builder.create();
            mapData = gson.fromJson(json, StarMapData.class);

            origin = new Vector2();
            for (SystemsResultset s : mapData.data.systems.resultset) {
                origin.add(s.positionX, s.positionY);
            }

            origin.x = origin.x / mapData.data.systems.rowcount;
            origin.y = origin.y / mapData.data.systems.rowcount;

            Vector2 dist = new Vector2();
            for (SystemsResultset s : mapData.data.systems.resultset) {
                dist.x = s.positionX - origin.x;
                dist.y = s.positionY - origin.y;

                dist.scl(80);

                s.positionX = dist.x + origin.x;
                s.positionY = dist.y + origin.y;

                mapData.data.systemHashMap.put(s.id, s);

                s.generateBoundingCircle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        mGestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(mGestureDetector);

        mStatusCallback.onFinishedLoading();
    }

    @Override
    public void render() {
        super.render();

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return mCurrentScreen.touchDown(x, y, pointer, button);
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return mCurrentScreen.tap(x, y, count, button);
    }

    @Override
    public boolean longPress(float x, float y) {
        return mCurrentScreen.longPress(x, y);
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return mCurrentScreen.fling(velocityX, velocityY, button);
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return mCurrentScreen.pan(x, y, deltaX, deltaY);
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return mCurrentScreen.panStop(x, y, pointer, button);
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return mCurrentScreen.zoom(initialDistance, distance);
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return mCurrentScreen.pinch(initialPointer1, initialPointer2, pointer1, pointer2);
    }

    public interface StatusCallback {
        void onStartedLoading();

        void onFinishedLoading();

        void onError(String error);
    }

    public interface SystemSelectedCallback {
        void onSystemSelected(SystemsResultset s);
    }

    class ThumbnailsDeserializer implements JsonDeserializer<Thumbnail> {
        @Override
        public Thumbnail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            // TODO: handle gracefully
            return new Thumbnail();
        }
    }
}
