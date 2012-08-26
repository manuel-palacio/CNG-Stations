/**
 ********************************************************************************************************************
 *
 * Copyright (C) 8/16/12 by Manuel Palacio
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

class ScraperFactory {

    public static Scraper newInstance(def params) {
        if (params.countryCode == "SE") {
            return new StationScraperSE(countryCode: params.countryCode, countryName: params.countryName)
        }

        if (params.countryCode == "IT" || params.countryCode == "FR") {
            return new MetanoAutoScraper(openCellNumber: 7, countryCode: params.countryCode, countryName: params.countryName)
        }


        return new MetanoAutoScraper(openCellNumber: 6, countryCode: params.countryCode, countryName: params.countryName)
    }

}
