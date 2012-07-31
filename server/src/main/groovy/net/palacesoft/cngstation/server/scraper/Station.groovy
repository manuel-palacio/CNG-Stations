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

import groovyx.gaelyk.datastore.Unindexed
import groovyx.gaelyk.datastore.Key


class Station {
    @Key String id
    String countryCode
    String countryName;
    String city
    String street
    @Unindexed String operatedBy
    String openingHours
    @Unindexed String price
    String latitude
    String longitude
    @Unindexed String payment
    @Unindexed String phoneNo
    @Unindexed String web
    @Unindexed String email

    @Override
    public String toString() {
        return "Station{" +
                "countryCode='" + countryCode + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", operatedBy='" + operatedBy + '\'' +
                ", openingHours='" + openingHours + '\'' +
                ", price='" + price + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", payment='" + payment + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", web='" + web + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
