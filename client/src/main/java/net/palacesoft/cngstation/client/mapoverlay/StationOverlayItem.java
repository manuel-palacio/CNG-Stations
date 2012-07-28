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
