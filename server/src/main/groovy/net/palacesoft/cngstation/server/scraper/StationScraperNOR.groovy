package net.palacesoft.cngstation.server.scraper

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlTable

class StationScraperNOR {

    def COUNTRY_CODE = "NOR"

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
            String city = infoTable.getRow(1).getCell(1).asText().split("-")[1].trim().toLowerCase().capitalize()
            String phoneNo = infoTable.getRow(2).getCell(1).asText().trim()
            String coordinates = infoTable.getRow(3).getCell(1).asText().trim()

            def latitudeRegExp = coordinates.split("-")[0] =~ /(lat.)\s(.*)/
            def longitudeRegExp = coordinates.split("-")[1] =~ /(long.)\s(.*)/
            String latitude = latitudeRegExp[0][2]
            String longitude = longitudeRegExp[0][2]

            String price = infoTable.getRow(6).getCell(1).asText().replaceAll("\\(.*\\)", "").trim()
            String operatedBy = infoTable.getRow(7).getCell(1).asText().trim()

            String openingHours = infoTable.getRow(5).getCell(1).asText().replaceAll("\\D[^\\d{2}]", "").trim()

            Station station = new Station(street: street, city: city, phoneNo: phoneNo, latitude: latitude,
                    longitude: longitude, price: price, operatedBy: operatedBy, openingHours: openingHours, countryCode: COUNTRY_CODE)

            gasStations << station
        }



        return gasStations

    }

    @Override
    String toString() {
        return "Scraper ${COUNTRY_CODE} (${URL})"
    }
}
