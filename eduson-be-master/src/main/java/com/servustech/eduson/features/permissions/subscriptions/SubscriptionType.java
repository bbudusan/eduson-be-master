package com.servustech.eduson.features.permissions.subscriptions;

public enum SubscriptionType {
    RETAIL, CORPORATE, PRIVATE;

    public boolean isRetail() {
        return this == RETAIL;
    }

    public boolean isCorporate() {
        return this == CORPORATE;
    }

    public boolean isPrivate() {
        return this == PRIVATE;
    }

}
