package org.rosuda.util.java.file;

import java.io.File;

public interface FileMatcher {

    String describe();

    boolean matches(File fileName);

    String perfectMatch();
}
