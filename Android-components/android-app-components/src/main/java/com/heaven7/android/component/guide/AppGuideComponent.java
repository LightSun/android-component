package com.heaven7.android.component.guide;

import android.view.View;

/**
 * the guide component
 * Created by heaven7 on 2017/8/14 0014.
 */

public interface AppGuideComponent {

    /**
     * align direction: left
     */
    byte ALIGN_LEFT = 1;
    /**
     * align direction: right
     */
    byte ALIGN_RIGHT = 2;
    /**
     * align direction: top
     */
    byte ALIGN_TOP   = 3;
    /**
     * align direction: bottom
     */
    byte ALIGN_BOTTOM = 4;

    /**
     * indicate absolute value.
     */
    byte ABSOLUTE        = 11;
    /**
     * indicate relative to tip view of guide.
     */
    byte RELATIVE_TIP    = 12;
    /**
     * indicate relative to anchor view of guide.
     */
    byte RELATIVE_ANCHOR = 14;

    /**
     * the guide callback.
     */
    abstract class GuideCallback{

        /**
         * called when click the root view.
         * @param root the root view
         * @return true if handled this event. false otherwise.
         */
        public boolean handleClickRoot(View root){
            return false;
        }
        /**
         * called when click the tip view.
         * @param tip the tip view
         * @return true if handled this event. false otherwise.
         */
        public boolean handleClickTip(View tip){
            return false;
        }
        /**
         * called when click the Anchor view.
         * @param copyOfAnchor the copy of Anchor view
         * @return true if handled this event. false otherwise.
         */
        public boolean handleClickAnchor(View copyOfAnchor){
            return false;
        }

        /**
         * called on bind data for anchor view.
         * @param copyOfAnchor the copy of anchor view
         */
        public void onBindData(View copyOfAnchor) {

        }

        /**
         * called on show the guide
         */
        public void onShow() {

        }
        /**
         * called on dismiss the guide
         */
        public void onDismiss() {

        }
    }

    /**
     * show the guide by target component and callback.
     * @param gc the guide component.
     * @param callback the guide callback.
     */
    void show(GuideComponent gc, GuideCallback callback);
    /**
     * show the guide by target components and callback.
     * @param gc the guide components.
     * @param callback the guide callback.
     */
    void show(GuideComponent[] gc, GuideCallback callback);

    /**
     * cancel the guide and will not notify the callback of dismiss.
     * this method is unlike the {@linkplain #dismiss()}.
     * @see #dismiss()
     */
    void cancel();

    /**
     * dismiss the guide window and notify the callback of dismiss if need.
     * this method is unlike the {@linkplain #cancel()}.
     * @see #cancel()
     */
    void dismiss();

    /**
     * Register a callback to be invoked when a hardware key is pressed in this view.
     * Key presses in software input methods will generally not trigger the methods of
     * this listener.
     * @param l the key listener to attach to this view(root of this guide)
     */
    void setOnKeyListener(View.OnKeyListener l);

}
