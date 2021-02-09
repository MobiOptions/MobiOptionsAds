package com.lib.mobioptionsads.data.local;

import java.util.Map;

public interface ILocalData {

    void setAppToken(String appToken);

    String getAppToken();

    void setLaunchedFirstTime(boolean launchedFirstTime);

    boolean getLaunchedFirstTime();


    void setAppLaunchedAt(Long launchedAt);

    Long getAppLaunchedAt();


    void setNumTimesLaunched(int numTimesLaunched);

    int getNumTimesLaunched();


    void setLastProvidersShown(Map<String, Boolean> map);

    Map<String, Boolean> getLastProvidersShown();

    void setProjectId(int projectId);

    int getProjectId();
}
