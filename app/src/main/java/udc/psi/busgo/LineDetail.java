package udc.psi.busgo;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Configuration;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import udc.psi.busgo.adapters.LineDetailAdapter;
import udc.psi.busgo.databinding.FragmentLineDetailBinding;
import udc.psi.busgo.objects.Bus;
import udc.psi.busgo.objects.Line;

public class LineDetail extends Fragment implements View.OnClickListener {
    private static final String TAG = "_TAG Line Detail";
    private static final String LINE_PARAM = "lineNameParam";
    FragmentLineDetailBinding binding;
    LineDetailAdapter lineDetailAdapter;
    RecyclerView recyclerView;
    TextView detailTittle;

    ArrayList<Bus> buses;
    WorkManager workManager;

    private Line line;

    public static LineDetail newInstance(Line line){
        LineDetail fragment = new LineDetail();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LINE_PARAM, line);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            line = getArguments().getParcelable(LINE_PARAM);
        }
        workManager = WorkManager.getInstance(getActivity().getApplicationContext());
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLineDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setLayout();
        searchSpecificLine(line.getId());
        searchBuses(line.getId());


        probarWorker();

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void setLayout(){
        recyclerView = binding.linesRv;
        initRecycler();
        detailTittle = binding.tvLineDetailTittle;

        detailTittle.setText("Linea " + line.getName());

        buses = new ArrayList<Bus>();

    }

    void searchSpecificLine(int lineId){
        Log.d(TAG, "Search line " + lineId);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/linea/" + lineId + "/paradas",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(TAG, response);
                        String prefijo = "var paradas = ";
                        if (response.startsWith(prefijo)) {
                            String respuestaRecortada = response.substring(prefijo.length());
                            if (respuestaRecortada.endsWith(";")) {
                                respuestaRecortada = respuestaRecortada.substring(0, respuestaRecortada.length()-1);
                            }

                            Log.d(TAG, respuestaRecortada);
                            try {
                                addStop("Ida: ", new double[]{0.1, 0.1});
                                JSONObject jsonObject = new JSONObject(respuestaRecortada);
                                JSONArray jsonArray = jsonObject.getJSONArray("features");
                                for (int i = 0; i < jsonArray.length(); i++){
                                    JSONObject currentStop = (JSONObject) jsonArray.get(i);
                                    JSONObject stopProperties = currentStop.getJSONObject("properties");
                                    JSONObject stopGeometry = currentStop.getJSONObject("geometry");
                                    String name = stopProperties.getString("name");
                                    String popupContent = stopProperties.getString("popupContent");
                                    if (!name.equals(popupContent)){
                                        Log.d(TAG, "Discrepancia entre nombre y popupContent en al posicion "+ i);
                                    }
                                    if (!stopGeometry.getString("type").equals("Point")){
                                        Log.d(TAG, "Tipo de parada distinta a punto en "+ i);
                                    }
                                    JSONArray coordsArray = stopGeometry.getJSONArray("coordinates");
                                    double[] coords = new double[2];
                                    coords[0] = coordsArray.getDouble(0);
                                    coords[1] = coordsArray.getDouble(1);
                                    addStop(name, coords);
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );

        requestQueue.add(stringRequest);
        searchStopNames();
    }

    void searchBuses(int lineId){
        Log.d(TAG, "Search buses of line " + lineId);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/linea/" + lineId + "/buses",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, response.toString());
                        try {
                            JSONArray generalStops = response.getJSONArray("paradas");
                            for (int i = 0; i < generalStops.length(); i++) {
                                JSONObject busesDirection = (JSONObject) generalStops.get(i);
                                int direction = busesDirection.getInt("sentido");
                                JSONArray stops = busesDirection.getJSONArray("paradas");
                                for (int j = 0; j < stops.length(); j++) {
                                    JSONObject stop = (JSONObject) stops.get(j);
                                    int stopId = stop.getInt("parada");
                                    JSONArray buses = stop.getJSONArray("buses");
                                    for (int k = 0; k < buses.length(); k++) {
                                        JSONObject bus = (JSONObject) buses.get(k);
                                        int busId = bus.getInt("bus");
                                        double distance = bus.getDouble("distancia");
                                        int state =  bus.getInt("estado");
                                        Bus busObject = new Bus(busId, stopId, distance, state, direction);
                                        Log.d(TAG, "Bus: " + busId + " encontrado");
                                        addBus(busObject);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            try {
                                String state = response.getString("estado");
                                Log.d(TAG, "state");
                            } catch (JSONException e2) {
                                throw new RuntimeException(e2);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }

    public interface StopNameCallback{
        void nameReturn(String stopName);
        void error(String error);
    }

    private void searchStopName(int stopId, final StopNameCallback callback){
        Log.d(TAG, "Search stop name " + stopId);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/parada/" + stopId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, response.toString());
                        try {
                            String stopName = response.getString("nombre");
                            callback.nameReturn(stopName);
                        } catch (JSONException e) {
                            callback.error(e.toString());
                        }
                    }
                    },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                        callback.error(error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }
    private void addBus(Bus bus){
        buses.add(bus);
    }
    private void searchStopNames(){

        Log.d(TAG, "Search stop name size: " + buses.size());
        for (Bus bus: buses){
            Log.d(TAG, "Search stop name " + bus.getStopId());
            searchStopName(bus.getStopId(), new StopNameCallback() {
                @Override
                public void nameReturn(String stopName) {
                    lineDetailAdapter.addBus(bus, stopName);
                }

                @Override
                public void error(String error) {
                }
            });
        }
    }



    private void addStop(String name, double[] coords) {
        if (name.equals(line.getDestination())) {
            lineDetailAdapter.addDestinationStop(name, coords);
        } else {
            lineDetailAdapter.addStop(name, coords);
        }
    }

    private void initRecycler() {
        lineDetailAdapter = new LineDetailAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(lineDetailAdapter);
    }

    public static class BusSearch extends Worker {

        public BusSearch(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {

            Context context = getApplicationContext();

            int n = getInputData().getInt("KEYAAAA", 0);
            Log.d(TAG, "doing work " + n);
            return Result.success();
        }
    }
    public void probarWorker(){

        Log.d(TAG, "probarWorker");
        OneTimeWorkRequest testRequest = new OneTimeWorkRequest.Builder(BusSearch.class).setInputData(createInputDataForBusSearch(4)).build();
        workManager.enqueue(OneTimeWorkRequest.from(BusSearch.class));

    }

    private Data createInputDataForBusSearch(int n) {
        Data.Builder builder = new Data.Builder();
        builder.putInt("KEYAAAA", n);
        return builder.build();
    }



}
