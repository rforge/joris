package org.rosuda.util.java.file;

public class RegexpFilenameMatcher implements FilenameMatcher {

    private final String regexp;

    protected RegexpFilenameMatcher(final String regexp) {
        this.regexp = regexp;
    }

    @Override
    public String describe() {
        return new StringBuilder("matchRegexp \"").append(regexp).append("\"").toString();
    }

    @Override
    public boolean matches(String fileName) {
        return fileName.matches(regexp);
    }

    @Override
    public String perfectMatch() {
        return null;
    }
}
