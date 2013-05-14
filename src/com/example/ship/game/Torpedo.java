package com.example.ship.game;

import android.graphics.PointF;
import com.example.ship.R;
import com.example.ship.SceletonActivity;
import org.andengine.entity.modifier.*;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseExponentialOut;

public class Torpedo extends Sprite {

    private static final float TIME_OF_FLIGHT = 30;

    private PointF startPoint;
    private PointF finishPoint;
    private double radians;

    public Torpedo(SceletonActivity activity, PointF point, float angle) {
        super( point.x
             , point.y
             , activity.getResourceManager().getLoadedTextureRegion(R.drawable.torpedo)
             , activity.getEngine().getVertexBufferObjectManager());

        this.setPosition( point.x - this.getWidth() / 2
                        , point.y - this.getHeight() / 2);

        startPoint = new PointF( point.x - this.getWidth() / 2
                               , point.y - this.getHeight() / 2);
        finishPoint = new PointF(0, 0);

        this.radians = Math.toRadians(angle);

        createModifier();
    }

    private void createModifier() {

        finishPoint.y = 0;
        finishPoint.x = startPoint.x + (float) Math.tan(radians) * startPoint.y;

        // AlphaModifier(время исполнения, изначальная прозрачность, конечная прозрачность)
        // LoopEntityModifier - выполняет модификатор пока не отменим
        LoopEntityModifier alphaLoopEntityModifier = new LoopEntityModifier(new AlphaModifier(1, 1, 0));

        // EaseExponentialOut.getInstance() - способ передвижения
        MoveModifier moveModifier = new MoveModifier( TIME_OF_FLIGHT
                                                    , startPoint.x
                                                    , finishPoint.x
                                                    , startPoint.y
                                                    , finishPoint.y
                                                    , EaseExponentialOut.getInstance());

        // ParallelEntityModifier - выполняет модификаторы параллельно
        ParallelEntityModifier parallelEntityModifier =
                new ParallelEntityModifier(alphaLoopEntityModifier, moveModifier);

        LoopEntityModifier loopEntityModifier = new LoopEntityModifier(parallelEntityModifier);

        this.registerEntityModifier(loopEntityModifier);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        // постепенное уменьшение торпеды
        this.setScaleY(this.getY() / startPoint.y);
        super.onManagedUpdate(pSecondsElapsed);
    }

}
