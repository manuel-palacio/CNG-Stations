package net.palacesoft.cngstation.server.persistence

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
