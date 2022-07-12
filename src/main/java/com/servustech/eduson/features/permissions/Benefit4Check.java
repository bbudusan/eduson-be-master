package com.servustech.eduson.features.permissions;

public interface Benefit4Check {

  public Long getId();

  public boolean isOrHas(Benefit4Check benefit, PermissionsService ps);

}