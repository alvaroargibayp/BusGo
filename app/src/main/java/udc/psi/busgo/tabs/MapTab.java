
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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import udc.psi.JsonUtils;
import udc.psi.busgo.MainActivity;
import udc.psi.busgo.R;
import udc.psi.busgo.WorkerMap;
import udc.psi.busgo.objects.BusStopClusterItem;
import udc.psi.busgo.objects.BusStopClusterRenderer;
import udc.psi.busgo.objects.MarkerConDistancia;

public class MapTab extends Fragment implements GoogleMap.OnMarkerClickListener, View.OnClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TASK_KEY = "Input_TASK_KEY";
    Marker originMarker = null;
    Marker destinationMarker = null;
    Button searchButton;
    List<Pair<Integer, LatLng>> stopsCoordsList = new ArrayList<>();
    WorkManager workManager;
    volatile boolean workerEnded = false;

    LinearLayout routesLayout;

    Button nextRouteButton;
    Button previousRouteButton;

    TextView lineName;
    List<Pair<Integer, Pair<Integer, MarkerConDistancia>>> marcadoresCercanosOrigen = new ArrayList<>();
    List<Pair<Integer, Pair<Integer, MarkerConDistancia>>> marcadoresCercanosDestino = new ArrayList<>();

    List<Pair<Integer, MarkerOptions>> currentMapMarkers = new ArrayList<>();
    List<Pair<Integer, String>> listaLineasOrigen = new ArrayList<>();
    List<Pair<Integer, String>> listaLineasDestino = new ArrayList<>();

    List<Pair<Integer, List<Pair<Integer, String>>>> listaDeListasDeParadasOrigen = new ArrayList<>();
    List<Pair<Integer, List<Pair<Integer, String>>>> listaDeListasDeParadasDestino = new ArrayList<>();

    List<Pair<String, Pair<Integer, Integer>>> rutasList = new ArrayList<>();

    int currentRouteIndex = 0;



    // Cada indice es una parada (Pair)
    // Cada parada (Pair) contiene (IDParada, Lista de lineas (Pair))
    // Cada lista de lineas (Pair) contiene (IDLinea, NombreLinea)

    // Declare a variable for the cluster manager.
    private ClusterManager<BusStopClusterItem> clusterManager;

    private void calcularRutas() {
        // Por cada parada en el origen...
        // Por cada linea en la parada de origen...
        // Si esa linea esta en alguna parada del destino...
        // Marcar linea como valida
        // Obtener el id de la parada de origen y destino


        Log.d("TAG_RUTAS", "ORIGEN: " + listaDeListasDeParadasOrigen.toString());
        Log.d("TAG_RUTAS", "DESTINO: " + listaDeListasDeParadasDestino.toString());

        for (Pair<Integer, List<Pair<Integer, String>>> paradaOrigen : listaDeListasDeParadasOrigen ) { // Por cada parada en origen...
            for (Pair<Integer, String> lineaOrigen : paradaOrigen.second) { // Por cada linea en la parada de origen...
                for (Pair<Integer, List<Pair<Integer, String>>> paradaDestino : listaDeListasDeParadasDestino) {
                    for (Pair<Integer, String> lineaDestino : paradaDestino.second) {
                        if (!paradaDestino.first.equals(paradaOrigen.first)) { // Si la parada de destino no es la misma que la de origen...
                            if (lineaOrigen.first.equals(lineaDestino.first)) { // Si esa linea esta en alguna parada de destino...
                                Pair<Integer, Integer> paradasOrigenDestino = new Pair<>(paradaOrigen.first, paradaDestino.first);
                                Pair<String, Pair<Integer, Integer>> ruta = new Pair<>(lineaOrigen.second, paradasOrigenDestino);
                                Log.d("TAG_RUTAS", "Ruta añadida: " + ruta);

                                rutasList.add(ruta); // Se añade la ruta a la lista (IdLinea, (IdParadaOrigen, IdParadaDestino))
                            }
                        }
                    }
                }
            }
        }

        rutasList = removeDuplicates(rutasList);

        Log.d("TAG_RUTAS", rutasList.toString());
    }

    public static List<Pair<String, Pair<Integer, Integer>>> removeDuplicates(List<Pair<String, Pair<Integer, Integer>>> rutasList) {
        Set<String> seenStrings = new HashSet<>();
        List<Pair<String, Pair<Integer, Integer>>> uniqueList = new ArrayList<>();

        for (Pair<String, Pair<Integer, Integer>> pair : rutasList) {
            if (!seenStrings.contains(pair.first)) {
                seenStrings.add(pair.first);
                uniqueList.add(pair);
            }
        }

        return uniqueList;
    }



    private void setUpClusterer(GoogleMap googleMap) {
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

                int stopId = currentObject.getInt("id");

                JSONArray jsonArray = currentObject.getJSONArray("lineas");


                LatLng coords = new LatLng(coordsLat, coordsLong);
                Log.d("_TAG2", coords.toString());


                Pair<Integer, LatLng> pair = new Pair<Integer, LatLng>(stopId, coords);
                stopsCoordsList.add(pair);

                Log.d("_TAG3", stopsCoordsList.toString());
            }
            return true;
        } catch (JSONException e) {
            Log.e("MapTabTag", "Error parsing JSON", e);
            return false;
        }
    }

    private List<Pair<Integer, String>> parseStopJson(String jsonString, int stopJsonPosition) {
        List<Pair<Integer, String>> listaLineas = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            int jsonArrayLength = jsonObject.getJSONArray("paradas").getJSONObject(stopJsonPosition).getJSONArray("lineas").length();

            for (int i = 0; i < jsonArrayLength; i++) {
                JSONObject currentObject = (JSONObject) jsonObject.getJSONArray("paradas").getJSONObject(stopJsonPosition).getJSONArray("lineas").get(i);

                int lineId = currentObject.getInt("id");
                String lineName = currentObject.getString("nombre");

                Pair<Integer, String> pair = new Pair<>(lineId, lineName);

                listaLineas.add(pair);

            }

        } catch (JSONException e) {
            Log.e("MapTabTag", "Error parsing JSON", e);
        }

        return listaLineas;
    }
    private void addItemsToCluster() {
        int pos = 1;
        for (Pair<Integer, LatLng> coords : stopsCoordsList) {
            BusStopClusterItem offsetItem = new BusStopClusterItem(coords.second.latitude, coords.second.longitude, "Title " + coords.second.latitude, "Snippet " + coords.second.longitude);
            clusterManager.addItem(offsetItem);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(offsetItem.getPosition())
                    .title(offsetItem.getTitle())
                    .snippet(offsetItem.getSnippet());

            currentMapMarkers.add(new Pair<Integer, MarkerOptions>(coords.first, markerOptions));
            pos = pos + 1;
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

            obtenerMarcadoresCercanosOrigen(originMarker.getPosition());
        }
        if (which == 1) {
            if (destinationMarker != null)
                destinationMarker.remove();

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            destinationMarker = googleMap.addMarker(markerOptions);

            obtenerMarcadoresCercanosDestino(originMarker.getPosition());

        }
    }

    @Override
    public void onClick(View v) {
        if (v == searchButton) {
            calcularRutas();
            showRoutesLayout();
        }
        else if (v == nextRouteButton) {
            currentRouteIndex += 1;
            if (currentRouteIndex >=  rutasList.size()-1)
                currentRouteIndex = rutasList.size()-1;
            updateRouteLayout();
        }
        else if (v == previousRouteButton) {
            currentRouteIndex -= 1;
            if (currentRouteIndex < 0)
                currentRouteIndex = 0;
            updateRouteLayout();
        }
    }

    private void updateRouteLayout() {
        lineName.setText(rutasList.get(currentRouteIndex).first);
    }
    private void showRoutesLayout() {
        routesLayout.setVisibility(View.VISIBLE);
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

    public double calcularDistancia(LatLng punto1, LatLng punto2) {
        double lat1 = punto1.latitude;
        double lon1 = punto1.longitude;
        double lat2 = punto2.latitude;
        double lon2 = punto2.longitude;
        double R = 6371; // Radio de la Tierra en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distancia en kilómetros
    }
    public void obtenerMarcadoresCercanosOrigen(LatLng punto) {
        // Calcula la distancia de cada marcador al punto presionado
        int id = 0;
        for (Pair<Integer, MarkerOptions> marker : currentMapMarkers) {
            double distancia = calcularDistancia(punto, marker.second.getPosition());
            //marker.setTag(distancia); // Guarda la distancia como un tag en el marcador
            Pair<Integer, Pair<Integer, MarkerConDistancia>> pair = new Pair<>(id, new Pair<>(marker.first, new MarkerConDistancia(marker.second, distancia)));
            marcadoresCercanosOrigen.add(pair);
            id = id + 1;
        }

        // Ordena los marcadores por distancia
        marcadoresCercanosOrigen.sort(Comparator.comparingDouble(m -> m.second.second.distancia));

        // Procesa los marcadores cercanos
        for (int i = 0; i < Math.min(5, marcadoresCercanosOrigen.size()); i++) {
            Pair<Integer, Pair<Integer, MarkerConDistancia>> markerCercano = marcadoresCercanosOrigen.get(i);
            Log.d("Marcador Cercano", "Marker: " + markerCercano.second.second.marker.getTitle() + ", Distancia: " + markerCercano.second.second.distancia);

            String jsonString = JsonUtils.readJsonFromFile(requireActivity().getApplicationContext(), "paradas.json");
            listaLineasOrigen = parseStopJson(jsonString, markerCercano.first);

            Pair<Integer, List<Pair<Integer, String>>> pair = new Pair<>(markerCercano.second.first, listaLineasOrigen);

            listaDeListasDeParadasOrigen.add(pair);

        }

        Log.d("Marcador Cercano", "LISTA LINEAS ORIGEN:" + listaDeListasDeParadasOrigen.toString());

    }

    public void obtenerMarcadoresCercanosDestino(LatLng punto) {
        // Calcula la distancia de cada marcador al punto presionado
        int id = 0;
        for (Pair<Integer, MarkerOptions> marker : currentMapMarkers) {
            double distancia = calcularDistancia(punto, marker.second.getPosition());
            //marker.setTag(distancia); // Guarda la distancia como un tag en el marcador
            Pair<Integer, Pair<Integer, MarkerConDistancia>> pair = new Pair<>(id, new Pair<>(marker.first, new MarkerConDistancia(marker.second, distancia)));

            marcadoresCercanosDestino.add(pair);
            id = id + 1;

        }

        // Ordena los marcadores por distancia
        marcadoresCercanosDestino.sort(Comparator.comparingDouble(m -> m.second.second.distancia));

        // Procesa los marcadores cercanos
        for (int i = 0; i < Math.min(5, marcadoresCercanosDestino.size()); i++) {
            Pair<Integer, Pair<Integer, MarkerConDistancia>> markerCercano = marcadoresCercanosDestino.get(i);
            Log.d("Marcador Cercano", "Marker: " + markerCercano.second.second.marker.getTitle() + ", Distancia: " + markerCercano.second.second.distancia);

            String jsonString = JsonUtils.readJsonFromFile(requireActivity().getApplicationContext(), "paradas.json");
            listaLineasDestino = parseStopJson(jsonString, markerCercano.first);

            // MarkerCercano
                // First => ParadaId
                // Second => MarkerConDistancia
            Pair<Integer, List<Pair<Integer, String>>> pair = new Pair<>(markerCercano.second.first, listaLineasDestino);

            listaDeListasDeParadasDestino.add(pair);

        }

        Log.d("Marcador Cercano", "LISTA LINEAS DESTINO:" + listaDeListasDeParadasDestino.toString());

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

        searchButton = view.findViewById(R.id.calculateRoutes);
        searchButton.setOnClickListener(this);

        nextRouteButton = view.findViewById(R.id.showNextRouteButton);
        nextRouteButton.setOnClickListener(this);

        previousRouteButton = view.findViewById(R.id.showPreviousRouteButton);
        previousRouteButton.setOnClickListener(this);

        routesLayout = view.findViewById(R.id.routesLayout);

        lineName = view.findViewById(R.id.line_name);

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
                                }
                            }
                        });
                googleMap.setOnMyLocationButtonClickListener(MapTab.this);
                googleMap.setOnMyLocationClickListener(MapTab.this);
                enableMyLocation(googleMap); // Si el usuario da permisos de localización, dibujar el boton de ir a ubicacion

                LatLngBounds cityBounds = new LatLngBounds(
                        new LatLng(43.33920463853277, -8.437498363975866), // Suroeste de la ciudad (latitud, longitud)
                        new LatLng(43.39274161525237, -8.379991804710077)); // Noreste de la ciudad (latitud, longitud)

                googleMap.setLatLngBoundsForCameraTarget(cityBounds);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityBounds.getCenter(), 12));

                googleMap.setMinZoomPreference(8);

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