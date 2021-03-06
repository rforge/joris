package org.rosuda.util.java.file;

import java.io.File;

public class RegexpFilenameMatcher implements FileMatcher {

    private final String regexp;

    protected RegexpFilenameMatcher(final String regexp) {
        this.regexp = regexp;
    }

    @Override
    public String describe() {
        return new StringBuilder("matchRegexp \"").append(regexp).append("\"").toString();
    }

    @Override
    public boolean matches(File file) {
        return file.getName().matches(regexp);
    }

    @Override
    public String perfectMatch() {
        return null;
    }
}
