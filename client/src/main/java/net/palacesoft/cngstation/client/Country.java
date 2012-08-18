/**
 * *******************************************************************************************************************
 * <p/>
 * Copyright (C) 7/28/12 by Manuel Palacio
 * <p/>
 * **********************************************************************************************************************
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * <p/>
 * **********************************************************************************************************************
 */
package net.palacesoft.cngstation.client;


import android.location.Address;

import java.util.Locale;

public enum Country {
    Sweden("Sweden", "SE", 62.0, 15.0),
    Norway("Norway", "NO", 62.0, 10.0),
    France("France", "FR", 46.0, 2.0),
    Germany("Germany", "DE", 51.0, 9.0),
    Spain("Spain", "ES", 40.0, -4.0),
    Austria("Austria", "AT", 47.3333, 13.3333),
    Italy("Italy", "IT", 42.8333, 12.8333),

    DEFAULT("","", 0.0, 0.0);


    private String countryCode;
    private double longitude;
    private double latitude;
    private String countryName;

    private Country(String countryName, String countryCode, double latitude, double longitude) {
        this.countryCode = countryCode;
        this.longitude = longitude;
        this.latitude = latitude;
        this.countryName = countryName;
    }

    public String getCountryCode(){
        return countryCode;
    }


    public Address getAddress() {
        Address address = new Address(Locale.ENGLISH);
        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address.setCountryCode(countryCode);
        address.setCountryName(countryName);
        return address;
    }
}
