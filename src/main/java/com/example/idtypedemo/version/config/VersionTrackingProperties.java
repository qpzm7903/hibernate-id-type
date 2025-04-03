package com.example.idtypedemo.version.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "version.tracking")
public class VersionTrackingProperties {
    private boolean enabled = true;
    private List<String> businessPackages = new ArrayList<>();
    private List<String> excludePackages = new ArrayList<>();
    private int maxStackDepth = 10;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getBusinessPackages() {
        return businessPackages;
    }

    public void setBusinessPackages(List<String> businessPackages) {
        this.businessPackages = businessPackages;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
    }

    public int getMaxStackDepth() {
        return maxStackDepth;
    }

    public void setMaxStackDepth(int maxStackDepth) {
        this.maxStackDepth = maxStackDepth;
    }
} 