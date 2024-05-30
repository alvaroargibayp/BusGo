package udc.psi.busgo.tabs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import udc.psi.busgo.LineDetail;
import udc.psi.busgo.R;
import udc.psi.busgo.StopDetail;
import udc.psi.busgo.adapters.LineAdapter;
import udc.psi.busgo.adapters.StopAdapter;
import udc.psi.busgo.databinding.FragmentStopsTabBinding;
import udc.psi.busgo.objects.Line;
import udc.psi.busgo.objects.Stop;


public class StopsTab extends Fragment {

    public interface StopDetailSelection{
        public void seeStopDetail(Fragment stopDetail);
    }
    StopDetailSelection detailSelection;

    public void setDetailSelection(StopDetailSelection detailSelection){
        this.detailSelection = detailSelection;

    }

    private static final String TAG = "_TAG Stops Tab";
    FragmentStopsTabBinding binding;

    StopAdapter stopAdapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentStopsTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.backgroundColor));
        recyclerView = binding.stopsRv;
        initRecycler();
        searchAllStops();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            detailSelection = (StopDetailSelection) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement StopDetailSelection");
        }
    }

    void searchAllStops(){
        Log.d(TAG, "searchStops");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/paradas",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("_TAG", response.toString());
                        //binding.tvStopsContent.setText(response.toString());
                        try {
                            for (int i = 0; i < response.getJSONArray("paradas").length(); i++){
                                JSONObject currentObject = (JSONObject) response.getJSONArray("paradas").get(i);
                                JSONArray coordsArray = currentObject.getJSONArray("coords");
                                double[] coords = new double[2];
                                coords[0] = coordsArray.getDouble(0);
                                coords[1] = coordsArray.getDouble(1);
                                ArrayList<Line> stopLines = new ArrayList<>();
                                for (int j = 0; j < currentObject.getJSONArray("lineas").length(); j++){
                                    JSONObject currentLine = (JSONObject) currentObject.getJSONArray("lineas").get(j);
                                    stopLines.add(new Line(
                                            currentLine.get("color").toString(), currentLine.get("nombre").toString(), Integer.parseInt(currentLine.get("id").toString())));
                                }
                                try{
                                    Stop stop = new Stop(coords,
                                            Integer.parseInt(currentObject.get("id").toString()),
                                            currentObject.get("nombre").toString(),
                                            stopLines.toArray(new Line[0])
                                            );
                                    addStop(stop);
                                } catch (Exception e){
                                    Log.d(TAG, "Error en bucle " + i);
                                }

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

    private void initRecycler() {
        stopAdapter = new StopAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(stopAdapter);
        stopAdapter.setClickListener(new StopAdapter.OnStopClickListener() {
            @Override
            public void OnClick(View view, int position, Stop stop) {
                Log.d(TAG, "Seleccionada la parada " + stop.getName() + " de id " + stop.getId());
                Fragment stopDetail = StopDetail.newInstance(stop);
                if (detailSelection != null){
                    detailSelection.seeStopDetail(stopDetail);
                }
            }
        });
    }

    private void addStop(Stop stop) {
        stopAdapter.addStop(stop);
    }
}