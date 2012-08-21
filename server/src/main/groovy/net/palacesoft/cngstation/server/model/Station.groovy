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
package net.palacesoft.cngstation.server.model

import groovyx.gaelyk.datastore.Entity
import groovyx.gaelyk.datastore.Indexed
import groovyx.gaelyk.datastore.Key

@Entity
class Station implements Serializable {
    @Key String id
    String countryCode
    @Indexed String countryName;
    @Indexed String city
    String street
    String operatedBy
    String openingHours
    String price
    String latitude
    String longitude
    String payment
    String phoneNo
    String web
    String email
    String type = StationType.CNG.name()
    @Indexed Date dateUpdated = new Date()


    private Station() {}

    static class Builder {

        private Station station = new Station()

        Builder(String latitude, String longitude, String city, Country country) {
            station.latitude = latitude
            station.longitude = longitude
            station.city = city
            station.countryName = country.name()
            station.countryCode = country.countryCode
            station.id = longitude + latitude
        }

        Builder withStreet(String street) {
            station.street = street
            return this
        }

        Builder withPhoneNo(String phoneNo) {
            station.phoneNo = phoneNo
            return this
        }

        Builder withPrice(String price) {
            station.price = price
            return this
        }

        Builder withPayment(String payment) {
            station.payment = payment
            return this
        }

        Builder withWeb(String web) {
            station.web = web
            return this
        }

        Builder withEmail(String email) {
            station.email = email
            return this
        }

        Builder withOperatedBy(String operatedBy) {
            station.operatedBy = operatedBy
            return this
        }

        Builder withOpeningHours(String openingHours) {
            station.openingHours = openingHours
            return this
        }


        Station build() {
            return station
        }
    }

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
