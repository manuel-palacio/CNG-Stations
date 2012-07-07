package net.palacesoft.cngstation.client.mapoverlay;

import net.palacesoft.cngstation.client.StationDTO;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


/**
 * Represents station on map
 */
public class StationOverlayItem extends OverlayItem {

    private String phoneNo;
    private String price;
    private String openingHours;
    private float latitude;
    private float longitude;

    public StationOverlayItem(GeoPoint geoPoint, String title, String snippet, StationDTO stationDTO) {
        super(geoPoint, title, snippet);

        setPhoneNo(stationDTO.getPhoneNo());
        setOpeningHours(stationDTO.getOpeningHours());
        setPrice(stationDTO.getPrice());
        setLatitude(geoPoint.getLatitudeE6());
        setLongitude(geoPoint.getLongitudeE6());
    }

    public double getLatitude() {
        return latitude / 1E6;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude / 1E6;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }


    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String telephone) {
        this.phoneNo = telephone;
    }
}
