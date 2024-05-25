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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import udc.psi.busgo.adapters.LineDetailAdapter;
import udc.psi.busgo.databinding.FragmentLineDetailBinding;
import udc.psi.busgo.objects.Line;

public class LineDetail extends Fragment implements View.OnClickListener {
    private static final String TAG = "_TAG Line Detail";
    private static final String LINE_PARAM = "lineNameParam";
    FragmentLineDetailBinding binding;
    LineDetailAdapter lineDetailAdapter;
    RecyclerView recyclerView;
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
}
