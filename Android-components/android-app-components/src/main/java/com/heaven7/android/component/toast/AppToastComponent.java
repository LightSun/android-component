package com.heaven7.android.component.toast;

import com.heaven7.android.component.AppComponentContext;

/**
 * the toast component.
 * <p>Here is some test code.</p>
 * <pre><code>
 //@OnClick(R.id.bt_toast_warn)
 public void onClickWarnToast(final View v){
     getAppToastComponent()
         .withEndAction(new Runnable() {
             //@Override
             public void run() {
             Toaster.show(v.getContext(), "action end...");
             }
         })
         .withStartAction(new Runnable() {
             //@Override
             public void run() {
             Toaster.show(v.getContext(), "action start...");
             }
         })
         .type(AppToastComponent.TYPE_WARN)
         .show("your toast message");
 }
// @OnClick(R.id.bt_toast_error)
 public void onClickErrorToast(View v){
     getAppToastComponent().type(AppToastComponent.TYPE_ERROR).show("your toast message");
 }
// @OnClick(R.id.bt_toast_click)
 public void onClickClickToast(View v){
     final View.OnClickListener l = new View.OnClickListener() {
         //@Override
         public void onClick(View v) {
         Toaster.show(v.getContext(), "Toast view was clicked.");
         }
     };
     getAppToastComponent()
         .type(AppToastComponent.TYPE_WARN)
         .enableClick(true)
         .bindView(new AppToastComponent.IViewBinder() {
             //@Override
             public void onBind(View view) {
             view.setOnClickListener(l);
             }
         })
         .show("your toast message");
 }
 * </code></pre>
 * Created by heaven7 on 2017/8/14 0014.
 */

public interface AppToastComponent extends IWindow, AppComponentContext {

}
