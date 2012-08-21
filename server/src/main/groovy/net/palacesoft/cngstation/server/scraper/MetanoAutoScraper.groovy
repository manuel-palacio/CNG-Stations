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

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTable
import net.palacesoft.cngstation.server.model.Country
import net.palacesoft.cngstation.server.model.Station

public class MetanoAutoScraper implements Scraper {

    private int openCellNumber

    private String baseUrl = "http://www.metanoauto.com/modules.php?name=Distributori&op=DistUELista&p=%"
    private String countryCode, countryName

    def webClient = new WebClient()

    MetanoAutoScraper(Map params) {

        this.openCellNumber = params.openCellNumber
        this.countryCode = params.countryCode
        this.countryName = params.countryName

        webClient.javaScriptEnabled = false
        webClient.cssEnabled = false
    }


    private void getPage(List urls, HtmlPage htmlPage) {

        urls << htmlPage.getUrl().toString()
        getPage(urls, htmlPage.getAnchorByText("Successiva >>").click())
    }


    public List<String> getUrlsToScrape(String baseUrl) {

        if (!baseUrl) {
            throw new IllegalArgumentException("Base URL cannot be null")
        }

        String url = this.baseUrl.replace("%", baseUrl)
        List urlList = []

        HtmlPage firstPage = webClient.getPage(url)

        try {
            getPage(urlList, firstPage)
        } catch (Exception e) {
            //ignore
        }

        return urlList

    }


    public Set<Station> scrapePage(String url) {
        if (!url) {
            throw new IllegalArgumentException("Url cannot be null")
        }

        Set gasStations = []
        List<?> links = webClient.getPage(url).getByXPath("//a[@title='Dettagli']")

        links.each { HtmlAnchor stationLink ->
            boolean isOpen = stationLink.getParentNode().getParentNode().getCell(openCellNumber).asText() == "Aperto"
            if (isOpen) {
                HtmlPage stationPage = stationLink.click()
                HtmlTable infoTable = stationPage.getByXPath("//table[@class='forumline']").get(0)
                String street = infoTable.getRow(1).getCell(1).asText().split("-")[0].trim()
                String cityText = infoTable.getRow(1).getCell(1).asText()
                String[] citySplit = cityText.split("-")
                String city = citySplit[citySplit.length - 1].trim().toLowerCase().capitalize()
                if (city.contains("(")) {
                    city = city.substring(0, city.indexOf("(")).trim()
                }

                def phoneNos = infoTable.getRow(2).getCell(1).asText().trim().split("-")
                String coordinates = infoTable.getRow(3).getCell(1).asText().trim().replaceFirst("-", "&")

                def latitudeRegExp = coordinates.split("&")[0] =~ /(lat.)\s(.*)/
                def longitudeRegExp = coordinates.split("&")[1] =~ /(long.)\s(.*)/
                String latitude = latitudeRegExp[0][2].trim()
                String longitude = longitudeRegExp[0][2].trim()

                String price = infoTable.getRow(6).getCell(1).asText().replaceAll("\\(.*\\)", "").trim()
                String operatedBy = infoTable.getRow(7).getCell(1).asText().trim()

                String openingHours = infoTable.getRow(5).getCell(1).asText().replaceAll("\\D[^\\d{2}]", "").trim()

                if (latitude && longitude) {

                    Country country = Country.values().find {it.countryCode == countryCode}
                    Station station = new Station.Builder(latitude, longitude, city, country).withPrice(price).
                            withPhoneNo(phoneNos.join(",")).withStreet(street).withOperatedBy(operatedBy).withOpeningHours(openingHours).build()

                    gasStations << station
                }
            }
        }

        return gasStations
    }
}
