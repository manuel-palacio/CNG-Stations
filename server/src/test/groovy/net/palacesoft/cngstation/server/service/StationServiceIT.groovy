package net.palacesoft.cngstation.server.service

import static com.jayway.restassured.RestAssured.*
import static org.hamcrest.Matchers.*
import org.junit.Test

class StationServiceIT {

    def String BASE_URL = "http://fuelstationservice.appspot.com"

    @Test
    void canGetStationsForCity() {

        expect().statusCode(200).body("city", hasItem("Stockholm")).when().get("${BASE_URL}/stations/city/Stockholm")
    }

    @Test
    void cannotGetStationsForUnknownCity() {

        expect().statusCode(404).when().get("${BASE_URL}/stations/city/XXX")
    }

    @Test
       void canGetListOfCities() {

           expect().statusCode(404).when().get("${BASE_URL}/stations/city/XXX")
       }
}
