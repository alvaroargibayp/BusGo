package udc.psi.busgo.tabs;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import org.json.JSONObject;

import udc.psi.busgo.R;
import udc.psi.busgo.databinding.FragmentHomeTabBinding;

public class HomeTab extends Fragment implements View.OnClickListener {

    private static final String TAG = "_TAG Home Tab";
    FragmentHomeTabBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.backgroundColor));
        binding.btnSearch.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnSearch) {
            if (binding.etSearch.getText().toString().isEmpty()) {
                Toast.makeText(binding.getRoot().getContext(), "Por favor, introduce un valor válido", Toast.LENGTH_SHORT).show();
            } else {
                individual_search();
            }
        } else {
            Log.d(TAG, "Click desconocido");
        }
    }

    public void individual_search() {
        Log.d(TAG, "searchIndividual");
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String baseUrl;
        if (binding.switchToggle.isChecked()) {
            baseUrl = "https://bus.delthia.com/api/linea/" + binding.etSearch.getText().toString();
        } else {
            baseUrl = "https://bus.delthia.com/api/parada/" + binding.etSearch.getText().toString();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                baseUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        binding.tvSearchContent.setText(response.toString());
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
}