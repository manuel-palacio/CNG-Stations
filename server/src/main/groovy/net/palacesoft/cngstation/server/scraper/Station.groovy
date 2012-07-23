package net.palacesoft.cngstation.server.scraper

import groovyx.gaelyk.datastore.Unindexed


class Station {
    String countryCode
    String city
    String street
    @Unindexed String operatedBy
    String openingHours
    @Unindexed String price
    String latitude
    String longitude
    @Unindexed String payment
    @Unindexed String phoneNo
    @Unindexed String web
    @Unindexed String email

    @Override
    public String toString() {
        return "Station{" +
                "countryCode='" + countryCode + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", operatedBy='" + operatedBy + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", price='" + price + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", payment='" + payment + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", web='" + web + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
