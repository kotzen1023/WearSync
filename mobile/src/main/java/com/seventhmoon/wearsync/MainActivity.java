package com.seventhmoon.wearsync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private GoogleApiClient mGoogleApiClient;
    private static int count = 0;
    Bitmap mBitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncSampleDataItem();
            }
        });

        Button btn2 = (Button) findViewById(R.id.button3);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAssetAndFinish(R.drawable.cb11);
            }
        });

        Button btn3 = (Button) findViewById(R.id.button4);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAssetAndFinish(R.drawable.cb12);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        mGoogleApiClient.disconnect();
        super.onDestroy();

    }

    private void syncSampleDataItem() {
        Log.d(TAG, "syncSampleDataItem");
        if(mGoogleApiClient==null)
            return;

        final PutDataMapRequest putRequest = PutDataMapRequest.create("/MOBILE");
        final DataMap map = putRequest.getDataMap();
        map.putInt("color", Color.RED);
        map.putString("string_example", "Sample String"+count);
        count++;
        Wearable.DataApi.putDataItem(mGoogleApiClient,  putRequest.asPutDataRequest());
    }

    private void sendAssetAndFinish(final int id) {
        Log.d(TAG, "sendAssetAndFinish");
        // create an Asset
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        /*mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                 R.drawable.cb11 ), 320, 320, false);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        Asset asset = Asset.createFromBytes(baos.toByteArray());

        // send Asset
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/PIC");
        final DataMap map = putDataMapReq.getDataMap();
        map.putAsset("assetbody", asset);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);*/



        //finish();
        new Thread() {

            @Override

            public void run() {
                Log.d(TAG, "send start");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
                final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                Asset asset = Asset.createFromBytes(byteStream.toByteArray());
                //Asset asset = createAssetFromBitmap(bitmap);
                PutDataMapRequest dataMap = PutDataMapRequest.create("/PIC");
                dataMap.getDataMap().putAsset("profileImage", asset);
                dataMap.getDataMap().putLong("datasize", byteStream.size());
                dataMap.getDataMap().putLong("count", count);
                PutDataRequest request = dataMap.asPutDataRequest();
                PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                        .putDataItem(mGoogleApiClient, request);
                count++;
                Log.d(TAG, "send end");
            }
        }.start();

    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }
}
