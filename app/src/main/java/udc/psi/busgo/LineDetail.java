package udc.psi.busgo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import udc.psi.busgo.adapters.LineAdapter;
import udc.psi.busgo.adapters.LineDetailAdapter;
import udc.psi.busgo.databinding.FragmentLineDetailBinding;
import udc.psi.busgo.databinding.FragmentLinesTabBinding;
import udc.psi.busgo.objects.Line;
import udc.psi.busgo.tabs.MapTab;

public class LineDetail extends Fragment implements View.OnClickListener {
    private static final String TAG = "_TAG Line Detail";
    private static final String LINE_PARAM = "lineNameParam";
    FragmentLineDetailBinding binding;
    LineDetailAdapter lineDetailAdapter;
    RecyclerView recyclerView;
    TextView tv_response_placeholder;
    TextView detailTittle;

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
        return view;
    }
    private void setLayout(){
        recyclerView = binding.linesRv;
        initRecycler();
        tv_response_placeholder = binding.tvResponsePlaceholder;
        detailTittle = binding.tvLineDetailTittle;

        detailTittle.setText("Linea " + line.getName());

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
                                JSONObject jsonObject = new JSONObject(respuestaRecortada);
                                tv_response_placeholder.setText(jsonObject.toString());
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
    }

    private void initRecycler() {

        //TODO borrar despues
        ArrayList<String> stops = new ArrayList<String>();
        for (int i = 0; i < 10; i++){
            stops.add("Parada " + i);
        }

        lineDetailAdapter = new LineDetailAdapter(stops);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(lineDetailAdapter);
    }
}
