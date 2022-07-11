package com.servustech.eduson.features.account;

public class SmartBill {
    public String name;
    public String vatCode;
    public String cif;
    public String seriesname;
    public String number;
    public String companyVatCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(String vatCode) {
        this.vatCode = vatCode;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getSeriesname() {
        return seriesname;
    }

    public void setSeriesname(String seriesname) {
        this.seriesname = seriesname;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCompanyVatCode() {
        return companyVatCode;
    }

    public void setCompanyVatCode(String companyVatCode) {
        this.companyVatCode = companyVatCode;
    }
}
