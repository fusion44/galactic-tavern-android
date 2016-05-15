package me.stammberger.starcitizencompact.map.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rahul.libgdx.parallax.ParallaxBackground;
import com.rahul.libgdx.parallax.ParallaxLayer;
import com.rahul.libgdx.parallax.TextureRegionParallaxLayer;
import com.rahul.libgdx.parallax.Utils;

import java.util.ArrayList;
import java.util.List;

import me.stammberger.starcitizencompact.map.GtStarMap;
import me.stammberger.starcitizencompact.map.data.StarMapData;
import me.stammberger.starcitizencompact.map.data.SystemsResultset;
import me.stammberger.starcitizencompact.map.data.TunnelsResultset;

public class MainScreen extends BaseScreen {
    private final float mViewportWidth = 1000f;
    private final float mMaxZoom = 8.5f;
    private final float mMinZoom = 0.5f;
    private final int systemBackgroundRadius = 50;
    private final int systemBackgroundRadiusSelected = 75;
    private final Color mSystemHandleBackgroundColor = Color.valueOf("04454CFF");
    private final Color mSystemHandleOutlineColor = Color.valueOf("42EDFFFF");
    private final Color mTunnelColor = Color.valueOf("0A55B1FF");
    private final Color mSelectedEntryTunnelColor = Color.valueOf("FF0000FF");
    private final Color mSelectedExitTunnelColor = Color.valueOf("00FF00FF");
    private final Color mSelectedTwoWayTunnelColor = Color.valueOf("FFFF00FF");
    private final GlyphLayout mFontGlyphlayout;
    private ShapeRenderer mShapeRenderer;
    private OrthographicCamera mCam;
    private Vector3 mTouchPos;
    private Vector2 mOldInitialFirstPointer;
    private Vector2 mOldInitialSecondPointer;
    private float mOldScale;
    private SpriteBatch mSpriteBatch;
    private ParallaxBackground mParallaxBackground;
    private Texture mBackgroundTexture;
    private float mMaxPanX = 8500f;
    private float mMaxPanY = 5800f;
    private BitmapFont mFont;
    private BitmapFont mFontSelected;
    private SystemsResultset mSelectedSystem;
    private int mSelectedSystemIndex = -1;
    private ArrayList<TunnelsResultset> mSelectedExitTunnels = new ArrayList<TunnelsResultset>();
    private ArrayList<TunnelsResultset> mSelectedEntryTunnels = new ArrayList<TunnelsResultset>();
    private GtStarMap.SystemSelectedCallback mSelectedCallback;
    private FPSLogger mFPSLogger = new FPSLogger();
    private StarMapData mMapData;

    public MainScreen(GtStarMap.SystemSelectedCallback callback) {
        if (callback == null) {
            throw new NullPointerException("SystemSelectedCallback must not be null");
        }
        mSelectedCallback = callback;

        mShapeRenderer = new ShapeRenderer();
        mTouchPos = new Vector3();

        mSpriteBatch = new SpriteBatch();

        setupBackground();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        mCam = new OrthographicCamera(mViewportWidth, mViewportWidth * (h / w));
        mCam.position.set(mCam.viewportWidth / 2f, mCam.viewportHeight / 2f, 0);
        mCam.update();

        mFont = new BitmapFont(Gdx.files.internal("TestFont.fnt"), Gdx.files.internal("TestFont.png"), false);
        mFont.getData().setScale(2);

        mFontSelected = new BitmapFont(Gdx.files.internal("TestFont.fnt"), Gdx.files.internal("TestFont.png"), false);
        mFontSelected.getData().setScale(4);

        mFontGlyphlayout = new GlyphLayout();
    }

    private void setupBackground() {
        mBackgroundTexture = new Texture(Gdx.files.internal("space_background.jpg"));
        mParallaxBackground = new ParallaxBackground();
        TextureRegionParallaxLayer layer = new TextureRegionParallaxLayer(
                new TextureRegion(mBackgroundTexture), mViewportWidth * 16, new Vector2(), Utils.WH.width);
        layer.setAllPad(-mViewportWidth * 5);
        layer.setParallaxRatio(0.1f, 0.1f);
        layer.setTileModeX(ParallaxLayer.TileMode.single);
        layer.setTileModeY(ParallaxLayer.TileMode.single);
        mParallaxBackground.addLayers(layer);
    }

