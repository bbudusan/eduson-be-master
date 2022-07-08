package com.servustech.eduson.features.permissions;

public enum ProductType {
    SUBSCRIPTION, MODULE, WEBINAR, COURSE, LIVE_EVENT;

    public boolean isSubscription() {
        return this == SUBSCRIPTION;
    }

    public boolean isModule() {
        return this == MODULE;
    }

    public boolean isWebinar() {
        return this == WEBINAR;
    }

    public boolean isCourse() {
        return this == COURSE;
    }

    public boolean isLiveEvent() {
        return this == LIVE_EVENT;
    }
}
