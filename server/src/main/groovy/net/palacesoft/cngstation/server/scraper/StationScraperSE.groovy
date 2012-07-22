package net.palacesoft.cngstation.server.scraper

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage

class StationScraperSE {

    def COUNTRY = "SWEDEN"

    def URL = "http://www.gasbilen.se/Att-tanka-din-gasbil/Tankstallelista"


    List<Station> scrape() {
        def gasStations = []
        def webClient = new WebClient()
        webClient.javaScriptEnabled = false
        webClient.cssEnabled = false
        HtmlPage page = webClient.getPage(URL) //webclient "knows" how to use URLs correctly in GAE

        page.getElementById("tankstallelist").getHtmlElementsByTagName("table").each { table ->

            def wholeAddress = table.getRows()[0].getCells()[0].asText()
            def city = wholeAddress.split(",")[0].trim()
            def street = wholeAddress.split(",")[1].trim()
            def operatedBy = table.getRows()[1].getCells()[1].asText().trim()
            def openingHours = table.getRows()[2].getCells()[1].asText().trim()
            def payment = table.getRows()[3].getCells()[1].asText().trim()
            def price = table.getRows()[4].getCells()[1].asText().trim()
            def telephones = table.getRows()[5].getCells()[1].asText().trim().split(",")
            def results = []
            telephones.each {phone ->
                if (phone) {
                    def m = phone =~ /([\w\W]*\s)*(\d*[-\/][\d\s]*)/
                    results << m[0][2].replaceAll("\\D", "")
                }
            }

            String coordinates = table.getRows()[6].getCells()[1].asText().trim()
            String latitude = coordinates.split(",")[0].split(":")[1].trim()
            String longitude = coordinates.split(",")[1].split(":")[1].trim()

            Station station = new Station(city: city, street: street, operatedBy: operatedBy, openingHours: openingHours,
                    payment: payment, price: price, phoneNo: results.join(","), latitude: latitude, longitude: longitude,
                    country: COUNTRY)

            gasStations << station

        }


        return gasStations

    }

    @Override
    String toString() {
        return "Scraper ${COUNTRY} (${URL})"
    }
}
