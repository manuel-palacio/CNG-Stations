package net.palacesoft.cngstation.client.mapoverlay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;
import net.palacesoft.cngstation.client.StationActivity;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;
import java.util.List;


public class StationOverlay extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    private StationActivity mContext;


    public StationOverlay(Drawable defaultMarker, StationActivity context) {
        super(boundCenterBottom(defaultMarker));
        mContext = context;
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
        final StationOverlayItem item = (StationOverlayItem) mOverlays.get(index);
        if (item != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle(item.getTitle());
            final List<CharSequence> items = createItemsToShowList(item);


            dialog.setItems(items.toArray(new CharSequence[items.size()]), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (items.get(item).toString().startsWith("Phone")) {
                        try {
                            String number = items.get(item).toString().split(":")[1];
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.trim()));
                            mContext.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "Kunde inte placera samtalet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            dialog.setPositiveButton("Vägbeskrivning", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + mContext.getCurrentLocation().getLatitude()
                                        + "," + mContext.getCurrentLocation().getLongitude() + "&daddr="
                                        + item.getLatitude() + "," + item.getLongitude()));
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(mContext, "Kunde inte visa kartan", Toast.LENGTH_SHORT).show();

                    }
                }
            });
            dialog.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
            return true;
        }

        return false;
    }

    private List<CharSequence> createItemsToShowList(StationOverlayItem item) {
        List<CharSequence> items = new ArrayList<CharSequence>();
        String[] phoneNo = item.getPhoneNo().split(",");

        for (String aPhoneNo : phoneNo) {
            items.add("Tel: " + aPhoneNo);
        }
        items.add("Pris: " + item.getPrice() + " kr");
        items.add("Öppetider: " + item.getOpeningHours());

        return items;
    }
}
