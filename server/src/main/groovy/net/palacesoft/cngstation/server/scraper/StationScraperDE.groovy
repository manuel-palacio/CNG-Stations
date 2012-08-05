/**
 ********************************************************************************************************************
 *
 * Copyright (C) 8/4/12 by Manuel Palacio
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

class StationScraperDE extends MetanoAutoScraper {

    def static COUNTRY_CODE = "DE"
    def static COUNTRY_NAME = "Germany"
    def static  OPEN_CELL_NO = 8
    def static URL = "http://www.metanoauto.com/modules.php?name=Distributori&op=DistUELista&p=9"

    StationScraperDE() {
        super(COUNTRY_CODE, COUNTRY_NAME)
    }

    def anchor = {HtmlPage nextPage -> nextPage.getAnchorByText("Successiva >>").click()}

    private def getAndSavePage(List<HtmlPage> pages, HtmlPage htmlPage) {

        pages << htmlPage
        getAndSavePage(pages, anchor(htmlPage))
    }

    @Override
    Set<Station> scrape() {
        Set<Station> stations = []

        HtmlPage firstPage = webClient.getPage(URL)

        List<HtmlPage> pages = gatherPages(firstPage)

        pages.each { page ->
            stations.addAll(scrapePage(page, OPEN_CELL_NO))
        }

        return stations
    }

    @Override
    String toString() {
        return "Scraper ${COUNTRY_CODE} (${URL})"
    }
}
