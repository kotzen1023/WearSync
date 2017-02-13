package com.seventhmoon.wearsync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.seventhmoon.wearsync.Data.Constant.ACTION.GET_IMG_COMPLETE;

public class MainActivity extends WearableActivity {
    private static final String TAG = MainActivity.class.getName();

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private ImageView imageView;

    public static GoogleApiClient mGoogleApiClient;
    private static int count = 0;

    private BroadcastReceiver mBroadcastReceiver;
    private boolean isRegister;
    //public static byte[] imgBuffer;
    //public static long imgSize = 0;
    public static Bitmap imgBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        //mClockView = (TextView) findViewById(R.id.clock);
        Button btn = (Button) findViewById(R.id.button2);
        imageView = (ImageView) findViewById(R.id.imageView);

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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncSampleDataItem();
            }
        });

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                switch (action) {
                    case GET_IMG_COMPLETE:
                        //byte[] byteArray = intent.getByteArrayExtra("image");
                        //String size = intent.getStringExtra("length");
                        //Log.d(TAG, "size = "+imgBuffer.length);
                        //Bitmap bmp = BitmapFactory.decodeByteArray(imgBuffer, 0, imgBuffer.length);
                        if (imgBitmap != null)
                            imageView.setImageBitmap(imgBitmap);
                        break;
                }
            }
        };

        if (!isRegister) {


            IntentFilter filter = new IntentFilter();
            filter.addAction(GET_IMG_COMPLETE);
            registerReceiver(mBroadcastReceiver, filter);
            isRegister = true;
        }
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

        if (isRegister && mBroadcastReceiver != null) {

            try {
                unregisterReceiver(mBroadcastReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mBroadcastReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");

        }

        mGoogleApiClient.disconnect();
        super.onDestroy();

    }

    private void syncSampleDataItem() {
        Log.d(TAG, "syncSampleDataItem");
        if(mGoogleApiClient==null)
            return;

        final PutDataMapRequest putRequest = PutDataMapRequest.create("/WEAR");
        final DataMap map = putRequest.getDataMap();
        //map.putInt("color", Color.RED);
        map.putString("wear_call", "call "+count);
        count++;
        Wearable.DataApi.putDataItem(mGoogleApiClient,  putRequest.asPutDataRequest());
    }
}
