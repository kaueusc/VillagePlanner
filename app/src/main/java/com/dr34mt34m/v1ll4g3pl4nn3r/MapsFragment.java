package com.dr34mt34m.v1ll4g3pl4nn3r;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dr34mt34m.v1ll4g3pl4nn3r.components.Place;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.FirebaseHelper;
import com.dr34mt34m.v1ll4g3pl4nn3r.dataFetchers.MapsHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsFragment extends Fragment implements GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowCloseListener, ClusterManager.OnClusterItemClickListener<PlaceMarker>, GoogleMap.InfoWindowAdapter {
    private static final String TAG = MapsFragment.class.getName();
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final long GEOFENCE_RADIUS_IN_METERS = 5;
    private FusedLocationProviderClient fusedLocationClient;
    // Allows class to cancel the location request if it exits the activity.
    // Typically, you use one cancellation source per lifecycle.
    private final CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    // setup the geofencing client variable
    private GeofencingClient geofencingClient;

    private List<Geofence> geofenceList = new ArrayList<>();

    private GoogleMap map;
    private Polyline curPolyline;

    private boolean darkMode = false;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            // Center map around village
            LatLng village = new LatLng(34.025876978440984, -118.28501948275283);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(village, 17f));

            // Enable location
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);

            // Set up cluster manager
            populateCluster();

            map.setOnMapClickListener(MapsFragment.this::onMapClick);
            map.setOnInfoWindowCloseListener(MapsFragment.this::onInfoWindowClose);
            map.setInfoWindowAdapter(MapsFragment.this);

            if (darkMode) {
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle_night));
            }
        }
    };

    @Override
    public void onInfoWindowClose(@NonNull Marker marker) {
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
//        for (Marker m : placeMarkers) {
//            m.showInfoWindow();
//        }
//        hideNavigateBtn();
    }

    @Override
    public boolean onClusterItemClick(PlaceMarker item) {
//        showNavigateBtn(item.getPlace());
        onNavigate(item.getPlace());
        return false;
    }

    public void populateCluster() {
//        if (clusterManager != null) {
////            clusterManager.clearItems();
////            clusterManager.cluster();
//            return;
//        }

        ClusterManager<PlaceMarker> c = new ClusterManager<>(getActivity(), map);
        c.setRenderer(new PlaceRenderer(getActivity(), map, c));
        map.setOnCameraIdleListener(c);
        map.setOnMarkerClickListener(c);
        c.setOnClusterItemClickListener(MapsFragment.this::onClusterItemClick);

        // Get places array
        FirebaseHelper.loadPlaces((Map<String, Place> places) -> {
            for (Place p : places.values()) {
                c.addItem(new PlaceMarker(p));
            }
            c.cluster();

            return null;
        });
    }

    public void onNavigate(Place place) {
        requestCurrentLocation((Location l) -> {
            LatLng currentLocation = new LatLng(l.getLatitude(), l.getLongitude());
            MapsHelper.getRoute(currentLocation, place.getLocation(), (String polyline) -> {
                List<LatLng> path = PolyUtil.decode(polyline);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (curPolyline != null)
                            curPolyline.remove();
                        curPolyline = map.addPolyline(new PolylineOptions()
                                .add(path.toArray(new LatLng[0]))
                                .color(Color.BLUE)
                                .width(25)
                                .pattern(Arrays.asList(
                                    new Dot(), new Gap(10)
                                ))
                        );
                    }
                });
                return null;
            });
            return null;
        });
    }

    private void showNavigateBtn(Place place) {
        hideNavigateBtn();
        String placeName = place.getName();

        LayoutInflater inflater = getLayoutInflater();
        ConstraintLayout layout = (ConstraintLayout) getView();

        View btnContainer = inflater.inflate(R.layout.navigate_button, layout, false);
        Button btn = btnContainer.findViewById(R.id.navigate_button);
        btn.setText("Navigate to " + placeName);
        btn.setOnClickListener((View v) -> {
            onNavigate(place);
        });
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        btnContainer.setLayoutParams(lp);

        layout.addView(btnContainer);
    }

    private void hideNavigateBtn() {
        View btnContainer = getView().findViewById(R.id.navigate_button_container);
        if (btnContainer != null) {
            ((ViewGroup) btnContainer.getParent()).removeView(btnContainer);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        if(EasyPermissions.hasPermissions(this.getContext(), perms)) {
            Toast.makeText(this.getContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void requestCurrentLocation(Function<Location, Void> callback) {
        Log.d(TAG, "requestCurrentLocation()");
        // Request permission
        if (ActivityCompat.checkSelfPermission(
                this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            // Main code
            Task<Location> currentLocationTask = fusedLocationClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.getToken()
            );

            currentLocationTask.addOnCompleteListener((new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    String result = "";

                    if (task.isSuccessful()) {
                        // Task completed successfully
                        Location location = task.getResult();
                        result = "Location (success): " +
                                location.getLatitude() +
                                ", " +
                                location.getLongitude();
                        callback.apply(location);
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        result = "Exception thrown: " + exception;
                    }

                    System.out.println("getCurrentLocation() result: " + result);
                }
            }));
        } else {
            // Request fine location permission
            requestLocationPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        requestLocationPermission();
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the location client and the geofencing client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        geofencingClient = LocationServices.getGeofencingClient(this.getActivity());
    }

    public void createGeofence(Place place) {
        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(place.getId())

                .setCircularRegion(
                        place.getLocation().latitude,
                        place.getLocation().longitude,
                        GEOFENCE_RADIUS_IN_METERS
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        return new View(getActivity());
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return new View(getActivity());
    }
}

class PlaceRenderer extends DefaultClusterRenderer<PlaceMarker> {
    private Context context;

    public PlaceRenderer(Context context, GoogleMap map, ClusterManager<PlaceMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull PlaceMarker placeMarker, MarkerOptions markerOptions) {
        // Draw a single person - show their profile photo and set the info window to show their name
        markerOptions
                .icon(getItemIcon(placeMarker.getPlace()))
                .title(getItemTitle(placeMarker.getPlace()));
    }

    @Override
    protected void onClusterItemUpdated(@NonNull PlaceMarker placeMarker, Marker marker) {
        // Same implementation as onBeforeClusterItemRendered() (to update cached markers)
        marker.setIcon(getItemIcon(placeMarker.getPlace()));
        marker.setTitle(getItemTitle(placeMarker.getPlace()));
    }

    private BitmapDescriptor getItemIcon(Place p) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setBackground(context.getDrawable(R.drawable.marker_rectangle));
        View inflatedView = View.inflate(context, R.layout.custom_marker, null);
        ((TextView) inflatedView.findViewById(R.id.custom_marker_title)).setText(p.getName());
        ((TextView) inflatedView.findViewById(R.id.custom_marker_wait_time)).setText("" + Math.round(p.getWaitTime()));
        iconGenerator.setContentView(inflatedView);
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon());
        return markerIcon;
    }

    private String getItemTitle(Place p) {
        String name = p.getName();
        long waitTime = Math.round(p.getWaitTime());
        return String.format("%s_%d", name, waitTime);
    }
}

class PlaceMarker implements ClusterItem {
    private Place place;
    private LatLng position;

    public PlaceMarker(Place place) {
        this.place = place;
        this.position = place.getLocation();
    }

    public Place getPlace() {
        return place;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }
}