package org.rosuda.util.process;

import java.util.Properties;

public class ExtendedSystemContext extends ShellContext {

    public ExtendedSystemContext(final Properties properties) {
        //1. copy everything from system to own propreties
        //2. use customization init (Properties constructor) to override non-present properties
    }
    
}
