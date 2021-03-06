package net.palacesoft.cngstation.server.scraper

import org.junit.Test

class StationScraperIT {

    @Test
    void scrapeSE() {
        def scraper = ScraperFactory.newInstance(countryCode: "SE", countryName: "Sweden")
        def stations = []
        List<String> pages = scraper.getUrlsToScrape("http://www.gasbilen.se/Att-tanka-din-gasbil/Tankstallelista")
        pages.each {
            stations.addAll scraper.scrapePage(it)
        }
        print stations.size()

        assert !stations.empty

        stations.each {
            assert it.city
            assert it.street
            assert it.latitude
            assert it.longitude
            assert it.price
            assert it.countryCode
            assert it.countryName
        }
    }

    @Test
    void scrapeAT() {
        def scraper = ScraperFactory.newInstance(countryCode: "AT", countryName: "Austria")
        def stations = []
        List<String> pages = scraper.getUrlsToScrape("3")
        pages.each {
            stations.addAll scraper.scrapePage(it)
        }
        print stations.size()

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
    void scrapeCH() {
        def scraper = ScraperFactory.newInstance(countryCode: "CH", countryName: "Switzerland")
        def stations = []
        List<String> pages = scraper.getUrlsToScrape("2")
        pages.each {
            stations.addAll scraper.scrapePage(it)
        }
        print stations.size()

        assert !stations.empty
        assert stations.size() > 10

        stations.each {
            assert it.city
            assert it.street
            assert it.latitude
            assert it.longitude
            assert it.price
        }
    }

    @Test
    void scrapeFI() {
        def scraper = ScraperFactory.newInstance(countryCode: "FI", countryName: "Finland")
        def stations = []
        List<String> pages = scraper.getUrlsToScrape("20")
        pages.each {
            stations.addAll scraper.scrapePage(it)
        }
        print stations.size()

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
    void scrapeBE() {
        def scraper = ScraperFactory.newInstance(countryCode: "BE", countryName: "Belgium")
        def stations = []
        List<String> pages = scraper.getUrlsToScrape("6")
        pages.each {
            stations.addAll scraper.scrapePage(it)
        }
        print stations.size()

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
