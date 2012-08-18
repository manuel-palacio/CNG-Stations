/**
 * *******************************************************************************************************************
 * <p/>
 * Copyright (C) 8/7/12 by Manuel Palacio
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


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import net.palacesoft.cngstation.client.StationActivity;

import java.util.*;

public class StationBalloonOverlay extends BalloonItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

    private StationActivity stationActivity;


    public StationBalloonOverlay(Drawable defaultMarker, StationActivity context) {
        super(boundCenterBottom(defaultMarker), context.getMapView());
        stationActivity = context;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return overlayItems.get(i);
    }

    @Override
    public int size() {
        return overlayItems.size();
    }

    @Override
    protected boolean onBalloonTap(int index, OverlayItem item) {

        AlertDialog.Builder dialog = buildDialog((StationOverlayItem) item);
        dialog.show();
        return true;

    }

    private AlertDialog.Builder buildDialog(final StationOverlayItem item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(stationActivity);
        dialog.setTitle(item.getTitle());
        final List<CharSequence> items = getItemsToShow(item);


        setDialogContent(dialog, items);
        setDialogPositiveButton(item, dialog);
        setDialogNegativeButton(dialog);
        return dialog;
    }

    private void setDialogNegativeButton(AlertDialog.Builder dialog) {
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    private void setDialogPositiveButton(final StationOverlayItem item, AlertDialog.Builder dialog) {
        dialog.setPositiveButton("Directions", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + stationActivity.getCurrentLocation().getLatitude()
                                    + "," + stationActivity.getCurrentLocation().getLongitude() + "&daddr="
                                    + item.getLatitude() + "," + item.getLongitude()));
                    stationActivity.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(stationActivity, "Could not show map", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setDialogContent(AlertDialog.Builder dialog, final List<CharSequence> items) {
        dialog.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (items.get(item).toString().startsWith("Tel")) {
                    try {
                        String number = items.get(item).toString().split(":")[1];
                        if (number.length() > 1) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.trim()));
                            stationActivity.startActivity(intent);
                        }
                    } catch (Exception e) {
                        Toast.makeText(stationActivity, "Could not make call", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private List<CharSequence> getItemsToShow(StationOverlayItem item) {
        List<CharSequence> items = new ArrayList<CharSequence>();
        String[] phoneNo = item.getPhoneNo().split(",");

        for (String aPhoneNo : phoneNo) {
            items.add("Tel: " + aPhoneNo);
        }
        items.add("Price: " + item.getPrice());
        items.add("Opening hours: " + item.getOpeningHours());

        return items;
    }

    /*@Override
    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        if (!shadow) {
            //cycle through all overlays
            for (OverlayItem item : overlayItems) {
                // Converts lat/lng-Point to coordinates on the screen
                GeoPoint point = item.getPoint();
                Point ptScreenCoord = new Point();
                mapView.getProjection().toPixels(point, ptScreenCoord);

                //Paint
                Paint paint = new Paint();
                //paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(30);
                paint.setARGB(150, 0, 0, 0); // alpha, r, g, b (Black, semi see-through)
                StationOverlayItem stationOverlayItem = (StationOverlayItem) item;
                //show text to the right of the icon
                canvas.drawText(stationOverlayItem.getPrice(), ptScreenCoord.x + 30, ptScreenCoord.y, paint);

            }
        }


    }*/

    public void removeOverlay(OverlayItem overlay) {
        overlayItems.remove(overlay);
        populate();
    }

    public void clear() {
        overlayItems.clear();
        populate();
    }

    public void popupCheapest() {
        List<OverlayItem> copy = new ArrayList<OverlayItem>(overlayItems);

        if (copy.size() == 1) {
            tapOverlay(copy.get(0));
            return;
        }

        try {
            Collections.sort(copy, new Comparator<OverlayItem>() {
                @Override
                public int compare(OverlayItem overlayItem1, OverlayItem overlayItem2) {
                    StationOverlayItem item1 = (StationOverlayItem) overlayItem1;
                    StationOverlayItem item2 = (StationOverlayItem) overlayItem2;

                    return Double.valueOf(item1.getFilteredPrice()).compareTo(Double.valueOf(item2.getFilteredPrice()));

                }
            });

            OverlayItem found = copy.get(0);

            tapOverlay(found);
        } catch (Exception e) {
            e.printStackTrace();
            //ignore
        }
    }

    private void tapOverlay(OverlayItem item) {
        int index = overlayItems.lastIndexOf(item);

        onTap(index);
    }


    public void addOverlayItems(List<StationOverlayItem> overlayItems) {
        this.overlayItems.addAll(overlayItems);
        populate();
    }
}
