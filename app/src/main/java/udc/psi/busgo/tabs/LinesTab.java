package udc.psi.busgo.tabs;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import udc.psi.busgo.LineDetail;
import udc.psi.busgo.R;
import udc.psi.busgo.adapters.LineAdapter;
import udc.psi.busgo.databinding.FragmentLineDetailBinding;
import udc.psi.busgo.databinding.FragmentLinesTabBinding;
import udc.psi.busgo.objects.Line;


public class LinesTab extends Fragment {

    public interface DetailSelection{
        public void seeDetail(Fragment lineDetail);
    }
    DetailSelection detailSelection;

    public void setDetailSelection(DetailSelection detailSelection){
        this.detailSelection = detailSelection;

    }

    private static final String TAG = "_TAG Lines Tab";
    FragmentLinesTabBinding binding;
    LineAdapter lineAdapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentLinesTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = binding.linesRv;
        initRecycler();
        searchAllLines();
        return view;
    }

    void searchAllLines(){
        Log.d(TAG, "searchLines");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/lineas",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, response.toString());
                        //binding.tvLineasContent.setText(response.toString());
                        try {
                            for (int i = 0; i < response.getJSONArray("lineas").length(); i++){
                                JSONObject currentObject = (JSONObject) response.getJSONArray("lineas").get(i);
                                Line line = new Line(currentObject.get("color").toString(),
                                                     currentObject.get("destino").toString(),
                                                     currentObject.get("nombre").toString(),
                                                     currentObject.get("origen").toString(),
                                                     Integer.parseInt(currentObject.get("id").toString()));
                                addLine(line);
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
        lineAdapter = new LineAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(lineAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        lineAdapter.setClickListener(new LineAdapter.OnLineClickListener() {
            @Override
            public void OnClick(View view, int position, int lineId, String lineName) {
                Log.d(TAG, "Seleccionada la linea " + lineName + " de id " + lineId);
                Fragment lineDetail = LineDetail.newInstance(lineId, lineName);
                if (detailSelection != null){
                    detailSelection.seeDetail(lineDetail);
                }
            }
        });
    }

    private void addLine(Line line){
        lineAdapter.addLine(line);
    }
}