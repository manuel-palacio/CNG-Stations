package com.fuelstation;

import android.location.*;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.fuelstation.mapoverlay.GasStationOverlay;
import com.google.android.maps.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GasStationActivity extends MapActivity {

    private MapView mapView;
    private MapController mapController;
    private Handler guiThread;
    private GasStationOverlay gasStationOverlay;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initMapView();

        guiThread = new Handler();

        gasStationOverlay = new GasStationOverlay(this.getResources().getDrawable(R.drawable.gas), this);

        mapView.getOverlays().add(gasStationOverlay);


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
        if (location != null) {
            mapController.animateTo(new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6)));
            country = extractCountryNameFromLocation(location);
            getStationsFromCloud(country);
        }

        initMyLocation();

    }

    private void initMyLocation() {
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableCompass();
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
            guiThread.post(new UpdateGasInfoTask(restTemplate, gasStationOverlay, country));
        }
    }
}
