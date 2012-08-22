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
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.maps.GeoPoint;
import net.palacesoft.cngstation.client.AddressEmptyException;
import net.palacesoft.cngstation.client.StationActivity;
import net.palacesoft.cngstation.client.StationDTO;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlayItem;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;


public class StationLoader extends AsyncTask<String, Integer, List<StationOverlayItem>> {
    private ProgressDialog progressDialog;
    private GeoPoint geoPointToZoomTo;
    private Address address;
    private Integer zoomLevel = 12;
    private StationActivity stationActivity;
    private RestTemplate restTemplate = new RestTemplate();


    public StationLoader(StationActivity stationActivity, Address addressToZoomTo, Integer zoomLevel) throws AddressEmptyException {
        this.stationActivity = stationActivity;
        if (addressToZoomTo == null) {
            throw new AddressEmptyException("Cannot load stations without a location");
        }
        this.address = addressToZoomTo;
        geoPointToZoomTo = new GeoPoint((int) (addressToZoomTo.getLatitude() * 1E6), (int) (addressToZoomTo.getLongitude() * 1E6));
        if (zoomLevel != null) {
            this.zoomLevel = zoomLevel;
        }
    }

    public StationLoader(StationActivity stationActivity, Address address) throws AddressEmptyException {
        this(stationActivity, address, null);
    }


    @Override
    protected void onPreExecute() {
        stationActivity.addStationOverlay();
        progressDialog = stationActivity.createProgressDialog("Loading CNG map...");
        progressDialog.show();
    }


    @Override
    protected List<StationOverlayItem> doInBackground(String... params) {
        List<StationOverlayItem> results = new ArrayList<StationOverlayItem>();
        String locality = address.getLocality();
        String countryName = address.getCountryName();

        if (hasText(locality)) {
            results = getLocalStations(locality);
        } else if (hasText(countryName)) {
            results = getCountryStations(countryName);
        }

        return results;
    }

    private List<StationOverlayItem> getCountryStations(String countryName) {
        String queryURL = "http://fuelstationservice.appspot.com/stations/country/" + countryName;
        return fetchStations(queryURL);
    }

    private List<StationOverlayItem> getLocalStations(String locality) {
        String queryURL = "http://fuelstationservice.appspot.com/stations/city/" + locality;
        return fetchStations(queryURL);
    }

    private List<StationOverlayItem> fetchStations(String queryURL) {
        List<StationOverlayItem> stationOverlayItems = new ArrayList<StationOverlayItem>();
        StationDTO[] stations = new StationDTO[0];
        try {
            stations = restTemplate.getForObject(queryURL, StationDTO[].class);
        } catch (RestClientException e) {
            Log.e(StationActivity.class.getName(), e.getMessage(), e);
        }
        for (StationDTO stationDTO : stations) {
            StationOverlayItem overlayItem = createOverlayItem(stationDTO);
            stationOverlayItems.add(overlayItem);
        }
        return stationOverlayItems;
    }

    private StationOverlayItem createOverlayItem(StationDTO stationDTO) {
        GeoPoint geoPoint = new GeoPoint((int) (stationDTO.getLatitude() * 1E6), (int) (stationDTO.getLongitude() * 1E6));
        String stationText = stationDTO.getStreet();
        return new StationOverlayItem(geoPoint, stationText, "Price: " + stationDTO.getPrice(), stationDTO);
    }

    @Override
    protected void onPostExecute(List<StationOverlayItem> overlayItems) {
        progressDialog.dismiss();

        if (!overlayItems.isEmpty()) {
            stationActivity.showStations(overlayItems, geoPointToZoomTo, zoomLevel);
        } else {
            stationActivity.showInfoMessage("Could not find CNG stations for location: " + address.getLocality());
        }
    }
}
