package net.palacesoft.stationclient;


import net.palacesoft.stationclient.mapoverlay.StationOverlay;
import net.palacesoft.stationclient.mapoverlay.StationOverlayItem;
import com.google.android.maps.GeoPoint;
import org.springframework.web.client.RestTemplate;

/**
 * Gets updated station list from net
 */
public class UpdateStationInfoTask implements Runnable {

    private RestTemplate restTemplate;
    private StationOverlay stationOverlay;
    private String country;


    public UpdateStationInfoTask(RestTemplate restTemplate, StationOverlay stationOverlay, String country) {
        this.restTemplate = restTemplate;
        this.stationOverlay = stationOverlay;
        this.country = country;
    }

    public void run() {

        String url = "http://fuelstationservice.appspot.com/stations/country/{query}";
        StationDTO[] stations = restTemplate.getForObject(url, StationDTO[].class, country.toUpperCase());

        for (StationDTO stationDTO : stations) {
            //  Log.i(StationActivity.class.getName(), stationDTO.getStreet());

            float lat = stationDTO.getLatitude();
            float lng = stationDTO.getLongitude();

            GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
            String address = stationDTO.getStreet() + " (" + stationDTO.getCity() + ")";
            StationOverlayItem overlayItem = new StationOverlayItem(point, address, "", stationDTO);

            stationOverlay.addOverlay(overlayItem);

        }

    }
}
