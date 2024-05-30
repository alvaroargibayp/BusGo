
package udc.psi.busgo.tabs;


import static udc.psi.busgo.StringUtils.decompress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;


import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import udc.psi.JsonUtils;
import udc.psi.busgo.MainActivity;
import udc.psi.busgo.R;
import udc.psi.busgo.WorkerMap;
import udc.psi.busgo.objects.BusStopClusterItem;
import udc.psi.busgo.objects.BusStopClusterRenderer;

public class MapTab extends Fragment implements GoogleMap.OnMarkerClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TASK_KEY = "Input_TASK_KEY";
    Marker originMarker = null;
    Marker destinationMarker = null;
    Button searchButton;
    List<LatLng> stopsCoordsList = new ArrayList<>();
    WorkManager workManager;
    volatile boolean workerEnded = false;

    // Declare a variable for the cluster manager.
    private ClusterManager<BusStopClusterItem> clusterManager;

    private void setUpClusterer(GoogleMap googleMap) {
        // Position the map.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = new ClusterManager<BusStopClusterItem>(requireActivity().getApplicationContext(), googleMap);
        clusterManager.setRenderer(new BusStopClusterRenderer(requireActivity().getApplicationContext(), googleMap, clusterManager));
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItemsToCluster();
    }

    private boolean parseJson(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            for (int i = 0; i < jsonObject.getJSONArray("paradas").length(); i++){
                JSONObject currentObject = (JSONObject) jsonObject.getJSONArray("paradas").get(i);

                double coordsLat = currentObject.getJSONArray("coords").getDouble(1);
                double coordsLong = currentObject.getJSONArray("coords").getDouble(0);

                LatLng coords = new LatLng(coordsLat, coordsLong);
                Log.d("_TAG2", coords.toString());

                stopsCoordsList.add(coords);
                Log.d("_TAG3", stopsCoordsList.toString());
            }
            return true;
        } catch (JSONException e) {
            Log.e("MapTabTag", "Error parsing JSON", e);
            return false;
        }
    }
    private void addItemsToCluster() {

        // Set some lat/lng coordinates to start with.
        /*double lat = 43.33920463853277;
        double lng = -8.437498363975866;
        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            BusStopClusterItem offsetItem = new BusStopClusterItem(lat, lng, "Title " + i, "Snippet " + i);
            clusterManager.addItem(offsetItem);
        }*/

        for (LatLng coords : stopsCoordsList) {
            BusStopClusterItem offsetItem = new BusStopClusterItem(coords.latitude, coords.longitude, "Title " + coords.latitude, "Snippet " + coords.longitude);
            clusterManager.addItem(offsetItem);
        }
    }

    public void placeMarker(GoogleMap googleMap, LatLng latLng, int which) {
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

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

    public MapTab() {
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

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(WorkerMap.class).build();

        WorkManager workManager1 = WorkManager.getInstance(requireActivity().getApplicationContext());
        workManager1.enqueue(workRequest);

        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

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

                workManager1.getWorkInfoByIdLiveData(workRequest.getId())
                        .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                try {
                                    if (workInfo.getState().isFinished()) {
                                        // Cuando se obtenga el json, parsealo en un hilo a parte
                                        String jsonString = JsonUtils.readJsonFromFile(requireActivity().getApplicationContext(), "paradas.json");

                                        if (jsonString != null) {
                                            Log.d("MapTabTag", "Starting HandlerThread to parse JSON");

                                            // Start a new HandlerThread for parsing the JSON
                                            HandlerThread handlerThread = new HandlerThread("JsonParseThread");
                                            handlerThread.start();
                                            Handler handler = new Handler(handlerThread.getLooper());

                                            CountDownLatch parseLatch = new CountDownLatch(1);

                                            handler.post(() -> {
                                                boolean parseSuccess = parseJson(jsonString);
                                                if (parseSuccess) {
                                                    Log.d("MapTabTag", "JSON parsed successfully");
                                                    new Handler(Looper.getMainLooper()).post(() -> setUpClusterer(googleMap));
                                                } else {
                                                    Log.d("MapTabTag", "Failed to parse JSON");
                                                }
                                                parseLatch.countDown();
                                            });
                                        }


                                    }
                                } catch (NullPointerException e) {
                                    Log.e("TAG", "onChanged: " + e.getLocalizedMessage());
                                } /*catch (IOException e) {
                                    throw new RuntimeException(e);
                                }*/
                            }
                        });

                googleMap.setOnMyLocationButtonClickListener(MapTab.this);
                googleMap.setOnMyLocationClickListener(MapTab.this);
                enableMyLocation(googleMap); // Si el usuario da permisos de localización, dibujar el boton de ir a ubicacion

                LatLngBounds cityBounds = new LatLngBounds(
                        new LatLng(43.33920463853277, -8.437498363975866), // Suroeste de la ciudad (latitud, longitud)
                        new LatLng(43.39274161525237, -8.379991804710077)); // Noreste de la ciudad (latitud, longitud)

                //googleMap.setLatLngBoundsForCameraTarget(cityBounds);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityBounds.getCenter(), 12));

                //googleMap.setMinZoomPreference(12);

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

}