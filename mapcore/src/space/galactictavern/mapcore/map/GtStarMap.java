package space.galactictavern.mapcore.map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

import space.galactictavern.mapcore.map.data.StarMapData;
import space.galactictavern.mapcore.map.screens.MainScreen;

public class GtStarMap extends Game implements GestureDetector.GestureListener {
    MainScreen mCurrentScreen;
    GestureDetector mGestureDetector;
    SystemSelectedCallback mSystemSelectedCallback;
    private StarMapData mMapData = null;

    public GtStarMap(SystemSelectedCallback systemSelectedCallback) {
        super();

        if (systemSelectedCallback == null) {
            throw new NullPointerException("Callback must not be null");
        }

        mSystemSelectedCallback = systemSelectedCallback;
    }

    public void setBootupData(StarMapData data) {
        /**
         * {@link #create()} is called on LibDGX's thread and might be called later than
         * the boot up data is available. If this happens {@link mCurrentScreen} might be null
         * and the data needs to be temporarily saved until {@link #create()} is called.
         */
        if (mCurrentScreen != null) {
            mCurrentScreen.setMapData(data);
        } else {
            mMapData = data;
        }
    }

    @Override
    public void create() {
        mCurrentScreen = new MainScreen(mSystemSelectedCallback);
        if (mMapData != null) {
            mCurrentScreen.setMapData(mMapData);
            // don't keep unnecessary references
            mMapData = null;
        }
        setScreen(mCurrentScreen);

        mGestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(mGestureDetector);
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

    /**
     * Callback for when user selects a specific system
     */
    public interface SystemSelectedCallback {
        /**
         * User tapped on empty space without selecting anything
         *
         * @param x Touch position x in pixels
         * @param y Touch position y in pixels
         */
        void onTap(int x, int y);

        /**
         * Will be called on new selection
         *
         * @param systemId of the selected system
         * @param x        Touch position x in pixels
         * @param y        Touch position y in pixels
         */
        void onSystemSelected(int systemId, int x, int y);
    }
}
