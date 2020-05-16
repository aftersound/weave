package io.aftersound.weave.common;

public class Keys {
    public static final Key<Results> SUB_RESULTS = Key.as("SUB_RESULTS", Results.class);
    public static final Key<Results> SUB_RESULTS_SUCCEEDED = Key.as("SUB_RESULTS_SUCCEEDED", Results.class);
    public static final Key<Results> SUB_RESULTS_FAILED = Key.as("SUB_RESULTS_FAILED", Results.class);

    public static final Key<ReturnInfo> RETURN_INFO = Key.as("ReturnInfo", ReturnInfo.class);
    public static final Key<ReturnInfos> RETURN_INFOS = Key.as("ReturnInfos", ReturnInfos.class);

}
