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

package net.palacesoft.cngstation.server.persistence

import com.google.appengine.api.datastore.PreparedQuery
import com.google.appengine.api.datastore.Query

import static com.google.appengine.api.datastore.FetchOptions.Builder.*
import com.google.appengine.api.datastore.PropertyProjection
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.DatastoreService

class CngDao {

    private static DatastoreService dataStore = DatastoreServiceFactory.datastoreService

    static def findStationsByCountryName(String countryName) {
        def query = new Query("ReadableStation")

        query.addFilter("countryName", Query.FilterOperator.EQUAL, countryName)

        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asList(withDefaults())
    }

    static def findStationsByCity(String city) {
        def query = new Query("ReadableStation")

        query.addFilter("city", Query.FilterOperator.EQUAL, city)

        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asList(withDefaults())
    }

    static def findCountries() {

        def query = new Query("Country")
        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asList(withChunkSize(200).prefetchSize(200))

    }

    static def findCities(String countryName) {
        Query query = new Query("ReadableStation");
        query.addProjection(new PropertyProjection("city", String.class));
        query.addFilter("countryName", Query.FilterOperator.EQUAL, countryName)

        PreparedQuery preparedQuery = dataStore.prepare(query)

        preparedQuery.asList(withChunkSize(200).prefetchSize(200))

    }
}
