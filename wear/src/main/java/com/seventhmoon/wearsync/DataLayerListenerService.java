package com.seventhmoon.wearsync;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;


public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = DataLayerListenerService.class.getName();
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            if("/MOBILE".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                int color = map.getInt("color");
                String stringExample = map.getString("string_example");
                Log.d(TAG, "stringExample = "+stringExample+" color = "+color);
            }
            else if("/NEW_PIC".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                Asset asset = map.getAsset("assetbody");

                byte[] data = asset.getData();
                //int color = map.getInt("color");
                //String stringExample = map.getString("string_example");
                Log.d(TAG, "receive asset ! size = "+data.length);
            }
        }
    }
}
