package com.heaven7.android.components.demo;

import com.heaven7.android.components.demo.sample.TestGlideComponentActivity;
import com.heaven7.android.components.demo.sample.TestGuideActivity;
import com.heaven7.android.components.demo.sample.TestLoadingComponentActivity;
import com.heaven7.android.components.demo.sample.TestToastActivity;

import java.util.List;

/**
 * Created by heaven7 on 2017/5/28.
 */
public class SampleActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(TestGlideComponentActivity.class));
        list.add(new ActivityInfo(TestLoadingComponentActivity.class));
        list.add(new ActivityInfo(TestGuideActivity.class));
        list.add(new ActivityInfo(TestToastActivity.class));
    }
}
