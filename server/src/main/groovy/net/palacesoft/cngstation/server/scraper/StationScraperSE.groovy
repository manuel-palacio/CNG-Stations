/**
 * *******************************************************************************************************************
 * <p/>
 * Copyright (C) 7/28/12 by Manuel Palacio
 * <p/>
 * **********************************************************************************************************************
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * <p/>
 * **********************************************************************************************************************
 */
package net.palacesoft.cngstation.server.scraper

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTable

class StationScraperSE {

    def COUNTRY_CODE = "SE"

    def URL = "http://www.gasbilen.se/Att-tanka-din-gasbil/Tankstallelista"


    List<Station> scrape() {
        def gasStations = []
        def webClient = new WebClient()
        webClient.javaScriptEnabled = false
        webClient.cssEnabled = false
        HtmlPage page = webClient.getPage(URL) //webclient "knows" how to use URLs correctly in GAE

        page.getElementById("tankstallelist").getHtmlElementsByTagName("table").each { HtmlTable table ->

            def wholeAddress = table.getRow(0).getCell(0).asText()
            def city = wholeAddress.split(",")[0].trim()
            def street = wholeAddress.split(",")[1].trim()
            def operatedBy = table.getRow(1).getCell(1).asText().trim()
            def openingHours = table.getRow(2).getCell(1).asText().trim()
            def payment = table.getRow(3).getCell(1).asText().trim()
            def price = table.getRow(4).getCell(1).asText().trim()
            def telephones = table.getRow(5).getCell(1).asText().trim().split(",")
            def phones = []
            telephones.each { phone ->
                if (phone) {
                    def m = phone =~ /([\w\W]*\s)*(\d*[-\/][\d\s]*)/
                    phones << m[0][2].replaceAll("\\D", "")
                }
            }

            String coordinates = table.getRow(6).getCell(1).asText().trim()
            String latitude = coordinates.split(",")[0].split(":")[1].trim()
            String longitude = coordinates.split(",")[1].split(":")[1].trim()

            if (latitude && longitude) {
                String id = longitude + latitude

                Station station = new Station(id: id, city: city, street: street, operatedBy: operatedBy, openingHours: openingHours,
                        payment: payment, price: price, phoneNo: phones.join(","), latitude: latitude, longitude: longitude,
                        countryCode: COUNTRY_CODE)

                gasStations << station
            }

        }


        return gasStations

    }

    @Override
    String toString() {
        return "Scraper ${COUNTRY_CODE} (${URL})"
    }
}
