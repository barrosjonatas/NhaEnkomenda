package cv.cvmovel.enacol_nhaenkomenda;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import cv.cvmovel.enacol_nhaenkomenda.olc.Locality;
import cv.cvmovel.enacol_nhaenkomenda.olc.OpenLocationCode;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private Marker marker;
    private GoogleMap Mapa;
    protected double Latitude, Longitude;
    private OpenLocationCode olc;
    private LocationManager locationManager;
    private LocationListener locationListener;
    final String morada = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        // For showing a move to my location button
        //googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        final Location[] location = new Location[1]; // location

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location locate) {
                if (locate != null) {
                    location[0] = locate;
                    setLocation(location[0]);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };


        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            200);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                location[0] = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Mapa.setMyLocationEnabled(true);
            } else {
                // Show rationale and request permission.
                 Snackbar.make(this.getView(), "Sem permissões para aceder localização actual", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

            if (location[0] != null) {
                Latitude = location[0].getLatitude();
                Longitude = location[0].getLongitude();
            }
        }
        LatLng caboverde;
        CameraPosition cameraPosition;

        // For dropping a marker at a point on the Map
        if (Latitude != 0 && Longitude != 0) {
            caboverde = new LatLng(Latitude, Longitude);
            // For zooming automatically to the location of the marker
            cameraPosition = new CameraPosition.Builder().target(caboverde).zoom(18).build();
        } else {
            Latitude = 14.918287;
            Longitude = -23.512335;
            caboverde = new LatLng(Latitude, Longitude);
            // For zooming automatically to the location of the marker
            cameraPosition = new CameraPosition.Builder().target(caboverde).zoom(12).build();
        }

        olc = new OpenLocationCode(Latitude, Longitude, 11);

        Locality local = new Locality();
        String morada = "";
        try {
            morada = local.getNearestLocality(olc);
        } catch (Locality.NoLocalityException e) {
            e.printStackTrace();
        }

        this.getActivity().getIntent().putExtra("olc",olc.getCode());

        googleMap.addMarker(new MarkerOptions().position(caboverde).title("OLC: " + olc.getCode()+"/Morada:"+morada).snippet("Minha Localização Actual"));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latlng) {
                if (marker != null) {
                    marker.remove();
                }
                Mapa.clear();
                marker = Mapa.addMarker(new MarkerOptions().position(latlng).title("OLC: " + olc.getCode()+"/Morada:"+"PRAIA").snippet("Minha Localização Actual"));
            }
        });
    }

    private void setLocation(Location location) {
        if (Mapa != null) {
            Mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    location.getLatitude(), location.getLongitude()), 18.0f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}