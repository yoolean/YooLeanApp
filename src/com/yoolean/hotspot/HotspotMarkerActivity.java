package com.yoolean.hotspot;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.yoolean.client.HotSpotClient;
import com.yoolean.common.model.HotArea;
import com.yoolean.common.model.HotSpot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by chenhang on 2015/3/26.
 */
public class HotspotMarkerActivity extends Activity implements LocationSource,
        AMapLocationListener, View.OnClickListener, AMap.OnMarkerClickListener {
    private TextView status;
    private Button scanner;
    private Button marker;
    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener myLocationChangedListener;
    private LocationManagerProxy locationManager;
    private List<MarkerOptions> hotSpotMarkerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.hotspot_marker_map);
        mapView.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        status = (TextView) findViewById(R.id.hotspot_marker_status);
        scanner = (Button) findViewById(R.id.scanner);
        scanner.setOnClickListener(this);
        marker = (Button) findViewById(R.id.marker);
        marker.setOnClickListener(this);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMarkerClickListener(this);
    }

    /**
     * 此方法需存在
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 此方法需存在
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 此方法需存在
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 此方法已经废弃
     */
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (myLocationChangedListener != null && amapLocation != null) {
            if (amapLocation.getAMapException().getErrorCode() == 0) {
                myLocationChangedListener.onLocationChanged(amapLocation);
                CachedLocation.put(amapLocation.getLatitude(), amapLocation.getLongitude());
            }
        }
    }

    private void updateStatus() {
        status.setText(CachedLocation.get().getLatitude() + "," + CachedLocation.get().getLongitude());
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        myLocationChangedListener = listener;
        if (locationManager == null) {
            locationManager = LocationManagerProxy.getInstance(this);
            locationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 10 * 1000, 2, this);
            locationManager.setGpsEnable(true);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        myLocationChangedListener = null;
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.destroy();
        }
        locationManager = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanner:
                updateHotSpot();
                break;
            case R.id.marker:
                addHotSpot();
                break;
            default:
        }
    }

    private void addHotSpot() {
        status.setText(CachedLocation.get().getLatitude() + "," + CachedLocation.get().getLongitude());
        new NewHotSpotDialogFragment().show(getFragmentManager(), null);
    }

    private void updateHotSpot() {
        hotSpotMarkerList.clear();
        getHotArea();
        aMap.clear();
        addHotSpotToMap();
    }

    private void getHotArea() {
        HotArea hotArea = getHotAreaFromServer();
        convertToMarkers(hotArea);
    }

    private void convertToMarkers(HotArea hotArea) {
        for (HotSpot hotSpot : hotArea.getHotSpots()) {
            MarkerOptions marker = new MarkerOptions();
            marker.title(hotSpot.getTitle());
            com.yoolean.common.model.Location location = hotSpot.getLocation();
            marker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            hotSpotMarkerList.add(marker);
        }
    }

    private HotArea getHotAreaFromServer() {
        HotArea hotArea = new HotArea();
        HotAreaTask hotAreaTask = new HotAreaTask(getString(R.string.yoolean_platform_url), CachedLocation.get(), 5000);
        Future<HotArea> task = Executors.newSingleThreadExecutor().submit(hotAreaTask);
        try {
            hotArea = task.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hotArea;
    }

    private void addHotSpotToMap() {
        for (MarkerOptions hotSpot : hotSpotMarkerList) {
            aMap.addMarker(hotSpot);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }
}
