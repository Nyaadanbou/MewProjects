package cc.mewcraft.mewcore.progressbar;

import com.google.common.base.Preconditions;

public class ProgressbarGenerator {

    private String left;
    private String full;
    private String empty;
    private String right;
    private String fullPrefix;
    private String emptyPrefix;
    private int width;

    private ProgressbarGenerator() {
    }

    public String create(double fillPercent) {
        Preconditions.checkArgument(fillPercent >= 0 && fillPercent <= 1);

        int repeatFull = (int) Math.ceil(width * fillPercent);
        int repeatEmpty = width - repeatFull;
        return left + fullPrefix + full.repeat(repeatFull) + emptyPrefix + empty.repeat(repeatEmpty) + right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getFull() {
        return full;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public String getEmpty() {
        return empty;
    }

    public void setEmpty(String empty) {
        this.empty = empty;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getFullPrefix() {
        return fullPrefix;
    }

    public void setFullPrefix(String fullPrefix) {
        this.fullPrefix = fullPrefix;
    }

    public String getEmptyPrefix() {
        return emptyPrefix;
    }

    public void setEmptyPrefix(String emptyPrefix) {
        this.emptyPrefix = emptyPrefix;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static final class Builder {
        private String left = "<dark_gray>[</dark_gray>";
        private String full = "<aqua>|</aqua>";
        private String empty = "<gray>|</gray>";
        private String right = "<dark_gray>]</dark_gray>";
        private String fullPrefix = "";
        private String emptyPrefix = "";
        private int width = 60;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder left(String left) {
            this.left = left;
            return this;
        }

        public Builder full(String full) {
            this.full = full;
            return this;
        }

        public Builder empty(String empty) {
            this.empty = empty;
            return this;
        }

        public Builder right(String right) {
            this.right = right;
            return this;
        }

        public Builder fullPrefix(String fullPrefix) {
            this.fullPrefix = fullPrefix;
            return this;
        }

        public Builder emptyPrefix(String emptyPrefix) {
            this.emptyPrefix = emptyPrefix;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public ProgressbarGenerator build() {
            ProgressbarGenerator progressbarGenerator = new ProgressbarGenerator();
            progressbarGenerator.setLeft(left);
            progressbarGenerator.setFull(full);
            progressbarGenerator.setEmpty(empty);
            progressbarGenerator.setRight(right);
            progressbarGenerator.setFullPrefix(fullPrefix);
            progressbarGenerator.setEmptyPrefix(emptyPrefix);
            progressbarGenerator.setWidth(width);
            return progressbarGenerator;
        }
    }

}
