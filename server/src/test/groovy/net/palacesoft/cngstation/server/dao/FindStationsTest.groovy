package net.palacesoft.cngstation.server.dao

import groovyx.gaelyk.datastore.PogoEntityCoercion
import net.palacesoft.cngstation.server.model.Station
import org.junit.Test

class FindStationsTest extends AbstractStationTest {


    @Test
    void findStationsByCity() {

        def stations = CngDao.findStationsByCity("Stockholm")

        Station station = PogoEntityCoercion.convert(stations[0], Station.class)

        assert station.city
        assert station.phoneNo
        assert station.price
        assert station.street
    }

    @Test
    void findStationsByCountry() {

        def stations = CngDao.findStationsByCountryName("Sweden")

        Station station = PogoEntityCoercion.convert(stations[0], Station.class)

        assert station.city
        assert station.phoneNo
        assert station.price
        assert station.street
    }
}
