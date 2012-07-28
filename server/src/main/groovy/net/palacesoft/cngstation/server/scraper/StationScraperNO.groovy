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
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTable

class StationScraperNO {

    def COUNTRY_CODE = "NO"

    def URL = "http://www.metanoauto.com/modules.php?name=Distributori&op=DistUELista&p=21"


    List<Station> scrape() {
        def gasStations = []
        def webClient = new WebClient()
        webClient.javaScriptEnabled = false
        webClient.cssEnabled = false
        HtmlPage page = webClient.getPage(URL) //webclient "knows" how to use URLs correctly in GAE

        List<?> links = page.getByXPath("//a[@title='Dettagli']")

        links.each { HtmlAnchor link ->
            HtmlPage infoPage = link.click()
            HtmlTable infoTable = infoPage.getByXPath("//table[@class='forumline']").get(0)
            String street = infoTable.getRow(1).getCell(1).asText().split("-")[0].trim()
            String cityText = infoTable.getRow(1).getCell(1).asText()
            String [] citySplit = cityText.split("-")
            String city = citySplit[citySplit.length-1].trim().toLowerCase().capitalize()
            String phoneNo = infoTable.getRow(2).getCell(1).asText().trim()
            String coordinates = infoTable.getRow(3).getCell(1).asText().trim()

            def latitudeRegExp = coordinates.split("-")[0] =~ /(lat.)\s(.*)/
            def longitudeRegExp = coordinates.split("-")[1] =~ /(long.)\s(.*)/
            String latitude = latitudeRegExp[0][2]
            String longitude = longitudeRegExp[0][2]

            String price = infoTable.getRow(6).getCell(1).asText().replaceAll("\\(.*\\)", "").trim()
            String operatedBy = infoTable.getRow(7).getCell(1).asText().trim()

            String openingHours = infoTable.getRow(5).getCell(1).asText().replaceAll("\\D[^\\d{2}]", "").trim()

            if (latitude && longitude) {
                Station station = new Station(street: street, city: city, phoneNo: phoneNo, latitude: latitude,
                        longitude: longitude, price: price, operatedBy: operatedBy, openingHours: openingHours, countryCode: COUNTRY_CODE)

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
