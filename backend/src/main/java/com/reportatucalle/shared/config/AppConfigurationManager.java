package com.reportatucalle.shared.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Pure Singleton Pattern for managing app configuration
 */
public final class AppConfigurationManager {
    private static volatile AppConfigurationManager instance;
    private final Map<String, String> configMap;

    private AppConfigurationManager() {
        this.configMap = new ConcurrentHashMap<>();
        // Default configs
        configMap.put("maxReportsPerUser", "50");
        configMap.put("cacheTimeout", "3600");
    }

    public static AppConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (AppConfigurationManager.class) {
                if (instance == null) {
                    instance = new AppConfigurationManager();
                }
            }
        }
        return instance;
    }

    public String getConfig(String key) {
        return configMap.get(key);
    }

    public void setConfig(String key, String value) {
        configMap.put(key, value);
    }
}
