package org.rosuda.util.service.rserve;

public interface RServeService {

    public static final String R_SERVE_ARGS = "--no-save --slave";

    String[] getRuntimeArgs();
}
