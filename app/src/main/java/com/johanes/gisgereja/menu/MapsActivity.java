package com.johanes.gisgereja.menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.johanes.gisgereja.R;
import com.johanes.gisgereja.helper.DatabaseHelper;
import com.johanes.gisgereja.utils.Gereja;

import java.util.List;

//import com.google.android.gms.location.LocationListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private double cLat, cLong;
    private GoogleMap mMap;
    private Marker cMarker, gMarker;
    private Polyline cPoly;

    private Gereja gClosest;
    private LatLng gLoc;

    private LocationManager locationManager;
    private DatabaseHelper dh;

    private boolean fixed_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dh = DatabaseHelper.openSharedDB(this); // new DatabaseHelper(this);

        fixed_mode = false;
        Intent in = getIntent();
        if (in.hasExtra("id")) {
            fixed_mode = true;

            gClosest = new Gereja();

            gClosest.setId(in.getIntExtra("id", 0));

            gClosest.setName(in.getStringExtra("nama"));
            gClosest.setAlamat(in.getStringExtra("alamat"));
            gClosest.setDeskripsi(in.getStringExtra("deskripsi"));
            gClosest.setLati(in.getStringExtra("lat"));
            gClosest.setLongi(in.getStringExtra("long"));
            gClosest.setImage(in.getByteArrayExtra("img"));

            gLoc = new LatLng(Double.parseDouble(gClosest.getLati()), Double.parseDouble(gClosest.getLongi()));
        }
    }

    Gereja getClosestChurch(LatLng myPos) {
        double closestNow = Double.POSITIVE_INFINITY;
        Gereja g = null;
        double lt, lg, jarak;

        List<Gereja> churches = dh.getAllGereja();
        for (Gereja x : churches) {
            lt = Double.parseDouble(x.getLati());
            lg = Double.parseDouble(x.getLongi());
            jarak = Math.sqrt(Math.pow(lt - myPos.latitude, 2) + Math.pow(lg - myPos.longitude, 2));

            if (jarak < closestNow) {
                closestNow = jarak;
                g = x;
            }
        }

        return g;
    }

    android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            cLat = location.getLatitude();
            cLong = location.getLongitude();
            LatLng nowp = new LatLng(cLat, cLong);

            if (!fixed_mode) {
                gClosest = getClosestChurch(nowp);
                if (gClosest != null)
                    gLoc = new LatLng(Double.parseDouble(gClosest.getLati()), Double.parseDouble(gClosest.getLongi()));
            }

            String msg = "Posisi Berubah!";
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();

            if (cMarker != null) cMarker.remove();
            cMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(cLat, cLong)).title("Posisi Sekarang"));

            if (gClosest != null) {
                if (cPoly != null) cPoly.remove();
                cPoly = mMap.addPolyline(new PolylineOptions()
                        .clickable(true).color(Color.BLUE).add(nowp).add(gLoc));

                if (!fixed_mode) {
                    byte[] bm = gClosest.getImage();
                    Bitmap bi = BitmapFactory.decodeByteArray(bm, 0, bm.length);
                    bi = Bitmap.createScaledBitmap(bi, 100, 100, false);

                    if (gMarker != null) gMarker.remove();
                    gMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(gClosest.getLati()), Double.parseDouble(gClosest.getLongi())))
                            .icon(BitmapDescriptorFactory.fromBitmap(bi))
                            .alpha(0.9f)
                            .title(gClosest.getName()));
                }
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(nowp);
            if (gClosest != null) builder.include(gLoc);
            LatLngBounds bounds = builder.build();

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
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
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListenerGPS);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, locationListenerGPS);

        //DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
        /*List<Gereja> lg = dh.getAllGereja();

        for(Gereja g: lg){
            byte[] bm = g.getImage();
            Bitmap bi = BitmapFactory.decodeByteArray(bm,0,bm.length);
            bi = Bitmap.createScaledBitmap(bi,100,100,false);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(g.getLati()), Double.parseDouble(g.getLongi())))
                    .icon(BitmapDescriptorFactory.fromBitmap(bi))
                    .alpha(0.9f)
                    .title(g.getName()));
        }*/

        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(loc != null){
            Toast.makeText(getBaseContext(),"Menggunakan Posisi Terakhir!",Toast.LENGTH_LONG).show();

            LatLng yourPosNow = new LatLng(loc.getLatitude(), loc.getLongitude());
            //LatLng yourPosNow = new LatLng(-8.5617, 115.1771);

            if(!fixed_mode){
                gClosest = getClosestChurch(yourPosNow);
                if(gClosest != null) gLoc = new LatLng(Double.parseDouble(gClosest.getLati()), Double.parseDouble(gClosest.getLongi()));
                else Toast.makeText(getBaseContext(),"Data Gereja Masih Kosong!",Toast.LENGTH_LONG).show();
            }

            if(gClosest != null){
                byte[] bm = gClosest.getImage();
                Bitmap bi = BitmapFactory.decodeByteArray(bm,0,bm.length);
                bi = Bitmap.createScaledBitmap(bi,100,100,false);
                gMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(gClosest.getLati()), Double.parseDouble(gClosest.getLongi())))
                        .icon(BitmapDescriptorFactory.fromBitmap(bi))
                        .alpha(0.9f)
                        .title(gClosest.getName()));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(yourPosNow);
            if(gClosest != null) builder.include(gLoc);
            LatLngBounds bounds = builder.build();

            //mMap.setPadding(100,100,100,100);
            cMarker = mMap.addMarker(new MarkerOptions().position(yourPosNow).title("Posisi Sekarang"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));

            if(gClosest != null)
                cPoly = mMap.addPolyline(new PolylineOptions()
                        .clickable(true).color(Color.BLUE).add(yourPosNow).add(gLoc));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(yourPosNow));
        } else {
            Toast.makeText(getBaseContext(),"Belum Mendapatkan Data Lokasi!",Toast.LENGTH_LONG).show();
        }
    }
}
