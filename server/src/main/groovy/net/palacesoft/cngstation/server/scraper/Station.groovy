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
import groovyx.gaelyk.datastore.Entity


@Entity(unindexed=false)
class Station implements Serializable{
    @Key String id
    String countryCode
    String countryName;
    String city
    @Unindexed String street
    @Unindexed String operatedBy
    @Unindexed String openingHours
    @Unindexed String price
    @Unindexed String latitude
    @Unindexed String longitude
    @Unindexed String payment
    @Unindexed String phoneNo
    @Unindexed String web
    @Unindexed String email

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Station station = (Station) o

        if (id != station.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

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
