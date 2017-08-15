package com.heaven7.android.components.demo.imageimpl;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.heaven7.android.component.image.BitmapPool;
import com.heaven7.android.component.image.BitmapTransformer;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class RoundTransformer implements BitmapTransformer {

    private final float radius;

    public RoundTransformer(float radius) {
        this.radius = radius;
    }

    @Override
    public Bitmap transform(String key, BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) return null;

        Bitmap result = pool.get(key, source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override
    public String getId() {
        return "com.heaven7.android.RoundTransformer";
    }
}
