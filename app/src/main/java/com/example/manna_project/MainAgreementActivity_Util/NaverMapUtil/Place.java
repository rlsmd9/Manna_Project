package com.example.manna_project.MainAgreementActivity_Util.NaverMapUtil;

import com.naver.maps.geometry.LatLng;

public class Place {
    String name;
    String load_address;
    String jibun_address;
    LatLng latLng;

    public Place(String name, String load_address, String jibun_address, LatLng latLng) {
        this.name = name;
        this.load_address = load_address;
        this.jibun_address = jibun_address;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoad_address() {
        return load_address;
    }

    public void setLoad_address(String load_address) {
        this.load_address = load_address;
    }

    public String getJibun_address() {
        return jibun_address;
    }

    public void setJibun_address(String jibun_address) {
        this.jibun_address = jibun_address;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}