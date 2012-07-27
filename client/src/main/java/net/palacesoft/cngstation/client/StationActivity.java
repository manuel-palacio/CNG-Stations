package net.palacesoft.cngstation.client;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.maps.*;
import net.palacesoft.cngstation.R;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlay;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlayItem;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.R.layout.*;
import static java.util.Arrays.asList;

public class StationActivity extends MapActivity {

    private MapView mapView;
    private MapController mapController;
    private StationOverlay stationOverlay;
    private Location currentLocation;
    private MyLocationOverlay myLocationOverlay;
    private RestTemplate restTemplate = new RestTemplate();
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
                Address locationAddress = extractAddressFromLocation(currentLocation);
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

        initMyLocation();
    }

    private void initSearchForm() {
        countries = (Spinner) findViewById(R.id.countries);
        countries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                String country = adapterView.getItemAtPosition(pos).toString();
                new CitiesLoader().execute(country);
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
                Geocoder geocoder = new Geocoder(StationActivity.this, new Locale(countries.getSelectedItem().toString()));   //

                try {
                    Integer zoomLevel = null;
                    List<Address> addresses = new ArrayList<Address>();
                    if (city.equalsIgnoreCase("Alla")) {
                        Address address = Country.findAddress(countries.getSelectedItem().toString()).getAddress();
                        addresses.add(address);
                        zoomLevel = 6;
                    } else {
                        addresses = geocoder.getFromLocationName(city, 1);
                    }
                    if (!addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        new StationLoader(address, zoomLevel).execute();
                    } else {
                        showError("Kunde inte visa info för " + city);
                    }
                } catch (IOException e) {
                    Log.w(e.getMessage(), e);
                }
            }
        });
        new CountriesLoader().execute();
    }

    private void showError(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void addStationOverlay() {
        stationOverlay = new StationOverlay(this.getResources().getDrawable(R.drawable.marker), this);
        mapView.getOverlays().add(stationOverlay);
    }


    private void initMyLocation() {
        myLocationOverlay = new MyLocationOverlay(this, mapView) {
            @Override
            public synchronized void onLocationChanged(Location location) {
                currentLocation = location;
                super.onLocationChanged(location);
            }
        };
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);
        final ProgressDialog progressDialog = createProgressDialog("Fastställer position...");
        progressDialog.show();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                currentLocation = myLocationOverlay.getLastFix();
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Address locationAddress = extractAddressFromLocation(currentLocation);
                        loadStations(locationAddress);
                    }
                });
            }
        });
    }

    private void loadStations(Address address) {
        if (address != null) {
            new StationLoader(address).execute();
        } else {
            showError("Kunde inte fastställa din eller stadens position");
        }
    }

    private void initMap() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
    }

    private Address extractAddressFromLocation(Location location) {

        if (location == null) {
            return null;
        }

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());

        List<Address> list = Collections.emptyList();
        try {
            list = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            //ignore
        }
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    private ProgressDialog createProgressDialog(String message) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private class CountriesLoader extends AsyncTask<Object, Integer, List<StationDTO>> {
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
            for (StationDTO next : stations) {
                countriesList.add(next.getCountryCode());
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(StationActivity.this,
                    simple_spinner_item, countriesList);
            dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
            countries.setAdapter(dataAdapter);
        }
    }

    private class CitiesLoader extends AsyncTask<String, Integer, List<String>> {
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
            citiesList.add(0, "Alla");
            return citiesList;
        }

        @Override
        protected void onPostExecute(List<String> citiesList) {

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(StationActivity.this,
                    simple_spinner_item, citiesList);
            dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
            cities.setAdapter(dataAdapter);
        }
    }

    private class StationLoader extends AsyncTask<Object, Integer, List<StationOverlayItem>> {
        private ProgressDialog progressDialog;
        private GeoPoint geoPoint;
        private Address address;
        private Integer zoomLevel = 12;

        private StationLoader(Address address, Integer zoomLevel) {
            this.address = address;
            if (address != null) {
                geoPoint = new GeoPoint((int) (address.getLatitude() * 1E6), (int) (address.getLongitude() * 1E6));
            }
            if (zoomLevel != null) {
                this.zoomLevel = zoomLevel;
            }
        }

        private StationLoader(Address address) {
            this(address, null);
        }


        @Override
        protected void onPreExecute() {
            progressDialog = createProgressDialog("Populerar kartan...");
            progressDialog.show();
        }


        @Override
        protected List<StationOverlayItem> doInBackground(Object... params) {
            if (address != null) {
                String locality = address.getLocality();
                String countryCode = address.getCountryCode();

                String queryURL;
                if (StringUtils.hasText(locality)) {
                    queryURL = "http://fuelstationservice.appspot.com/stations/city/" + locality;
                    List<StationOverlayItem> stationOverlayItems = fetchStations(queryURL);
                    if (!stationOverlayItems.isEmpty()) {
                        return stationOverlayItems;
                    }
                }

                if (StringUtils.hasText(countryCode)) {
                    queryURL = "http://fuelstationservice.appspot.com/stations/country/" + countryCode;
                    List<StationOverlayItem> stationOverlayItems = fetchStations(queryURL);
                    if (!stationOverlayItems.isEmpty()) {
                        return stationOverlayItems;
                    }
                }
            }

            return Collections.emptyList();
        }

        private List<StationOverlayItem> fetchStations(String queryURL) {
            List<StationOverlayItem> stationOverlayItems = new ArrayList<StationOverlayItem>();
            if (queryURL != null) {
                StationDTO[] stations = new StationDTO[0];
                try {
                    stations = restTemplate.getForObject(queryURL, StationDTO[].class);
                } catch (RestClientException e) {
                    Log.e(StationActivity.class.getName(), e.getMessage(), e);
                }
                for (StationDTO stationDTO : stations) {

                    float lat = stationDTO.getLatitude();
                    float lng = stationDTO.getLongitude();

                    GeoPoint geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
                    String stationAddress = stationDTO.getStreet() + " (" + stationDTO.getCity() + ")";
                    StationOverlayItem overlayItem = new StationOverlayItem(geoPoint, stationAddress, "", stationDTO);
                    stationOverlayItems.add(overlayItem);
                }
            }
            return stationOverlayItems;
        }

        @Override
        protected void onPostExecute(List<StationOverlayItem> result) {
            progressDialog.dismiss();

            if (!result.isEmpty()) {
                addStationOverlay();
                stationOverlay.addOverlays(result);
                mapController.animateTo(geoPoint);
                mapController.setZoom(zoomLevel);
            } else {
                mapView.getOverlays().remove(stationOverlay);
                showError("Kunde inte visa några resultat på kartan");
            }
        }
    }
}
