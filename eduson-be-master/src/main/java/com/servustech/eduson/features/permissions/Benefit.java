package com.servustech.eduson.features.permissions;

public interface Benefit {

  public String getName();

  public Long getId();

  public Float getAmount(Long periodId);

  public String getStripe();
  public void setStripe(String stripe);
  public String getPriceStripe();
  public void setPriceStripe(String stripe);
}