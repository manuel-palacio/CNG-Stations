package net.palacesoft.cngstation.server.scraper

import geb.*

class StationScraperSE {

    def COUNTRY = "SWEDEN"

    def URL = "http://www.gasbilen.se/Att-tanka-din-gasbil/Tankstallelista"

    List<Station> scrape() {

        def gasStations = []

        Browser.drive {
            go URL

            $("table").each {  station ->

                def wholeAddress = station.find("tr", 0).text()
                def city = wholeAddress.split(",")[0].trim()
                def street = wholeAddress.split(",")[1].trim()
                def operatedBy = station.find("tr", 1).find("td", 1).text().trim()
                def openingHours = station.find("tr", 2).find("td", 1).text().trim()
                def payment = station.find("tr", 3).find("td", 1).text().trim()
                def price = station.find("tr", 4).find("td", 1).text().trim()
                def telephones = station.find("tr", 5).find("td", 1).text().trim().split(",")
                def results = []
                telephones.each { phone ->
                    if (phone) {
                        def m = phone =~ /([\w\W]*\s)*(\d*[-\/][\d\s]*)/
                        results << m[0][2].replaceAll("\\D", "")
                    }
                }

                String coordinates = station.find("tr", 6).find("td", 1).text().trim()
                String latitude = coordinates.split(",")[0].split(":")[1].trim()
                String longitude = coordinates.split(",")[1].split(":")[1].trim()


                gasStations << new Station(city: city, street: street, operatedBy: operatedBy, openingHours: openingHours,
                        payment: payment, price: price, phoneNo: results.join(","), latitude: latitude,
                        longitude: longitude,
                        country: COUNTRY)
            }
        }

        return gasStations
    }


    @Override
    String toString() {
        return "Scraper ${COUNTRY} (${URL})"
    }
}
