package net.palacesoft.cngstation.client;

import android.app.ProgressDialog;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.maps.*;
import net.palacesoft.cngstation.R;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlay;
import net.palacesoft.cngstation.client.mapoverlay.StationOverlayItem;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationActivity extends MapActivity {

    private MapView mapView;
    private MapController mapController;
    private StationOverlay stationOverlay;
    private Location currentLocation;
    private String country;
    private MyLocationOverlay myLocationOverlay;
    private RestTemplate restTemplate = new RestTemplate();



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
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                getStationsFromCloud(country);
                mapController.setZoom(12);
                mapController.animateTo(myLocationOverlay.getMyLocation());
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

        initMapView();

        addStationsOverlay();

        extractOldLocationAndLoadStations();

        initMyLocation();

    }

    private void addStationsOverlay() {
        stationOverlay = new StationOverlay(this.getResources().getDrawable(R.drawable.marker), this);

        mapView.getOverlays().add(stationOverlay);
    }

    private void extractOldLocationAndLoadStations() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
        if (location != null) {
            mapController.animateTo(new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6)));
            country = extractCountryNameFromLocation(location);
            getStationsFromCloud(country);
        }
    }

    private void initMyLocation() {
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                currentLocation = myLocationOverlay.getLastFix();
                String updatedCountry = extractCountryNameFromLocation(myLocationOverlay.getLastFix());
                if (!updatedCountry.equals(country)) {
                    mapController.animateTo(myLocationOverlay.getMyLocation());
                    getStationsFromCloud(country);
                }
            }
        });
    }

    private void initMapView() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(12);
    }

    private String extractCountryNameFromLocation(Location location) {

        Geocoder geoCoder = new Geocoder(this);

        List<Address> list = Collections.emptyList();
        try {
            list = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            //ignore
        }
        if (!list.isEmpty()) {
            Address address = list.get(0);
            return address.getCountryName();
        }
        return null;

    }


    public void getStationsFromCloud(String country) {

        if (country != null) {
            new LoadViewTask().execute(country);
        }
    }

    private class LoadViewTask extends AsyncTask<String, Integer, List<StationOverlayItem>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(StationActivity.this);

            progressDialog.setMessage("Laddar...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<StationOverlayItem> doInBackground(String... params) {
            String url = "http://fuelstationservice.appspot.com/stations/country/{query}";
            StationDTO[] stations = restTemplate.getForObject(url, StationDTO[].class, params[0].toUpperCase());
            List<StationOverlayItem> stationOverlayItems = new ArrayList<StationOverlayItem>();
            for (StationDTO stationDTO : stations) {
                //  Log.i(StationActivity.class.getName(), stationDTO.getStreet());

                float lat = stationDTO.getLatitude();
                float lng = stationDTO.getLongitude();

                GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
                String address = stationDTO.getStreet() + " (" + stationDTO.getCity() + ")";
                StationOverlayItem overlayItem = new StationOverlayItem(point, address, "", stationDTO);
                stationOverlayItems.add(overlayItem);

            }
            return stationOverlayItems;
        }

        @Override
        protected void onPostExecute(List<StationOverlayItem> result) {
            //close the progress dialog

            for (StationOverlayItem next : result) {
                stationOverlay.addOverlay(next);
            }

            progressDialog.dismiss();
        }
    }
}
