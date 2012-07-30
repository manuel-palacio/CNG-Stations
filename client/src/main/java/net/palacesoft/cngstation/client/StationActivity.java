/**
 * *******************************************************************************************************************
 * <p/>
 * Copyright (C) 7/28/12 by Manuel Palacio
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
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import net.palacesoft.cngstation.R;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlay;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StationActivity extends MapActivity {

    private MapView mapView;
    private MapController mapController;
    private StationOverlay stationOverlay;
    private Location currentLocation;
    private MyLocationOverlay myLocationOverlay;
    private Spinner countries, cities;

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                Address locationAddress = lookupAddressFromLocation(Locale.getDefault(), currentLocation);
                loadStations(locationAddress);
        }

        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        myLocationOverlay.disableMyLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myLocationOverlay.enableMyLocation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        initMap();

        initSearchForm();

        loadAvailableCountriesList();

        initMyLocationOverlay();
    }

    private void initSearchForm() {
        countries = (Spinner) findViewById(R.id.countries);
        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                String country = adapterView.getItemAtPosition(pos).toString();
                loadAvailableCitiesList(country);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        cities = (Spinner) findViewById(R.id.cities);


        ImageButton btnSubmit = (ImageButton) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String city = cities.getSelectedItem().toString();

                Integer zoomLevel = null;
                Address address;
                if (city.equalsIgnoreCase("Alla")) {
                    address = Country.findAddress(CountryCode.valueOf(countries.getSelectedItem().toString())).getAddress();
                    zoomLevel = 6;
                } else {
                    address = lookupAddressFromLocationName(new Locale(countries.getSelectedItem().toString()), city);
                }
                new StationLoader(StationActivity.this, address, zoomLevel).execute();
            }
        });
    }

    private void loadAvailableCitiesList(String country) {
        new CitiesLoader(this).execute(country);
    }

    private void loadAvailableCountriesList() {
        new CountriesLoader(this).execute();
    }

    private Address lookupAddressFromLocation(Locale locale, Location location) {
        Geocoder geocoder = new Geocoder(this, locale);
        List<Address> addresses = Collections.emptyList();

        try {
            if (location != null) {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            }
        } catch (IOException e) {
            //ignore
        }

        return extractAddress(addresses);
    }

    private Address lookupAddressFromLocationName(Locale locale, String locationName) {
        Geocoder geocoder = new Geocoder(this, locale);
        List<Address> addresses = Collections.emptyList();
        if (StringUtils.hasText(locationName)) {
            try {
                addresses = geocoder.getFromLocationName(locationName, 1);
            } catch (IOException e) {
                //ignore
            }
        }
        return extractAddress(addresses);
    }

    private Address extractAddress(List<Address> addresses) {
        if (addresses.isEmpty()) {
            return null;
        }

        return addresses.get(0);
    }


    public void showInfoMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    public void addStationOverlay() {
        stationOverlay = new StationOverlay(this.getResources().getDrawable(R.drawable.marker), this);
        mapView.getOverlays().add(stationOverlay);
    }


    private void initMyLocationOverlay() {
        myLocationOverlay = new MyLocationOverlay(this, mapView) {
            @Override
            public synchronized void onLocationChanged(Location location) {
                currentLocation = location;
                super.onLocationChanged(location);
            }
        };

        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
        startTrackingMyLocation();
    }

    private void startTrackingMyLocation() {

        final ProgressDialog progressDialog = createProgressDialog("Fastställer position...");
        progressDialog.show();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                currentLocation = myLocationOverlay.getLastFix();
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Address locationAddress = lookupAddressFromLocation(Locale.getDefault(), currentLocation);
                        loadStations(locationAddress);
                    }
                });
            }
        });
    }

    private void loadStations(Address address) {
        if (address != null) {
            new StationLoader(this, address).execute();
        } else {
            showInfoMessage("Kunde inte fastställa din eller stadens position");
        }
    }

    private void initMap() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
    }


    public MapController getMapController() {
        return mapController;
    }

    public MapView getMapView() {
        return mapView;
    }

    public StationOverlay getStationOverlay() {
        return stationOverlay;
    }

    public ProgressDialog createProgressDialog(String message) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public Spinner getCountries() {
        return countries;
    }

    public Spinner getCities() {
        return cities;
    }
}
