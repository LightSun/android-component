package com.heaven7.android.components.demo;

import com.heaven7.android.components.demo.sample.TestGlideComponentActivity;
import com.heaven7.android.components.demo.sample.TestLoadingComponentActivity;

import java.util.List;

/**
 * Created by heaven7 on 2017/5/28.
 */
public class SampleActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(TestGlideComponentActivity.class));
        list.add(new ActivityInfo(TestLoadingComponentActivity.class));
    }
}
