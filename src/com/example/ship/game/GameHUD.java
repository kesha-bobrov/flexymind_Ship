package com.example.ship.game;

import android.graphics.PointF;
import android.util.Log;
import com.example.ship.Events;
import com.example.ship.R;
import com.example.ship.SceletonActivity;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.andengine.opengl.texture.region.ITextureRegion;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Vasya
 * Date: 03.05.13
 * Time: 17:19
 * To change this template use File | Settings | File Templates.
 */
public class GameHUD extends HUD {

    private static final float RELATIVE_BUTTON_HEIGHT = 0.15f;
    private static final float RELATIVE_SPACE_BETWEEN_CONTROLS = 0.01f;
    private static final float RELATIVE_SCREEN_BORDER = 0.02f;
    private static final float BUTTON_ALPHA = 0.75f;
    private final SceletonActivity activity;
    private final Engine engine;
    private       PointF cameraSize;
    private       ArrayList<GameButtonSprite> buttons;
    private       DigitalOnScreenControl rotateGunDigitalControl;

    public GameHUD(SceletonActivity activity) {
        super();
        setOnAreaTouchTraversalFrontToBack();
        buttons = new ArrayList<GameButtonSprite>();
        this.activity = activity;
        engine = this.activity.getEngine();
        cameraSize = new PointF( this.activity.getCamera().getWidthRaw()
                               , this.activity.getCamera().getHeightRaw());

        createButtons();
        createRotateGunDigitalControl();
    }

    private void createButtons() {
        GameButtonSprite pauseButton;
        pauseButton = new GameButtonSprite( activity.getResourceManager()
                                                    .getLoadedTextureRegion(R.drawable.pausebutton)
                                          , engine.getVertexBufferObjectManager()
                                          , R.string.GAME_PAUSE_BUTTON);
        buttons.add(pauseButton);

        GameButtonSprite fireButton;
        fireButton = new GameButtonSprite( activity.getResourceManager()
                                                    .getLoadedTextureRegion(R.drawable.firebutton)
                                         , engine.getVertexBufferObjectManager()
                                         , R.string.GAME_FIRE_BUTTON);
        buttons.add(fireButton);
        for (GameButtonSprite button: buttons) {
            if (button.getId() == R.string.GAME_BORDER_BUTTON) {
                continue;
            }
            button.setAlpha(BUTTON_ALPHA);
            button.setScale(cameraSize.y * RELATIVE_BUTTON_HEIGHT / fireButton.getHeight());
            this.registerTouchArea(button);
            this.attachChild(button);
        }

        pauseButton.setPosition( RELATIVE_SCREEN_BORDER * cameraSize.x
                               , RELATIVE_SCREEN_BORDER * cameraSize.y);
        fireButton.setPosition( (1 - RELATIVE_SCREEN_BORDER) * cameraSize.x - fireButton.getWidth()
                              , (1 - RELATIVE_SCREEN_BORDER) * cameraSize.y - fireButton.getHeight());
    }

    private void createRotateGunDigitalControl() {
        ITextureRegion rotateGunDigitalControlBaseTextureRegion = activity.getResourceManager()
                                                            .getLoadedTextureRegion( R.drawable.onscreen_control_base );
        ITextureRegion rotateGunDigitalControlKnobTextureRegion = activity.getResourceManager()
                                                            .getLoadedTextureRegion( R.drawable.onscreen_control_knob );

        final float CONTROL_BASE_TEXTURE_HEIGHT = rotateGunDigitalControlBaseTextureRegion.getHeight();
        final PointF BASE_TEXTURE_LEFT_BOTTOM = new PointF( 0f , 128f ); // Чтобы текстура не выходила за границы экрана
        final float TIME_PERIOD_CHECK = 0.1f;                            // при масштабировании
        final float RELATIVE_CONTROL_HEIGHT = 0.3f;
        final PointF GUN_DIGITAL_CONTROL_COORDINATE = new PointF( cameraSize.x * RELATIVE_SCREEN_BORDER
                                      , ( 1 - RELATIVE_SCREEN_BORDER )*( cameraSize.y - CONTROL_BASE_TEXTURE_HEIGHT ) );

        rotateGunDigitalControl = new DigitalOnScreenControl( GUN_DIGITAL_CONTROL_COORDINATE.x
                                                            , GUN_DIGITAL_CONTROL_COORDINATE.y
                                                            , activity.getCamera()
                                                            , rotateGunDigitalControlBaseTextureRegion
                                                            , rotateGunDigitalControlKnobTextureRegion
                                                            , TIME_PERIOD_CHECK
                                                            , activity.getVertexBufferObjectManager()
                                                            , new BaseOnScreenControl.IOnScreenControlListener() {
            @Override
            public void onControlChange( BaseOnScreenControl baseOnScreenControl, float xShift, float yShift ) {
                if          ( xShift == -1 ) {
                    activity.getSceneSwitcher().getGameScene().getGun().rotateLeft();
                } else if   ( xShift == 1 ) {
                    activity.getSceneSwitcher().getGameScene().getGun().rotateRight();
                } else {
                    activity.getSceneSwitcher().getGameScene().getGun().stopRotate();
                }
            }
        });
        rotateGunDigitalControl.getControlBase().setScaleCenter( BASE_TEXTURE_LEFT_BOTTOM.x, BASE_TEXTURE_LEFT_BOTTOM.y );
        rotateGunDigitalControl.getControlBase().setScale( cameraSize.y * RELATIVE_BUTTON_HEIGHT
                                                        / rotateGunDigitalControlBaseTextureRegion.getHeight() );
        rotateGunDigitalControl.getControlKnob().setScale( cameraSize.y * RELATIVE_BUTTON_HEIGHT
                                                        / rotateGunDigitalControlBaseTextureRegion.getHeight() );
        rotateGunDigitalControl.refreshControlKnobPosition();
        this.setChildScene( rotateGunDigitalControl );
    }
    public void setEventsToChildren(Events events) {
        for (GameButtonSprite button: buttons) {
            button.setEvents(events);
        }
    }
}
