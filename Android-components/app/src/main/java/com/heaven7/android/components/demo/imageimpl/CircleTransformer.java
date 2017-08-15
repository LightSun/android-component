package com.heaven7.android.components.demo.imageimpl;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.heaven7.android.component.image.ImageCachePool;
import com.heaven7.android.component.image.BitmapTransformer;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class CircleTransformer implements BitmapTransformer{

    private final Paint mBorderPaint;
    private final float mBorderWidth;

    public CircleTransformer(){
        this(0, Color.TRANSPARENT);
    }
    public CircleTransformer(float borderWidth, int borderColor){
        mBorderWidth = borderWidth;
        if(borderWidth < 0){
            throw new IllegalStateException();
        }
        if(borderWidth == 0){
            mBorderPaint = null;
        }else {
            mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBorderPaint.setDither(true);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(borderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mBorderWidth);
        }
    }

    @Override
    public Bitmap transform(String key , ImageCachePool pool, Bitmap source, int outWidth, int outHeight) {
        if (source == null) return null;

        int size = (int) (Math.min(source.getWidth(), source.getHeight()) - (mBorderWidth / 2));
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = pool.get(key , size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        if (mBorderPaint != null) {
            float borderRadius = r - mBorderWidth / 2;
            canvas.drawCircle(r, r, borderRadius, mBorderPaint);
        }
        return result;
    }

    @Override
    public String getId() {
        return "com.heaven7.android.CircleTransformer";
    }
}
