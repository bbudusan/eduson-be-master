package com.servustech.eduson.security.payload;

public enum StreamUsageAction {
    download, display;
    public boolean isDownload() {
        return this == download;
    }

    public boolean isDisplay() {
        return this == display;
    }

}
