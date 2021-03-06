/**
 ********************************************************************************************************************
 *
 * Copyright (C) 8/21/12 by Manuel Palacio
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

package net.palacesoft.cngstation.server.dao

import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query

import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import com.google.appengine.api.datastore.PropertyProjection
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.KeyFactory

class CngDao {

    static String READABLE_STATION = "ReadableStation"

    private static DatastoreService dataStore = DatastoreServiceFactory.datastoreService

    static def findStationsByCountryName(String countryName) {
        def query = new Query(READABLE_STATION)

        query.addFilter("countryName", Query.FilterOperator.EQUAL, countryName)

        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asIterator(withChunkSize(400).prefetchSize(400))
    }

    static def findStationsByCity(String city) {
        def query = new Query(READABLE_STATION)

        query.addFilter("city", Query.FilterOperator.EQUAL, city)

        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asIterator(withChunkSize(200).prefetchSize(200))
    }

    static def findCountries() {

        def query = new Query("Country")
        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asList(withDefaults())

    }

    static def findCities(String countryName) {
        Query query = new Query(READABLE_STATION);
        query.addProjection(new PropertyProjection("city", String.class));
        query.addFilter("countryName", Query.FilterOperator.EQUAL, countryName)

        PreparedQuery preparedQuery = dataStore.prepare(query)

        def results = preparedQuery.asList(withChunkSize(200).prefetchSize(200))

        results.collectAll(new HashSet(), {it.getProperty("city")})
    }

    static def findStationById(String id) {
        Query query = new Query(READABLE_STATION, KeyFactory.createKey(READABLE_STATION, Long.parseLong(id)))

        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asSingleEntity()
    }

    static def addCountry(String countryName) {

        Query query = new Query("Country")
        query.addFilter("countryName", Query.FilterOperator.EQUAL, countryName)

        def country = dataStore.prepare(query).asSingleEntity()

        if (!country) {
            country = new Entity("Country")
            country.setProperty("countryName", countryName)
            dataStore.put country
        }
    }
}
