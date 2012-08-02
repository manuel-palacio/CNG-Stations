/**
 ********************************************************************************************************************
 *
 * Copyright (C) 8/3/12 by Manuel Palacio
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 */
package net.palacesoft.cngstation.server.scraper

import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlTable
import com.gargoylesoftware.htmlunit.WebClient


class MetanoAutoScraper {

    protected WebClient webClient

    MetanoAutoScraper() {
        webClient = new WebClient()
        webClient.javaScriptEnabled = false
        webClient.cssEnabled = false
    }

    protected List<Station> scrapePage(HtmlPage page) {
        def gasStations = []
        List<?> links = page.getByXPath("//a[@title='Dettagli']")

        links.each { HtmlAnchor link ->
            boolean isOpen = link.getParentNode().getParentNode().getCell(8).asText() == "Aperto"
            if (isOpen) {
                HtmlPage infoPage = link.click()
                HtmlTable infoTable = infoPage.getByXPath("//table[@class='forumline']").get(0)
                String street = infoTable.getRow(1).getCell(1).asText().split("-")[0].trim()
                String cityText = infoTable.getRow(1).getCell(1).asText()
                String[] citySplit = cityText.split("-")
                String city = citySplit[citySplit.length - 1].trim().toLowerCase().capitalize()
                def phoneNos = infoTable.getRow(2).getCell(1).asText().trim().split("-")
                String coordinates = infoTable.getRow(3).getCell(1).asText().trim()

                def latitudeRegExp = coordinates.split("-")[0] =~ /(lat.)\s(.*)/
                def longitudeRegExp = coordinates.split("-")[1] =~ /(long.)\s(.*)/
                String latitude = latitudeRegExp[0][2]
                String longitude = longitudeRegExp[0][2]

                String price = infoTable.getRow(6).getCell(1).asText().replaceAll("\\(.*\\)", "").trim()
                String operatedBy = infoTable.getRow(7).getCell(1).asText().trim()

                String openingHours = infoTable.getRow(5).getCell(1).asText().replaceAll("\\D[^\\d{2}]", "").trim()

                if (latitude && longitude) {
                    String id = longitude + latitude

                    Station station = new Station(id: id, street: street, city: city, phoneNo: phoneNos.join(","), latitude: latitude,
                            longitude: longitude, price: price, operatedBy: operatedBy, openingHours: openingHours,
                            countryCode: COUNTRY_CODE, countryName: COUNTRY_NAME)

                    gasStations << station
                }
            }
        }

        return gasStations
    }
}