    public void setMapData(StarMapData data) {
        mMapData = data;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        mCam.update();
        mShapeRenderer.setProjectionMatrix(mCam.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mSpriteBatch.begin();
        mParallaxBackground.draw(mCam, mSpriteBatch);
        mSpriteBatch.end();


        if (mMapData != null) {
            renderSelected();

            mShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(4);
            mShapeRenderer.setColor(mTunnelColor);
            SystemsResultset from;
            SystemsResultset to;
            for (TunnelsResultset s : mMapData.data.tunnels.resultset) {
                from = mMapData.data.systemHashMap.get(s.entry.starSystemId);
                to = mMapData.data.systemHashMap.get(s.exit.starSystemId);
                mShapeRenderer.line(from.positionX, from.positionY, to.positionX, to.positionY);
            }
            mShapeRenderer.end();

            mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (SystemsResultset s : mMapData.data.systems.resultset) {
                mShapeRenderer.setColor(mSystemHandleOutlineColor);
                mShapeRenderer.circle(s.positionX, s.positionY, systemBackgroundRadius);

                mShapeRenderer.setColor(mSystemHandleBackgroundColor);
                mShapeRenderer.circle(s.positionX, s.positionY, systemBackgroundRadius - 1);
            }

            mShapeRenderer.setColor(Color.BLUE);
            mShapeRenderer.circle(mMapData.data.origin.x, mMapData.data.origin.y, systemBackgroundRadius);
            mShapeRenderer.end();

            mSpriteBatch.begin();
            for (SystemsResultset s : mMapData.data.systems.resultset) {
                mFontGlyphlayout.setText(mFont, s.name);
                mFont.draw(mSpriteBatch,
                        s.name,
                        s.positionX - mFontGlyphlayout.width / 2,
                        s.positionY + 115);
            }
            mSpriteBatch.end();
        }
    }

    private void renderSelected() {
        if (mSelectedSystem == null) {
            return;
        }
        mShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(7);
        mShapeRenderer.setColor(mSelectedEntryTunnelColor);
        SystemsResultset from;
        SystemsResultset to;
        for (TunnelsResultset s : mSelectedEntryTunnels) {
            from = mMapData.data.systemHashMap.get(s.entry.starSystemId);
            to = mMapData.data.systemHashMap.get(s.exit.starSystemId);
            mShapeRenderer.line(from.positionX, from.positionY, to.positionX, to.positionY);
        }

        mShapeRenderer.setColor(mSelectedExitTunnelColor);
        for (TunnelsResultset s : mSelectedExitTunnels) {
            from = mMapData.data.systemHashMap.get(s.entry.starSystemId);
            to = mMapData.data.systemHashMap.get(s.exit.starSystemId);
            mShapeRenderer.line(from.positionX, from.positionY, to.positionX, to.positionY);
        }
        mShapeRenderer.end();

        mShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        mShapeRenderer.setColor(mSystemHandleOutlineColor);
        mShapeRenderer.circle(mSelectedSystem.positionX,
                mSelectedSystem.positionY, systemBackgroundRadiusSelected);

        mShapeRenderer.setColor(mSystemHandleBackgroundColor);
        mShapeRenderer.circle(mSelectedSystem.positionX,
                mSelectedSystem.positionY, systemBackgroundRadiusSelected - 5);

        mShapeRenderer.end();

        mSpriteBatch.begin();
        mFontGlyphlayout.setText(mFontSelected, mSelectedSystem.name);
        mFontSelected.draw(mSpriteBatch,
                mSelectedSystem.name,
                mSelectedSystem.positionX - mFontGlyphlayout.width / 2,
                mSelectedSystem.positionY + 175);
        mSpriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        mCam.viewportWidth = mViewportWidth;
        mCam.viewportHeight = mViewportWidth * height / width;
        mCam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        int x_screen = -(Gdx.graphics.getWidth() / 2) + (int) x;
        int y_screen = -(Gdx.graphics.getHeight() / 2) + (int) y;
        mSelectedCallback.onTap(x_screen, y_screen);

        mTouchPos = mCam.unproject(new Vector3(x, y, 0));
        for (SystemsResultset s : mMapData.data.systems.resultset) {
            if (s.contains(mTouchPos.x, mTouchPos.y)) {
                mSelectedCallback.onSystemSelected(s.code, x_screen, y_screen);
                setSelectedSystem(s);
                return true;
            }
        }

        resetSelection();
        return false;
    }

    private void setSelectedSystem(SystemsResultset s) {
        List<SystemsResultset> systems = mMapData.data.systems.resultset;
        List<TunnelsResultset> tunnels = mMapData.data.tunnels.resultset;

        resetSelection();

        mSelectedSystem = s;
        mSelectedSystemIndex = systems.indexOf(s);
        for (TunnelsResultset tunnelsResultset : tunnels) {
            if (tunnelsResultset.entry.starSystemId == s.id) {
                mSelectedEntryTunnels.add(tunnelsResultset);
            } else if (tunnelsResultset.exit.starSystemId == s.id) {
                mSelectedExitTunnels.add(tunnelsResultset);
            }
        }

        tunnels.removeAll(mSelectedEntryTunnels);
        tunnels.removeAll(mSelectedExitTunnels);
        systems.remove(s);
    }

    private void resetSelection() {
        List<SystemsResultset> systems = mMapData.data.systems.resultset;
        List<TunnelsResultset> tunnels = mMapData.data.tunnels.resultset;
        if (mSelectedSystem != null) {
            systems.add(mSelectedSystemIndex, mSelectedSystem);
            tunnels.addAll(mSelectedEntryTunnels);
            mSelectedEntryTunnels.clear();

            tunnels.addAll(mSelectedExitTunnels);
            mSelectedExitTunnels.clear();

            mSelectedSystem = null;
        }
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        mCam.update();
        mCam.position.add(
                mCam.unproject(new Vector3(0, 0, 0))
                        .add(mCam.unproject(new Vector3(deltaX, deltaY, 0)).scl(-1f))
        );

        if (mCam.position.x > mMaxPanX) {
            mCam.position.x = mMaxPanX;
        } else if (mCam.position.x < -mMaxPanX) {
            mCam.position.x = -mMaxPanX;
        }
        if (mCam.position.y > mMaxPanY) {
            mCam.position.y = mMaxPanY;
        } else if (mCam.position.y < -mMaxPanY) {
            mCam.position.y = -mMaxPanY;
        }

        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        // awesome code from http://sadale.net/7/libgdx-pinch-to-zoom
        if (!(initialPointer1.equals(mOldInitialFirstPointer)
                && initialPointer2.equals(mOldInitialSecondPointer))) {
            mOldInitialFirstPointer = initialPointer1.cpy();
            mOldInitialSecondPointer = initialPointer2.cpy();
            mOldScale = mCam.zoom;
        }

        Vector3 center = new Vector3(
                (pointer1.x + initialPointer2.x) / 2,
                (pointer1.y + initialPointer2.y) / 2,
                0
        );

        zoomCamera(center, mOldScale * initialPointer1.dst(initialPointer2) / pointer1.dst(pointer2));
        return true;
    }

    private void zoomCamera(Vector3 origin, float scale) {
        mCam.update();
        Vector3 oldUnprojection = mCam.unproject(origin.cpy()).cpy();
        mCam.zoom = scale; //Larger value of zoom = small images, border view
        mCam.zoom = Math.min(mMaxZoom, Math.max(mCam.zoom, mMinZoom));
        mCam.update();
        Vector3 newUnprojection = mCam.unproject(origin.cpy()).cpy();
        mCam.position.add(oldUnprojection.cpy().add(newUnprojection.cpy().scl(-1f)));
    }
}
