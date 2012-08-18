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
import android.view.*;
import android.widget.*;
import com.google.android.maps.*;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;
import net.palacesoft.cngstation.R;
import net.palacesoft.cngstation.client.loader.CityLoader;
import net.palacesoft.cngstation.client.loader.CountryLoader;
import net.palacesoft.cngstation.client.loader.StationLoader;
import net.palacesoft.cngstation.client.mapoverlay.StationBalloonOverlay;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlayItem;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static org.springframework.util.StringUtils.hasText;

public class StationActivity extends MapActivity {

    private TapControlledMapView mapView;
    private MapController mapController;
    private StationBalloonOverlay stationOverlay;
    private Location currentLocation;
    private MyLocationOverlay myLocationOverlay;
    private Spinner countries, cities;
    private Address currentLocationAddress;

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
                Address locationAddress = lookupAddressFromLocation(Locale.ENGLISH, currentLocation);
                try {
                    new StationLoader(this, locationAddress).execute("");
                } catch (AddressEmptyException e) {
                    showInfoMessage("Could not determine your location");
                }

                break;

            case R.id.search:
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.searchLayout);
                if (linearLayout.getVisibility() == View.VISIBLE) {
                    linearLayout.setVisibility(View.INVISIBLE);
                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    if (countries == null) {
                        initSearchForm();
                        loadAvailableCountriesList();
                    }
                }
                break;

            case R.id.discard:
                 clearStationOverlay();
                break;
        }

        return false;
    }

    public void clearStationOverlay() {
        mapView.getOverlays().remove(stationOverlay);
        mapView.invalidate();
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

        initMyLocationOverlay();

        addStationOverlay();
    }

    private void initSearchForm() {
        countries = (Spinner) findViewById(R.id.countries);
        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                String country = adapterView.getItemAtPosition(pos).toString();
                new CityLoader(StationActivity.this, country).execute("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        cities = (Spinner) findViewById(R.id.cities);


        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String city = cities.getSelectedItem().toString();

                Integer zoomLevel = null;
                Address address;
                if (hasText(city)) {
                    if (city.equals("All")) {
                        address = Country.valueOf(countries.getSelectedItem().toString()).getAddress();
                        zoomLevel = 6;
                    } else {
                        String country = countries.getSelectedItem().toString();
                        address = lookupAddressFromLocationName(new Locale(Country.valueOf(country).getCountryCode()), city);
                    }
                    try {
                        new StationLoader(StationActivity.this, address, zoomLevel).execute("");
                    } catch (AddressEmptyException e) {
                        showInfoMessage("Problem finding CNG stations for chosen location");
                    }
                }
            }
        });

    }


    private void loadAvailableCountriesList() {
        new CountryLoader(this).execute("");
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

    private Address lookupAddressFromLocationName(Locale locale, String city) {
        Geocoder geocoder = new Geocoder(this, locale);
        List<Address> addresses = Collections.emptyList();
        if (StringUtils.hasText(city)) {
            try {
                addresses = geocoder.getFromLocationName(city, 1);
            } catch (IOException e) {
                //ignore
            }
        }
        Address address = extractAddress(addresses);
        if (address != null) {
            address.setLocality(city); //set the location name according to value stored in GAE
        }

        return address;
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
        if (stationOverlay == null) {
            stationOverlay = new StationBalloonOverlay(this.getResources().getDrawable(R.drawable.marker), this);
            stationOverlay.setShowClose(false);
            stationOverlay.setShowDisclosure(true);
            stationOverlay.setSnapToCenter(true);
        }
        if (!mapView.getOverlays().contains(stationOverlay)) {
            mapView.getOverlays().add(stationOverlay);
        }
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

        final ProgressDialog progressDialog = createProgressDialog("Determining your location...");
        progressDialog.show();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                currentLocation = myLocationOverlay.getLastFix();
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        currentLocationAddress = lookupAddressFromLocation(Locale.ENGLISH, currentLocation);
                        try {
                            new StationLoader(StationActivity.this, currentLocationAddress).execute("");
                        } catch (AddressEmptyException e) {
                            showInfoMessage("Could not determine your location");
                        }
                    }
                });
            }
        });
    }

    private void initMap() {
        mapView = (TapControlledMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public boolean onSingleTap(MotionEvent e) {
                stationOverlay.hideAllBalloons();
                return true;
            }
        });
        mapController = mapView.getController();
    }


    public MapController getMapController() {
        return mapController;
    }

    public MapView getMapView() {
        return mapView;
    }

    public StationBalloonOverlay getStationOverlay() {
        return stationOverlay;
    }

    public ProgressDialog createProgressDialog(String message) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public Spinner getCities() {
        return cities;
    }

    public void setCountries(List<String> countriesList) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                simple_spinner_item, countriesList);
        dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        countries.setAdapter(dataAdapter);

        String country = currentLocationAddress.getCountryName();
        int countryIndex = countriesList.indexOf(country);
        if (countryIndex > -1) {
            countries.setSelection(countryIndex);
        }
    }

    public void showStations(List<StationOverlayItem> overlayItems, GeoPoint geoPoint, int zoomLevel) {
        stationOverlay.clear();
        stationOverlay.addOverlayItems(overlayItems);
        mapController.animateTo(geoPoint);
        mapController.setZoom(zoomLevel);
        stationOverlay.popupCheapest();
    }
}
