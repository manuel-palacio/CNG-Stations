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

class StationScraperFR extends MetanoAutoScraper {

    def static COUNTRY_CODE = "FR"
    def static COUNTRY_NAME = "France"
    def static  OPEN_CELL_NO = 9
    def URL = "http://www.metanoauto.com/modules.php?name=Distributori&op=DistUELista&p=8"

    StationScraperFR() {
        super(COUNTRY_CODE, COUNTRY_NAME, OPEN_CELL_NO)
    }

    @Override
    Set<Station> scrape() {
        HtmlPage page = webClient.getPage(URL)
        scrapePage(page)
    }

    @Override
    String toString() {
        return "Scraper ${COUNTRY_CODE} (${URL})"
    }
}
