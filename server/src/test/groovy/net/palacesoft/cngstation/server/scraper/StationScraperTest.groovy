package net.palacesoft.cngstation.server.scraper

import org.junit.Test

class StationScraperTest {

    @Test
    void scrapeSE(){

        def stations = new StationScraperSE().scrape()

        assert !stations.empty

        stations.each {
            assert it.city
            assert it.street
            assert it.latitude
            assert it.longitude
            assert it.price
            assert it.payment
        }
    }
}
