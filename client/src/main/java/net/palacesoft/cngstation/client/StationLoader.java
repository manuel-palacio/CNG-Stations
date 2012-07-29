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

import android.app.ProgressDialog;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.maps.GeoPoint;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlayItem;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;


class StationLoader extends AsyncTask<Object, Integer, List<StationOverlayItem>> {
    private ProgressDialog progressDialog;
    private GeoPoint geoPoint;
    private Address address;
    private Integer zoomLevel = 12;
    private StationActivity stationActivity;
    private RestTemplate restTemplate = new RestTemplate();


    StationLoader(StationActivity stationActivity, Address address, Integer zoomLevel) {
        this.stationActivity = stationActivity;
        this.address = address;
        if (address != null) {
            geoPoint = new GeoPoint((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
        }
        if (zoomLevel != null) {
            this.zoomLevel = zoomLevel;
        }
    }

    StationLoader(StationActivity stationActivity, Address address) {
        this(stationActivity, address, null);
    }


    @Override
    protected void onPreExecute() {
        progressDialog = stationActivity.createProgressDialog("Populerar kartan...");
        progressDialog.show();
    }


    @Override
    protected List<StationOverlayItem> doInBackground(Object... params) {
        List<StationOverlayItem> results = new ArrayList<StationOverlayItem>();
        if (address != null) {
            String locality = address.getLocality();
            String countryCode = address.getCountryCode();

            if (hasText(locality)) {
                results = getLocalStations(locality);
            } else if (hasText(countryCode)) {
                results = getCountryStations(countryCode);
            }
        }

        return results;
    }

    private List<StationOverlayItem> getCountryStations(String countryCode) {
        String queryURL = "http://fuelstationservice.appspot.com/stations/country/" + countryCode;
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
        String stationAddress = stationDTO.getStreet() + " (" + stationDTO.getCity() + ")";
        return new StationOverlayItem(geoPoint, stationAddress, "", stationDTO);
    }

    @Override
    protected void onPostExecute(List<StationOverlayItem> overlayItems) {
        progressDialog.dismiss();

        if (!overlayItems.isEmpty()) {
            stationActivity.addStationOverlay();
            stationActivity.getStationOverlay().addOverlayItems(overlayItems);
            stationActivity.getMapController().animateTo(geoPoint);
            stationActivity.getMapController().setZoom(zoomLevel);
        } else {
            stationActivity.getMapView().getOverlays().remove(stationActivity.getStationOverlay());
            stationActivity.showError("Kunde inte visa några resultat på kartan");
        }
    }
}