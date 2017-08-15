package com.heaven7.android.components.demo.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.heaven7.android.component.image.ImageRequestEditor;
import com.heaven7.android.components.demo.BaseActivity;
import com.heaven7.android.components.demo.R;
import com.heaven7.android.components.demo.util.LogImageLoadCallback;
import com.heaven7.android.components.demo.util.TestUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class TestGlideComponentActivity extends BaseActivity{

    private static final String TAG = "TestGlideComponent";

    @BindView(R.id.iv1)
    ImageView mIv1;

    @BindView(R.id.bt1)
    Button mBt1;

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_glide_component;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {

    }

    @OnClick(R.id.bt1)
    public void onClickBt1(View v){
        //showByCallback();
        showByInto();
    }

    private void showByInto() {
        getAppImageComponent().newEditor(this)
                .fromUrl(TestUtil.TEST_IMAGE)
                .placeholder(R.mipmap.ic_launcher)
                .override(450, 300)
                .diskCacheFlags(ImageRequestEditor.FLAG_CACHE_SOURCE)
                .round(30)
               // .callback()
                .into(mIv1);
    }

    private void showByCallback() {
        getAppImageComponent().newEditor(this)
                .fromUrl(TestUtil.TEST_IMAGE)
                .placeholder(R.mipmap.ic_launcher)
                .override(450, 300)
                .diskCacheFlags(ImageRequestEditor.FLAG_CACHE_SOURCE)
               // .transform(new CircleTransformer()) //ok
                //.round(30)      //ok
                .circle(10, Color.RED)
                .callback(new LogImageLoadCallback(){
                    @Override
                    public void onLoadComplete(String key, Bitmap result) {
                        super.onLoadComplete(key, result);
                        mIv1.setImageBitmap(result);
                    }
                })
                .load();
    }


}
