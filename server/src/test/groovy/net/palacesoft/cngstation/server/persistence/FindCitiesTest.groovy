package net.palacesoft.cngstation.server.persistence

import com.google.appengine.api.datastore.Entity
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import net.palacesoft.cngstation.server.model.Country
import net.palacesoft.cngstation.server.model.Station
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.api.datastore.DatastoreService

class FindCitiesTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();

        DatastoreService ds = DatastoreServiceFactory.datastoreService

        def station = new Station.Builder("lat", "long", "Stockholm", Country.Sweden).build()
        def entity = new Entity("ReadableEntity")
        entity.setProperty("city", station.city)

        ds.put entity
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }


    @Test
    void findCities() {

        def cities = CngDao.findCities("Sweden")
        cities.each {
            assert it
        }

    }
}
