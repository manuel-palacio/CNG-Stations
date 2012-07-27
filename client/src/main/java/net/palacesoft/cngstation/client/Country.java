package net.palacesoft.cngstation.client;


import android.location.Address;

import java.util.Locale;

public enum Country {
    SWEDEN("SE", 62.0, 15.0),

    DEFAULT("",0.0,0.0);


    private String countryCode;
    private double longitude;
    private double latitude;

    private Country(String countryCode, double latitude, double longitude) {
        this.countryCode = countryCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    public Address getAddress() {
        Address address = new Address(Locale.getDefault());
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setCountryCode(countryCode);
        return address;
    }

    public static Country findAddress(String countryCode) {
        if (countryCode.equalsIgnoreCase("SE")) {
            return SWEDEN;
        }

        return DEFAULT;
    }
}
