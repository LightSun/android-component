package com.heaven7.android.component.guide;

import android.view.View;

/**
 * the guide component indicate a component of guide.
 * that means a guide may contains multi components of guide.
 *
 * @author heaven7
 */
public final class GuideComponent {
    /**
     * the anchor view which is already showed on screen
     */
    private View anchor;
    /**
     * the tip view which will show for target anchor
     */
    private View tip;
    /**
     * the show info of relative to anchor
     */
    private RelativeLocation rp;

    /**
     * get the anchor view id.
     *
     * @return the anchor view id
     */
    public int getAnchorViewId() {
        return anchor.getId();
    }

    /**
     * get the anchor view.
     *
     * @return the anchor view
     */
    public View getAnchor() {
        return anchor;
    }

    /**
     * get tip view. may be null
     *
     * @return tip view
     */
    public View getTip() {
        return tip;
    }

    /**
     * get the relative location.
     *
     * @return the relative location
     */
    public RelativeLocation getRelativeLocation() {
        return rp;
    }

    /**
     * the guide component builder.
     */
    public static class Builder {

        private final GuideComponent gc = new GuideComponent();

        /**
         * assign the anchor view
         *
         * @param anchor the anchor view
         * @return this.
         */
        public Builder anchor(View anchor) {
            gc.anchor = anchor;
            return this;
        }

        /**
         * assign the tip view, may be null.
         *
         * @param tip the tip view
         * @return this.
         */
        public Builder tip(View tip) {
            gc.tip = tip;
            return this;
        }

        /**
         * assign the relative location of tip view.
         *
         * @param rp the relative location
         * @return this.
         */
        public Builder location(RelativeLocation rp) {
            gc.rp = rp;
            return this;
        }

        /**
         * build the component
         *
         * @return the guide component.
         */
        public GuideComponent build() {
            if(gc.anchor == null)
                throw new NullPointerException("must assigned the anchor view");
            if(gc.tip != null && gc.rp == null){
                throw new IllegalStateException("when use tip, RelativeLocation can't be null.");
            }
            return gc;
        }
    }
}
