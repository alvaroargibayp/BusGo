package udc.psi.busgo.tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import udc.psi.busgo.adapters.StopAdapter;
import udc.psi.busgo.databinding.FragmentStopsTabBinding;
import udc.psi.busgo.objects.Stop;


public class StopsTab extends Fragment {
    private static final String TAG = "_TAG";
    FragmentStopsTabBinding binding;

    StopAdapter stopAdapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentStopsTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = binding.stopsRv;
        searchAllStops();

        return view;
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
                            ArrayList<Stop> stops = new ArrayList<Stop>();
                            for (int i = 0; i < response.getJSONArray("paradas").length(); i++){
                                JSONObject currentObject = (JSONObject) response.getJSONArray("paradas").get(i);
                                JSONArray coordsArray = currentObject.getJSONArray("coords");
                                int[] coords = new int[2];
                                coords[0] = coordsArray.getInt(0);
                                coords[1] = coordsArray.getInt(1);
                                String osmidStr = currentObject.get("osmid").toString();
                                long osmid;
                                if (osmidStr != null && !osmidStr.equals("null")) {
                                    osmid = Long.parseLong(osmidStr);
                                } else {
                                    osmid = 0;
                                }
                                Stop stop = new Stop(coords,
                                        Integer.parseInt(currentObject.get("id").toString()),
                                        currentObject.get("nombre").toString(),
                                        osmid);
                                stops.add(stop);
                            }
                            initRecycler(stops);

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

    private void initRecycler(ArrayList<Stop> stops) {
        stopAdapter = new StopAdapter(stops);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(stopAdapter);
    }
}