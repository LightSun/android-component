package com.heaven7.android.component.gallery;

/**
 * the pick option
 * @author heaven7
 * @since 1.1.4
 */
public class PickOption {

    private int minCount;
    private int maxCount;
    private boolean crop;
    private int aspectX;
    private int aspectY;
    private int outputX;
    private int outputY;
    private String outputFormat; //png ,jpeg and etc.

    protected PickOption(PickOption.Builder builder) {
        this.minCount = builder.minCount;
        this.maxCount = builder.maxCount;
        this.crop = builder.crop;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;
        this.outputFormat = builder.outputFormat;
    }

    public int getMinCount() {
        return this.minCount;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public boolean isCrop() {
        return this.crop;
    }

    public int getAspectX() {
        return this.aspectX;
    }

    public int getAspectY() {
        return this.aspectY;
    }

    public int getOutputX() {
        return this.outputX;
    }

    public int getOutputY() {
        return this.outputY;
    }

    public String getOutputFormat() {
        return this.outputFormat;
    }

    public static class Builder {
        private int minCount;
        private int maxCount;
        private boolean crop;
        private int aspectX;
        private int aspectY;
        private int outputX;
        private int outputY;
        private String outputFormat; //png ,jpeg and etc.

        public Builder setMinCount(int minCount) {
            this.minCount = minCount;
            return this;
        }

        public Builder setMaxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }

        public Builder setCrop(boolean crop) {
            this.crop = crop;
            return this;
        }

        public Builder setAspectX(int aspectX) {
            this.aspectX = aspectX;
            return this;
        }

        public Builder setAspectY(int aspectY) {
            this.aspectY = aspectY;
            return this;
        }

        public Builder setOutputX(int outputX) {
            this.outputX = outputX;
            return this;
        }

        public Builder setOutputY(int outputY) {
            this.outputY = outputY;
            return this;
        }

        public Builder setOutputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public PickOption build() {
            return new PickOption(this);
        }
    }
}
