package net.palacesoft.stationserver.scraper.scraper

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

import net.palacesoft.stationserver.scraper.Station

class GasStationScraperSE {

    def COUNTRY = "SWEDEN"

    def URL = "http://www.gasbilen.se/Att-tanka-din-gasbil/Tankstallelista"


    List<Station> scrape() {
        def gasStations = []
        def webClient = new WebClient()
        webClient.javaScriptEnabled = false
        webClient.cssEnabled = false
        HtmlPage page = webClient.getPage(URL)

        page.getElementById("tankstallelist").getHtmlElementsByTagName("table").each {

            def wholeAddress = it.getRows()[0].getCells()[0].asText()
            def city = wholeAddress.split(",")[0].trim()
            def street = wholeAddress.split(",")[1].trim()
            def operatedBy = it.getRows()[1].getCells()[1].asText().trim()
            def openingHours = it.getRows()[2].getCells()[1].asText().trim()
            def payment = it.getRows()[3].getCells()[1].asText().trim()
            def price = it.getRows()[4].getCells()[1].asText().trim()
            def telephones = it.getRows()[5].getCells()[1].asText().trim().split(",")
            def results = []
            telephones.each {
                if (it) {
                    def m = it =~ /([\w\W]*\s)*(\d*[-\/][\d\s]*)/
                    results << m[0][2].replaceAll("\\D", "")
                }
            }

            String coordinates = it.getRows()[6].getCells()[1].asText().trim()
            String latitude = coordinates.split(",")[0].split(":")[1].trim()
            String longitude = coordinates.split(",")[1].split(":")[1].trim()

            Station station = new Station(city: city, street: street, operatedBy: operatedBy, openingHours: openingHours,
                    payment: payment, price: price, phoneNo: results.join(","), latitude: latitude, longitude: longitude,
                    country: COUNTRY)

            gasStations << station

        }


        return gasStations

    }

}
