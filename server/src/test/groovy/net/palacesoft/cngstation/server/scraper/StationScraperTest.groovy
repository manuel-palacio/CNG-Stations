package net.palacesoft.cngstation.server.scraper

import org.junit.Test

class StationScraperTest {

    @Test
    void scrapeSWE() {

        def stations = new StationScraperSWE().scrape()

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

        def stations = new StationScraperNOR().scrape()

        assert !stations.empty

        stations.each {
            assert it.city
            assert it.street
            assert it.latitude
            assert it.longitude
            assert it.price
        }
    }
}
