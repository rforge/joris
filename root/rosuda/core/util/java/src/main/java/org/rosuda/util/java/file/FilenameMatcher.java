package org.rosuda.util.java.file;

public interface FilenameMatcher {

    String describe();

    boolean matches(String fileName);

    String perfectMatch();
}
