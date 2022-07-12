package com.servustech.eduson.security.payload;

public enum StreamType {
    live, vod;

    public boolean isLive() {
        return this == live;
    }

    public boolean isVod() {
        return this == vod;
    }

}
