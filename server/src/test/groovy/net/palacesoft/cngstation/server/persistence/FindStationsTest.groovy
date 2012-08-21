package net.palacesoft.cngstation.server.persistence

import net.palacesoft.cngstation.server.model.Country
import net.palacesoft.cngstation.server.model.Station
import org.junit.Before
import com.google.appengine.api.datastore.DatastoreService
import com.google.appengine.api.datastore.DatastoreServiceFactory
import com.google.appengine.tools.development.testing.LocalServiceTestHelper
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import org.junit.Test
import groovyx.gaelyk.datastore.PogoEntityCoercion

class FindStationsTest {


    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());



    @Before
    public void setUp() {
        helper.setUp();

        DatastoreService ds = DatastoreServiceFactory.datastoreService


        def station = new Station.Builder("lat", "long", "Stockholm", Country.Sweden).
                withPhoneNo("phone").withStreet("street").withPrice("price").build()
        def entity = PogoEntityCoercion.convert(station)
        entity.key.kind = "ReadableStation"

        ds.put entity
    }

    @Test
    void findStationsByCity() {

        def stations = CngDao.findStationsByCity("Stockholm")

        def station = PogoEntityCoercion.convert(stations[0], Station.class)

        assert station.city
        assert station.phoneNo
        assert station.price
        assert station.street
    }

    @Test
    void findStationsByCountty() {

        def stations = CngDao.findStationsByCountryName("Sweden")

        def station = PogoEntityCoercion.convert(stations[0], Station.class)

        assert station.city
        assert station.phoneNo
        assert station.price
        assert station.street
    }
}
