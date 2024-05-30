package udc.psi.busgo;

import static udc.psi.busgo.workers.JSONRequestWorker.JSON_REQUEST_WORKER_OUTPUT;
import static udc.psi.busgo.workers.JSONRequestWorker.JSON_REQUEST_WORKER_URL;
import static udc.psi.busgo.workers.StringRequestWorker.STRING_REQUEST_WORKER_OUTPUT;
import static udc.psi.busgo.workers.StringRequestWorker.STRING_REQUEST_WORKER_URL;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import udc.psi.busgo.adapters.LineDetailAdapter;
import udc.psi.busgo.databinding.FragmentLineDetailBinding;
import udc.psi.busgo.objects.Bus;
import udc.psi.busgo.objects.Line;
import udc.psi.busgo.objects.Stop;
import udc.psi.busgo.workers.JSONRequestWorker;
import udc.psi.busgo.workers.StringRequestWorker;

public class LineDetail extends Fragment{
    private static final String TAG = "_TAG Line Detail";
    private static final String LINE_PARAM = "lineNameParam";
    FragmentLineDetailBinding binding;
    LineDetailAdapter lineDetailAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView detailTittle;
    ArrayList<Bus> buses;
    WorkManager workManager;
    private Line line;
    private boolean working;
    private int countDown;
    private Stop lastStop;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLineDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.backgroundColor));
        setLayoutAndEnvironment();
        if (line != null){
            searchStopsInALine(line.getId());
        }
        return view;
    }


    private void setLayoutAndEnvironment(){
        workManager = WorkManager.getInstance(getActivity().getApplicationContext());
        buses = new ArrayList<Bus>();
        recyclerView = binding.linesRv;
        initRecycler();
        detailTittle = binding.tvLineDetailTittle;
        swipeRefreshLayout = binding.swipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (line != null && !working ){
                    searchBusesInALine(line.getId());
                    working = true;
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        if (line != null){
            detailTittle.setText("Linea " + line.getName());
        }
    }

    private void initRecycler() {
        lineDetailAdapter = new LineDetailAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(lineDetailAdapter);
    }

    private void searchStopNames(){
        int size = buses.size();
        countDown = size;
        Log.d(TAG, "Search stop name size: " + size);
        for (Bus bus : buses){
            searchStopNameById(bus.getStopId(), bus);
        }
    }

    private void addStop(Stop stop) {
        if (lastStop != null){
            if (lastStop.getName().equals(stop.getName())) {
                lineDetailAdapter.addStop(new Stop(new double[]{0.1, 0.1}, "Vuelta: "));
                lineDetailAdapter.addStop(stop);
            } else {
                lineDetailAdapter.addStop(stop);
                lastStop = stop;
            }
        } else {
            lineDetailAdapter.addStop(stop);
            lastStop = stop;
        }
    }

    private void processJsonLineStops(String Json){
        Log.d(TAG, "Processing Line Stops Json");
        try {
            addStop(new Stop(new double[]{0.1, 0.1}, "Ida: "));
            JSONObject jsonObject = new JSONObject(Json);
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
                Stop stop = new Stop(coords, name);
                addStop(stop);
            }
            searchBusesInALine(line.getId());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void processJsonLineBuses(String JsonString){
        Log.d(TAG, "Processing Line Buses Json");
        JSONObject Json;
        try {
            Json = new JSONObject(JsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            JSONArray generalStops = Json.getJSONArray("paradas");
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
                        this.buses.add(busObject);
                    }
                }
            }
            searchStopNames();
        } catch (JSONException e) {
            try {
                String state = Json.getString("estado");
                Log.d(TAG, "state");
            } catch (JSONException e2) {
                throw new RuntimeException(e2);
            }
        }
    }

    private void processJsonStopName(String JsonString, Bus bus){
        Log.d(TAG, "Processing Stop Json");
        JSONObject Json;
        try {
            Json = new JSONObject(JsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            String stopName = Json.getString("nombre");
            countDown = countDown -1;
            Log.d(TAG, "stopName: " + stopName + countDown);
            lineDetailAdapter.addBus(bus, stopName);

            if (countDown == 0){
                working = false;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchStopsInALine(int lineId){

        Log.d(TAG, "Search Stops in line " + lineId);
        OneTimeWorkRequest testLineRequest = new OneTimeWorkRequest.Builder(StringRequestWorker.class).setInputData(createUrlForLineStops(lineId)).build();
        WorkContinuation continuation = workManager.beginWith(testLineRequest);
        continuation.enqueue();
        continuation.getWorkInfosLiveData().observe(getViewLifecycleOwner(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }

                for (WorkInfo workInfo : workInfos) {
                    if (workInfo.getState().isFinished()) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            String output = null;
                            try {
                                output = StringUtils.decompress(workInfo.getOutputData().getString(STRING_REQUEST_WORKER_OUTPUT));
                                Log.d(TAG, "Work " + workInfo.getId() + " succeeded " + output);
                                processJsonLineStops(output);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d(TAG, "Work " + workInfo.getId() + " succeeded " + output);
                        } else {
                            Log.d(TAG, "Work " + workInfo.getId() + " failed");
                        }
                    }
                }
            }
        });
    }

    public void searchBusesInALine(int lineId){
        Log.d(TAG, "Search Buses in line " + lineId);
        OneTimeWorkRequest testLineRequest = new OneTimeWorkRequest.Builder(JSONRequestWorker.class).setInputData(createUrlForLineBuses(lineId)).build();
        WorkContinuation continuation = workManager.beginWith(testLineRequest);
        continuation.enqueue();
        continuation.getWorkInfosLiveData().observe(getViewLifecycleOwner(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }

                for (WorkInfo workInfo : workInfos) {
                    if (workInfo.getState().isFinished()) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            try {
                                String output = StringUtils.decompress(workInfo.getOutputData().getString(JSON_REQUEST_WORKER_OUTPUT));
                                Log.d(TAG, "Work " + workInfo.getId() + " succeeded " + output);
                                processJsonLineBuses(output);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Log.d(TAG, "Work " + workInfo.getId() + " failed");
                        }
                    }
                }
            }
        });
    }

    public void searchStopNameById(int stopId, Bus bus){
        Log.d(TAG, "Search Stop Name By Id: " + stopId);
        OneTimeWorkRequest testLineRequest = new OneTimeWorkRequest.Builder(JSONRequestWorker.class).setInputData(createUrlForStop(stopId)).build();
        WorkContinuation continuation = workManager.beginWith(testLineRequest);
        continuation.enqueue();
        continuation.getWorkInfosLiveData().observe(getViewLifecycleOwner(), new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }

                for (WorkInfo workInfo : workInfos) {
                    if (workInfo.getState().isFinished()) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            try {
                                String output = StringUtils.decompress(workInfo.getOutputData().getString(JSON_REQUEST_WORKER_OUTPUT));
                                Log.d(TAG, "Work " + workInfo.getId() + " succeeded " + output);
                                processJsonStopName(output, bus);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Log.d(TAG, "Work " + workInfo.getId() + " failed");
                        }
                    }
                }
            }
        });
    }

    private Data createUrlForLineStops(int lineId) {
        Data.Builder builder = new Data.Builder();
        builder.putString(STRING_REQUEST_WORKER_URL, "https://bus.delthia.com/api/linea/" + lineId + "/paradas");
        return builder.build();
    }

    private Data createUrlForLineBuses(int lineId) {
        Data.Builder builder = new Data.Builder();
        builder.putString(JSON_REQUEST_WORKER_URL, "https://bus.delthia.com/api/linea/" + lineId + "/buses");
        return builder.build();
    }

    private Data createUrlForStop(int stopId) {
        Data.Builder builder = new Data.Builder();
        builder.putString(JSON_REQUEST_WORKER_URL, "https://bus.delthia.com/api/parada/" + stopId);
        return builder.build();
    }

}
