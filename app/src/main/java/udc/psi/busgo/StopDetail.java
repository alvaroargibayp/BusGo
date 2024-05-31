package udc.psi.busgo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import udc.psi.busgo.adapters.StopDetailAdapter;
import udc.psi.busgo.databinding.FragmentStopDetailBinding;
import udc.psi.busgo.objects.Line;
import udc.psi.busgo.objects.Stop;

public class StopDetail extends Fragment implements View.OnClickListener{

    private static final String TAG = "_TAG Stop Detail";
    private static final String STOP_PARAM = "stopNameParam";
    FragmentStopDetailBinding binding;
    StopDetailAdapter stopDetailAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    TextView detailTittle;
    TextView coordsTv;

    private Stop stop;

    public static StopDetail newInstance(Stop stop) {
        StopDetail fragment = new StopDetail();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STOP_PARAM, stop);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stop = getArguments().getParcelable(STOP_PARAM);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStopDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.backgroundColor));
        swipeRefresh = binding.swipeRefresh;
        swipeRefresh.setEnabled(false);
        setLayout();
        searchSpecificStop(stop.getId());
        return view;
    }
    private void setLayout(){
        recyclerView = binding.stopLinesRv;
        initRecycler();
        detailTittle = binding.tvStopDetailTittle;
        detailTittle.setText(stop.getName());
        coordsTv = binding.tvStopDetailCoords;
        String coordsPlaceHolder = coordsTv.getText().toString();
        coordsTv.setText(coordsPlaceHolder + " " + stop.getCoords()[0] + " " + stop.getCoords()[1]);
    }

    void searchSpecificStop(int stopId){
        Log.d(TAG, "Search stop " + stopId);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/parada/" + stopId,
                    null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, String.valueOf(response));
                        try {
                            ArrayList<Line> stopLines = new ArrayList<>();
                            for (int i = 0; i < response.getJSONArray("lineas").length(); i++){
                                JSONObject currentLine = (JSONObject) response.getJSONArray("lineas").get(i);
                                stopLines.add(new Line(
                                    currentLine.get("color").toString(), currentLine.get("nombre").toString(), Integer.parseInt(currentLine.get("id").toString())));
                                stopDetailAdapter.addLine(currentLine.get("nombre").toString(), Integer.parseInt(currentLine.get("id").toString()));
                             }
                            JSONArray coordsArray = response.getJSONArray("coords");
                            double[] coords = new double[2];
                            coords[0] = coordsArray.getDouble(0);
                            coords[1] = coordsArray.getDouble(1);
                            int id = Integer.parseInt(response.get("id").toString());
                            String name = response.get("nombre").toString();
                            stop = new Stop(coords, id, name, stopLines.toArray(new Line[0]));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
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

    private void initRecycler() {
        stopDetailAdapter = new StopDetailAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(stopDetailAdapter);
    }
}