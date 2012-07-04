package net.palacesoft.stationserver.scraper

import org.junit.Test
import net.palacesoft.stationserver.scraper.scraper.GasStationScraperSE


class GasStationScraperTest {

    @Test
    void scrapeSE(){
        GasStationScraperSE gasStationScraper = new GasStationScraperSE()

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
