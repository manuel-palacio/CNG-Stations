package net.palacesoft.cngstation.server.scraper

import org.junit.Test

class StationScraperTest {

    @Test
    void scrapeSE() {

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
        void scrapeFR() {

            def stations = new StationScraperFR().scrape()

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
    void scrapeNO() {

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

    @Test
    void scrapeDE() {

        def stations = new StationScraperDE().scrape()

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
