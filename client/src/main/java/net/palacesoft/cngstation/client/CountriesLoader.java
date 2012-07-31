/**
* *******************************************************************************************************************
* <p/>
* Copyright (C) 7/30/12 by Manuel Palacio
* <p/>
* **********************************************************************************************************************
* <p/>
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
* <p/>
* http://www.apache.org/licenses/LICENSE-2.0
* <p/>
* Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
* an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
* specific language governing permissions and limitations under the License.
* <p/>
* **********************************************************************************************************************
*/
package net.palacesoft.cngstation.client;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static java.util.Arrays.asList;


class CountriesLoader extends AsyncTask<Object, Integer, List<StationDTO>> {
    private StationActivity stationActivity;
    private RestTemplate restTemplate = new RestTemplate();


    public CountriesLoader(StationActivity stationActivity) {
        this.stationActivity = stationActivity;
    }

    @Override
    protected List<StationDTO> doInBackground(Object... objects) {
        StationDTO[] dtos = new StationDTO[0];
        try {
            dtos = restTemplate.getForObject("http://fuelstationservice.appspot.com/countries", StationDTO[].class);
        } catch (RestClientException e) {
            Log.e(StationActivity.class.getName(), e.getMessage(), e);
        }
        return asList(dtos);
    }

    @Override
    protected void onPostExecute(List<StationDTO> stations) {
        List<String> countriesList = new ArrayList<String>();
        for (StationDTO stationDTO : stations) {
            countriesList.add(stationDTO.getCountryName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(stationActivity,
                simple_spinner_item, countriesList);
        dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        stationActivity.getCountries().setAdapter(dataAdapter);
    }
}
