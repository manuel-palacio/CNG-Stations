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
}
