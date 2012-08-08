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
package net.palacesoft.cngstation.client.loader;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import net.palacesoft.cngstation.client.StationActivity;
import net.palacesoft.cngstation.client.StationDTO;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;


public class CityLoader extends AsyncTask<String, Integer, List<String>> {
    private StationActivity stationActivity;
    private ProgressDialog progressDialog;
    private RestTemplate restTemplate = new RestTemplate();


    public CityLoader(StationActivity stationActivity) {
        this.stationActivity = stationActivity;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = stationActivity.createProgressDialog("Loading cities...");
        progressDialog.show();
    }

    @Override
    protected List<String> doInBackground(String... params) {
        StationDTO[] dtos = new StationDTO[0];
        try {
            dtos = restTemplate.getForObject("http://fuelstationservice.appspot.com/cities/country/{query}", StationDTO[].class, params[0]);
        } catch (RestClientException e) {
            Log.e(StationActivity.class.getName(), e.getMessage(), e);
        }

        List<String> citiesList = new ArrayList<String>();
        for (StationDTO stationDTO : dtos) {
            citiesList.add(stationDTO.getCity());
        }
        citiesList.add(0, "All");
        return citiesList;
    }

    @Override
    protected void onPostExecute(List<String> citiesList) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(stationActivity,
                simple_spinner_item, citiesList);
        dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        stationActivity.getCities().setAdapter(dataAdapter);
        progressDialog.dismiss();
    }
}
