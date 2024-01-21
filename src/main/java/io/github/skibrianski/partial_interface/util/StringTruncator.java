package io.github.skibrianski.partial_interface.util;

// yeah, this is overkill, but ¯\_(ツ)_/¯
public class StringTruncator {

    private String string;
    private int truncations = 0;

    public StringTruncator(String string) {
        this.string = string;
    }

    public StringTruncator truncateOnce(String suffix) {
        removeTrailing(suffix);
        return this;
    }
    public StringTruncator truncateAll(String suffix) {
        while (removeTrailing(suffix));
        return this;
    }

    public String value() {
        return string;
    }

    public int truncationCount() {
        return truncations;
    }

    private boolean removeTrailing(String suffix) {
        if (string.endsWith(suffix)) {
            string = string.substring(0, string.length() - suffix.length());
            truncations++;
            return true;
        }
        return false;
    }
}
