package org.rosuda.util.java.file;

import java.io.File;

public class RegexpFileAndPathMatcher implements FileMatcher {

    private final String regexp;

    protected RegexpFileAndPathMatcher(final String regexp) {
        this.regexp = regexp;
    }

    @Override
    public String describe() {
        return new StringBuilder("matchRegexp \"").append(regexp).append("\"").toString();
    }

    @Override
    public boolean matches(File file) {
        return file.getAbsolutePath().matches(regexp);
    }

    @Override
    public String perfectMatch() {
        return null;
    }
}
