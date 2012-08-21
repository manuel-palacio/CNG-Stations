package net.palacesoft.cngstation.server.dao

import org.junit.Test

class FindCountriesTest extends AbstractStationTest {


    @Test
    void findCities() {

        def cities = CngDao.findCities("Sweden")
        cities.each {
            print it
        }

    }
}
