package com.fefe.routesample;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * ルート検索するAPIを叩くサンプルアプリケーション
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    private SupportMapFragment mMapFragment;
    private LatLng start,end;
    private ArrayList<Polyline> mPolilines;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        start = new LatLng(34.7021644,135.499749);
        end = new LatLng(34.831436, 135.608139);

        /**
         * ルート検索APIに必要な情報を入れたオブジェクトを作成
         * travelMode -> 歩きで行くか、車で行くかなどを設定するメソッド
         * withListener -> ルート検索完了を受け取るコールバック
         * waypoints -> どこを通っていくかを決める（スタート地点とゴール地点は入れる）
         * build -> オブジェクトを作成
         */
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.WALKING)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {
                        Log.d("RoutingSuccess", e.toString());
                    }

                    @Override
                    public void onRoutingStart() {
                        Log.d("RoutingSuccess", "start");
                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> routes, int i) {
//                        if(mPolilines.size() == 0) {
//                            for(Polyline poli : mPolilines) {
//                                poli.remove();
//                            }
//                        }

                        mPolilines = new ArrayList<>();
                        for(Route route : routes) {
                            PolylineOptions options = new PolylineOptions();
                            options.color(Color.RED);
                            options.width(20);
                            options.addAll(route.getPoints());
                            Polyline polyline = mGoogleMap.addPolyline(options);
                            mPolilines.add(polyline);
                        }
                    }

                    @Override
                    public void onRoutingCancelled() {
                        Log.d("RoutingSuccess", "cancel");
                    }
                })
                .waypoints(start, end)
                .build();
        /**
         * ルート検索APIを作成
         */
        routing.execute();

    }

    /**
     * GoogleMapの表示準備が完了した時に呼ばれるコールバック
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        try {
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 16));
            googleMap.addMarker(new MarkerOptions()
                            .title("Now")
                            .position(start)
            );

            googleMap.addMarker(new MarkerOptions()
                            .title("MyHouse")
                            .position(end)
            );
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }catch(SecurityException e) {
            e.printStackTrace();
        }
    }
}
