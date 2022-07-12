package com.servustech.eduson.features.account;

public enum AccountStatus {
    ACTIVE, BANNED, LOCKED, INACTIVE, STARTED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isBanned() {
        return this == BANNED;
    }

    public boolean isLocked() {
        return this == LOCKED;
    }

    public boolean isRegisteredOnly() {
        return this == STARTED;
    }

    public boolean isInactive(){return this == INACTIVE;}
}
