package net.palacesoft.cngstation.client;

import android.app.ProgressDialog;
import android.location.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
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
                getStationsFromCloud();
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

        initMap();

        addStationsOverlay();

        initMyLocation();
    }

    private void addStationsOverlay() {
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fastställer position...");
        progressDialog.show();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                currentLocation = myLocationOverlay.getLastFix();
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        getStationsFromCloud();
                    }
                });
            }
        });
    }

    private void initMap() {
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
    }

    private Address extractAddressFromLocation() {

        Geocoder geoCoder = new Geocoder(this);

        List<Address> list = Collections.emptyList();
        try {
            list = geoCoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
        } catch (IOException e) {
            //ignore
        }
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }


    private void getStationsFromCloud() {

        new StationsLoader().execute();
    }

    private class StationsLoader extends AsyncTask<String, Integer, List<StationOverlayItem>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            createProgressDialog();
        }

        private void createProgressDialog() {
            progressDialog = new ProgressDialog(StationActivity.this);
            progressDialog.setMessage("Populerar kartan...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected List<StationOverlayItem> doInBackground(String... params) {

            List<StationOverlayItem> stationOverlayItems = Collections.emptyList();

            Address locationAddress = extractAddressFromLocation();

            if (locationAddress != null) {
                String locality = locationAddress.getLocality();
                String countryCode = locationAddress.getCountryCode();

                String queryURL;
                if (locality != null) {
                    queryURL = "http://fuelstationservice.appspot.com/stations/city/" + locality;
                    stationOverlayItems = fetchStations(queryURL);
                    if (!stationOverlayItems.isEmpty()) {
                        return stationOverlayItems;
                    }
                }

                if (countryCode != null) {
                    queryURL = "http://fuelstationservice.appspot.com/stations/country/" + countryCode;
                    stationOverlayItems = fetchStations(queryURL);
                    if (!stationOverlayItems.isEmpty()) {
                        return stationOverlayItems;
                    }
                }
            }

            return stationOverlayItems;
        }

        private List<StationOverlayItem> fetchStations(String queryURL) {
            List<StationOverlayItem> stationOverlayItems = new ArrayList<StationOverlayItem>();
            if (queryURL != null) {
                StationDTO[] stations = restTemplate.getForObject(queryURL, StationDTO[].class);
                stationOverlayItems = new ArrayList<StationOverlayItem>();
                for (StationDTO stationDTO : stations) {
                    //  Log.i(StationActivity.class.getName(), stationDTO.getStreet());

                    float lat = stationDTO.getLatitude();
                    float lng = stationDTO.getLongitude();

                    GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
                    String stationAddress = stationDTO.getStreet() + " (" + stationDTO.getCity() + ")";
                    StationOverlayItem overlayItem = new StationOverlayItem(point, stationAddress, "", stationDTO);
                    stationOverlayItems.add(overlayItem);

                }
            }
            return stationOverlayItems;
        }

        @Override
        protected void onPostExecute(List<StationOverlayItem> result) {
            if (!result.isEmpty()) {
                stationOverlay.addOverlays(result);
                progressDialog.dismiss();
                mapController.animateTo(myLocationOverlay.getMyLocation());
                mapController.setZoom(12);
            } else {
                Toast.makeText(StationActivity.this, "Kunde inte visa några resultat på kartan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
