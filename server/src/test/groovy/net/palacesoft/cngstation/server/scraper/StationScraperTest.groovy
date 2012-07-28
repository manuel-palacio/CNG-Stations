package net.palacesoft.cngstation.server.scraper

import org.junit.Test

class StationScraperTest {

    @Test
    void scrapeSWE() {

        def stations = new StationScraperSE().scrape()

        assert !stations.empty

        stations.each {
            assert it.city
            assert it.street
            assert it.latitude
            assert it.longitude
            assert it.price
        }
    }

    @Test
    void scrapeNOR() {

        def stations = new StationScraperNO().scrape()

        assert !stations.empty

        stations.each {
            assert it.city
            assert !it.city.isNumber()
            assert it.street
            assert it.latitude
            assert it.longitude
            assert it.price
        }
    }
}
