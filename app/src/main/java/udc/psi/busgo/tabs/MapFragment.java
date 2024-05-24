
package udc.psi.busgo.tabs;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import udc.psi.busgo.MainActivity;
import udc.psi.busgo.R;
import udc.psi.busgo.objects.Line;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    Marker originMarker = null;
    Marker destinationMarker = null;

    Button searchButton;

    List<LatLng> stopsCoordsList = new ArrayList<>();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean permissionDenied = false;
    public void placeMarker(GoogleMap googleMap, LatLng latLng, int which) {
        MarkerOptions markerOptions=new MarkerOptions();
        // Set position of marker
        markerOptions.position(latLng);
        // Set title of marker
        markerOptions.title(latLng.latitude+" : "+ latLng.longitude);

        if (which == 0) {
            if (originMarker != null)
                originMarker.remove();

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            originMarker = googleMap.addMarker(markerOptions);
        }
        if (which == 1) {
            if (destinationMarker != null)
                destinationMarker.remove();

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            destinationMarker = googleMap.addMarker(markerOptions);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {

        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    public interface OnMapClickedListener {
        public void onMapClicked(GoogleMap googleMap, LatLng latLng);
    }

    OnMapClickedListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnMapClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement OnMapClickedListener");
        }
    }
    void setUpBusStops(GoogleMap googleMap) {
        /*double lat = 0;
        double lng = 0;
        double lat = 43.36086451031581;
        double lng = -8.412490223028753;



        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;

            MarkerOptions markerOptions=new MarkerOptions();
            LatLng teatroPos = new LatLng(lat, lng);
            markerOptions.position(teatroPos);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            markerOptions.title(teatroPos.latitude+" : "+teatroPos.longitude);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(teatroPos,10));
            googleMap.addMarker(markerOptions);
        }*/

        Log.d("_TAG3", stopsCoordsList.toString());


        for (LatLng coord : stopsCoordsList) {
            MarkerOptions markerOptions=new MarkerOptions();

            LatLng teatroPos = new LatLng(coord.latitude, coord.longitude);
            markerOptions.position(teatroPos);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            markerOptions.title(teatroPos.latitude+" : "+teatroPos.longitude);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(teatroPos,10));
            googleMap.addMarker(markerOptions);


        }
    }
    /*
    NavigationApi.getNavigator(this, new NavigationApi.NavigatorListener() {
        /**
         * Sets up the navigation UI when the navigator is ready for use.
        @Override
        public void onNavigatorReady(Navigator navigator) {
            displayMessage("Navigator ready.");
            mNavigator = navigator;
            mNavFragment = (NavigationFragment) getFragmentManager()
                    .findFragmentById(R.id.navigation_fragment);

            // Optional. Disable the guidance notifications and shut down the app
            // and background service when the user closes the app.
            // mNavigator.setTaskRemovedBehavior(Navigator.TaskRemovedBehavior.QUIT_SERVICE)

            // Optional. Set the last digit of the car's license plate to get
            // route restrictions for supported countries.
            // mNavigator.setLicensePlateRestrictionInfo(getLastDigit(), "BZ");

            // Set the camera to follow the device location with 'TILTED' driving view.
            mNavFragment.getCamera().followMyLocation(Camera.Perspective.TILTED);

            // Set the travel mode (DRIVING, WALKING, CYCLING, TWO_WHEELER, or TAXI).
            mRoutingOptions = new RoutingOptions();
            mRoutingOptions.travelMode(RoutingOptions.TravelMode.DRIVING);

            // Navigate to a place, specified by Place ID.
            navigateToPlace(SYDNEY_OPERA_HOUSE, mRoutingOptions);
        }

         * Handles errors from the Navigation SDK.
         * @param errorCode The error code returned by the navigator.
        @Override
        public void onError(@NavigationApi.ErrorCode int errorCode) {
            switch (errorCode) {
                case NavigationApi.ErrorCode.NOT_AUTHORIZED:
                    displayMessage("Error loading Navigation SDK: Your API key is "
                            + "invalid or not authorized to use the Navigation SDK.");
                    break;
                case NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED:
                    displayMessage("Error loading Navigation SDK: User did not accept "
                            + "the Navigation Terms of Use.");
                    break;
                case NavigationApi.ErrorCode.NETWORK_ERROR:
                    displayMessage("Error loading Navigation SDK: Network error.");
                    break;
                case NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING:
                    displayMessage("Error loading Navigation SDK: Location permission "
                            + "is missing.");
                    break;
                default:
                    displayMessage("Error loading Navigation SDK: " + errorCode);
            }
        }
    });
*/
    @Override
    public boolean onMarkerClick(final Marker marker) {

        Integer clickCount = (Integer) marker.getTag();
        //handle click here

        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(requireActivity().getApplicationContext(),
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        Log.d("_TAG", "TGAAGADGDAFA: " + getTag());

        View view=inflater.inflate(R.layout.fragment_map, container, false);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        searchButton = view.findViewById(R.id.searchRoutesButtonId);
        searchButton.setOnClickListener(this);

        //searchAllStops();

        // Async map
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // When map is loaded
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        listener.onMapClicked(googleMap, latLng);
                    }
                });

                googleMap.setOnMyLocationButtonClickListener(MapFragment.this);
                googleMap.setOnMyLocationClickListener(MapFragment.this);
                enableMyLocation(googleMap);

                LatLngBounds cityBounds = new LatLngBounds(
                        new LatLng(43.33920463853277, -8.437498363975866), // Suroeste de la ciudad (latitud, longitud)
                        new LatLng(43.39274161525237, -8.379991804710077)); // Noreste de la ciudad (latitud, longitud)

                //googleMap.setLatLngBoundsForCameraTarget(cityBounds);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityBounds.getCenter(), 12));

                googleMap.setMinZoomPreference(12);

                //setUpBusStops(googleMap);
            }
        });
        // Return view
        return view;
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation(GoogleMap googleMap) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            if (mainActivity.checkLocationPermission())
                googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(requireActivity().getApplicationContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    void searchAllStops(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/paradas",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("_TAG", response.toString());
                        try {
                            ArrayList<Line> stops = new ArrayList<Line>();
                            for (int i = 0; i < response.getJSONArray("paradas").length(); i++){
                                JSONObject currentObject = (JSONObject) response.getJSONArray("paradas").get(i);

                                double coordsLat = currentObject.getJSONArray("coords").getDouble(1);
                                double coordsLong = currentObject.getJSONArray("coords").getDouble(0);

                                LatLng coords = new LatLng(coordsLat, coordsLong);
                                Log.d("_TAG2", coords.toString());


                                stopsCoordsList.add(coords);
                                Log.d("_TAG3", stopsCoordsList.toString());

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja errores aquí
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }




}