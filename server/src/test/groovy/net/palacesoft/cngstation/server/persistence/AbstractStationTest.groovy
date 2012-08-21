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

import org.junit.Before
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import net.palacesoft.cngstation.server.model.Station
import net.palacesoft.cngstation.server.model.Country
import com.google.appengine.api.datastore.Entity
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import org.junit.After

abstract class AbstractStationTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())

    @Before
    public void setUp() {
        helper.setUp();

        createEntity()
    }

    private void createEntity() {
        DatastoreService ds = DatastoreServiceFactory.datastoreService


        def station = new Station.Builder("lat", "long", "Stockholm", Country.Sweden).
                withPhoneNo("phone").withStreet("street").withPrice("price").build()

        def entity = new Entity(CngDao.READABLE_STATION)
        entity.setProperty("city", station.city)
        entity.setProperty("phoneNo", station.phoneNo)
        entity.setProperty("street", station.street)
        entity.setProperty("price", station.price)
        entity.setProperty("countryName", station.countryName)
        entity.setProperty("countryCode", station.countryCode)

        ds.put entity
    }

    @After
    public void tearDown() {
        helper.tearDown()
    }
}
