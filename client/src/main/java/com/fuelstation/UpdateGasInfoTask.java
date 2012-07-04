package com.fuelstation;


import com.fuelstation.mapoverlay.GasStationOverlay;
import com.fuelstation.mapoverlay.GasStationOverlayItem;
import com.google.android.maps.GeoPoint;
import org.springframework.web.client.RestTemplate;

/**
 * Gets updated station list from net
 */
public class UpdateGasInfoTask implements Runnable {

    private RestTemplate restTemplate;
    private GasStationOverlay gasStationOverlay;
    private String country;


    public UpdateGasInfoTask(RestTemplate restTemplate, GasStationOverlay gasStationOverlay, String country) {
        this.restTemplate = restTemplate;
        this.gasStationOverlay = gasStationOverlay;
        this.country = country;
    }

    public void run() {

        String url = "http://fuelstationservice.appspot.com/stations/country/{query}";
        GasStationDTO[] stations = restTemplate.getForObject(url, GasStationDTO[].class, country.toUpperCase());

        for (GasStationDTO gasStationDTO : stations) {
            //  Log.i(GasStationActivity.class.getName(), gasStationDTO.getStreet());

            float lat = gasStationDTO.getLatitude();
            float lng = gasStationDTO.getLongitude();

            GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
            String address = gasStationDTO.getStreet() + " (" + gasStationDTO.getCity() + ")";
            GasStationOverlayItem overlayItem = new GasStationOverlayItem(point, address, "", gasStationDTO);

            gasStationOverlay.addOverlay(overlayItem);

        }

    }
}
