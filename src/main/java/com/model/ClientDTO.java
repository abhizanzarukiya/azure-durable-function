package com.model;

import java.io.Serializable;

public class ClientDTO implements Serializable{
    private String name;
    private String address;
    private String kycInfo;
    private Integer id;


    public Integer getId() {
        return id;
    } 

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKycInfo() {
        return kycInfo;
    }

    public void setKycInfo(String kycInfo) {
        this.kycInfo = kycInfo;
    }

    public String toString() {
        return "Name: " + name + ", Address: " + address + ", KYC Info: " + kycInfo;
    }
}