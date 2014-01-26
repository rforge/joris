package org.rosuda.util.java.file;

import java.io.File;

public class EqualsFilenameMatcher implements FileMatcher {

    private final String equalsFileName;

    protected EqualsFilenameMatcher(final String equalsFileName) {
        this.equalsFileName = equalsFileName;
    }

    @Override
    public String describe() {
        return new StringBuilder("equals \"").append(equalsFileName).append("\"").toString();
    }

    @Override
    public boolean matches(File file) {
        return file.getName().equals(equalsFileName);
    }

    @Override
    public String perfectMatch() {
        return equalsFileName;
    }
}
