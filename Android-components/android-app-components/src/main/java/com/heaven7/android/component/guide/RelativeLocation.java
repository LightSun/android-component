package com.heaven7.android.component.guide;

import android.util.Log;

import static com.heaven7.android.component.guide.AppGuideComponent.*;

/**
 * the relative location of guide.
 * @author heaven7
 */
public final class RelativeLocation {
    private final byte alignType;
    private final int margin; //align margin

    private final byte relativeType;
    /**
     * offset of center, top and left is positive, right bottom is negative.
     * may be percent of sth.
     *
     * @see AppGuideComponent#ABSOLUTE
     * @see AppGuideComponent#RELATIVE_ANCHOR
     * @see AppGuideComponent#RELATIVE_TIP
     */
    private final float offsetValue;

    /**
     * create relative location with relative type ({@linkplain AppGuideComponent#ABSOLUTE}).
     *
     * @param alignType  the align type. see {@linkplain AppGuideComponent#ALIGN_BOTTOM} and etc.
     * @param margin the margin of align
     * @param offset the absolute offset value .
     * @see AppGuideComponent#ALIGN_BOTTOM
     * @see AppGuideComponent#ALIGN_LEFT
     * @see AppGuideComponent#ALIGN_RIGHT
     * @see AppGuideComponent#ALIGN_TOP
     */
    public RelativeLocation(byte alignType, int margin, int offset) {
        this(alignType, margin, ABSOLUTE, offset);
    }

    /**
     * create relative location.
     *
     * @param alignType        the align type. see {@linkplain AppGuideComponent#ALIGN_BOTTOM} and etc.
     * @param margin       the margin of align
     * @param relativeType the relative type . see {@link AppGuideComponent#ABSOLUTE} and etc.
     * @param offsetValue  the offset value. may be absolute value of relative value.
     * @see AppGuideComponent#ABSOLUTE
     * @see AppGuideComponent#RELATIVE_TIP
     * @see AppGuideComponent#RELATIVE_ANCHOR
     */
    public RelativeLocation(byte alignType, int margin, byte relativeType, float offsetValue) {
        this.alignType = alignType;
        this.margin = margin;
        this.relativeType = relativeType;
        this.offsetValue = offsetValue;
    }

    /**
     * get the align type.
     * @return the align type
     * @see AppGuideComponent#ALIGN_BOTTOM
     * @see AppGuideComponent#ALIGN_TOP
     * @see AppGuideComponent#ALIGN_LEFT
     * @see AppGuideComponent#ALIGN_RIGHT
     */
    public byte getAlignType() {
        return alignType;
    }

    /**
     * get the margin of align type.
     * @return the margin of align type
     */
    public int getMargin() {
        return margin;
    }

    /**
     * get the relative type
     * @return the relative type
     * @see AppGuideComponent#ABSOLUTE
     * @see AppGuideComponent#RELATIVE_ANCHOR
     * @see AppGuideComponent#RELATIVE_TIP
     */
    public byte getRelativeType() {
        return relativeType;
    }

    /**
     * get the offset value.
     * @return the offset value.
     */
    public float getOffsetValue() {
        return offsetValue;
    }

    /**
     * get the offset of tip.
     * @param anchorW the width of anchor
     * @param anchorH the height of anchor
     * @param tipW the width of tip
     * @param tipH the height of tip
     * @return the expect offset of tip. or 0 . if not support.
     * @see AppGuideComponent#ABSOLUTE
     * @see AppGuideComponent#RELATIVE_ANCHOR
     * @see AppGuideComponent#RELATIVE_TIP
     */
    public int getOffSet(int anchorW, int anchorH, int tipW, int tipH) {
        switch (relativeType) {
            case ABSOLUTE:
                return (int) offsetValue;
            case RELATIVE_ANCHOR: {
                switch (alignType) {
                    case ALIGN_BOTTOM:
                    case ALIGN_TOP:
                        return (int) (anchorW * offsetValue);

                    case ALIGN_LEFT:
                    case ALIGN_RIGHT:
                        return (int) (anchorH * offsetValue);
                }
            }
            case RELATIVE_TIP: {
                switch (alignType) {
                    case ALIGN_BOTTOM:
                    case ALIGN_TOP:
                        return (int) (tipW * offsetValue);

                    case ALIGN_LEFT:
                    case ALIGN_RIGHT:
                        return (int) (tipH * offsetValue);
                }
            }
            default:
                Log.w("RelativeLocation", "called [ getOffSet()]: wrong relative type = " + relativeType);
        }
        return 0;
    }
}