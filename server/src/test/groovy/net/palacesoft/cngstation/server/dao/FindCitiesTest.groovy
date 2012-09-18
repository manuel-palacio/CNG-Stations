package net.palacesoft.cngstation.server.dao

import org.junit.Test

class FindCitiesTest extends AbstractStationTest {

    @Test
    void findCities() {

        def cities = CngDao.findCities("Sweden")
        assert cities.size() == 1
        cities.each {
            assert it == "Stockholm"
        }
    }
}
