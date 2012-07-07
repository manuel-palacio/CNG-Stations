package net.palacesoft.cngstation.server.scraper

import org.junit.Test

class StationScraperTest {

    @Test
    void scrapeSE(){
        StationScraperSE gasStationScraper = new StationScraperSE()

        def stations = gasStationScraper.scrape()

        assert !stations.empty

        stations.each {
            assert it.city
            assert it.street
            assert it.latitude
            assert it.longitude
        }
    }
}
