package com.fuelstation.mapoverlay;

import com.fuelstation.GasStationDTO;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


/**
 * Represents station on map
 */
public class GasStationOverlayItem extends OverlayItem {

    private String phoneNo;
    private String price;
    private String openingHours;
    private float latitude;
    private float longitude;

    public GasStationOverlayItem(GeoPoint geoPoint, String title, String snippet, GasStationDTO gasStationDTO) {
        super(geoPoint, title, snippet);

        setPhoneNo(gasStationDTO.getPhoneNo());
        setOpeningHours(gasStationDTO.getOpeningHours());
        setPrice(gasStationDTO.getPrice());
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
