package org.rosuda.util.java.file;

public class EqualsFilenameMatcher implements FilenameMatcher {

    private final String equalsFileName;

    protected EqualsFilenameMatcher(final String equalsFileName) {
        this.equalsFileName = equalsFileName;
    }

    @Override
    public String describe() {
        return new StringBuilder("equals \"").append(equalsFileName).append("\"").toString();
    }

    @Override
    public boolean matches(String fileName) {
        return equalsFileName.equals(fileName);
    }

    @Override
    public String perfectMatch() {
        return equalsFileName;
    }
}
