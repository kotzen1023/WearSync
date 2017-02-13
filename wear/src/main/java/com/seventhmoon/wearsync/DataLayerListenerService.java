package com.seventhmoon.wearsync;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.seventhmoon.wearsync.Data.Constant.ACTION.GET_IMG_COMPLETE;

import static com.seventhmoon.wearsync.MainActivity.imgBitmap;
import static com.seventhmoon.wearsync.MainActivity.mGoogleApiClient;


public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = DataLayerListenerService.class.getName();

    int TIMEOUT_MS = 10000;
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            Log.d(TAG, "path = "+path);
            if("/MOBILE".equals(path)) {
                Log.d(TAG, "/MOBILE");
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                int color = map.getInt("color");
                String stringExample = map.getString("string_example");
                Log.d(TAG, "stringExample = "+stringExample+" color = "+color);
            }
            else if("/PIC".equals(path)) {
                Log.d(TAG, "/PIC");

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("profileImage");
                imgBitmap = loadBitmapFromAsset(profileAsset);

                //final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                //Asset asset = map.getAsset("profileImage");
                Long size = dataMapItem.getDataMap().getLong("datasize");
                Long count = dataMapItem.getDataMap().getLong("count");

                //imgBuffer = map.toByteArray();
                //int color = map.getInt("color");
                //String stringExample = map.getString("string_example");
                Log.d(TAG, "receive asset ! size = "+size+" count = "+count);




                Intent sendIntent = new Intent();
                sendIntent.setAction(GET_IMG_COMPLETE);
                sendBroadcast(sendIntent);
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }
}
