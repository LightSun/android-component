package com.heaven7.android.component.image;

import android.graphics.Bitmap;

/**
 * the bitmap transformer.
 * <p><h2>Here is a demo of CircleTransformer which can make bitmap to be circle.</h2></p>
 * <pre><code>
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

    // @Override
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

     //@Override
     public String getId() {
         return "com.heaven7.android.CircleTransformer";
     }
 }

  * </code></pre>
 * @author heaven7
 * @since 1.0.1
 */
public interface BitmapTransformer {

    /**
     * called on transform the bitmap.
     * @param key the key of image
     * @param pool the bitmap pool
     * @param source    the source.
     * @param outWidth  the out/expect width
     * @param outHeight the out/expect height.
     * @return a new bitmap
     */
    Bitmap transform(String key, ImageCachePool pool, Bitmap source, int outWidth, int outHeight);

    /**
     * get the id of this transformer.
     * @return the id
     */
    String getId();
}