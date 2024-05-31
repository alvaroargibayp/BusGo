package udc.psi.busgo.tabs;

import static udc.psi.busgo.workers.JSONRequestWorker.JSON_REQUEST_WORKER_OUTPUT;
import static udc.psi.busgo.workers.JSONRequestWorker.JSON_REQUEST_WORKER_URL;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import udc.psi.busgo.LineDetail;
import udc.psi.busgo.R;
import udc.psi.busgo.StringUtils;
import udc.psi.busgo.adapters.LineAdapter;
import udc.psi.busgo.databinding.FragmentLinesTabBinding;
import udc.psi.busgo.objects.Line;
import udc.psi.busgo.workers.JSONRequestWorker;


public class LinesTab extends Fragment {

    WorkManager workManager;

    public interface LineDetailSelection{
        public void seeLineDetail(Fragment lineDetail);
    }
    LineDetailSelection linedetailSelection;

    private static final String TAG = "_TAG Lines Tab";
    FragmentLinesTabBinding binding;
    LineAdapter lineAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentLinesTabBinding.inflate(inflater, container, false);
        Log.d(TAG, "A");
        View view = binding.getRoot();
        Log.d(TAG, "A");
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.backgroundColor));
        Log.d(TAG, "A");
        recyclerView = binding.linesRv;
        Log.d(TAG, "A");
        swipeRefreshLayout = binding.swipeRefresh;
        Log.d(TAG, "A");
        workManager = WorkManager.getInstance(requireContext());
        Log.d(TAG, "A");
        swipeRefreshLayout.setEnabled(false);
        Log.d(TAG, "A");
        initRecycler();
        Log.d(TAG, "A");
        searchLines();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            linedetailSelection = (LineDetailSelection) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " implement OnMapClickedListener");
        }
    }

    private void initRecycler() {
        lineAdapter = new LineAdapter();
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(lineAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        lineAdapter.setClickListener(new LineAdapter.OnLineClickListener() {
            @Override
            public void OnClick(View view, int position, Line line) {
                Log.d(TAG, "Seleccionada la linea " + line.getName() + " de id " + line.getId());
                Fragment lineDetail = LineDetail.newInstance(line);
                if (linedetailSelection != null){
                    Log.d(TAG, "222Seleccionada la linea " + line.getName() + " de id " + line.getId());
                    linedetailSelection.seeLineDetail(lineDetail);
                }
            }
        });
    }

    private void processJsonLines(String JsonString){
        Log.d(TAG, "Processing Lines Json");
        JSONObject Json;
        try {
            Json = new JSONObject(JsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            for (int i = 0; i < Json.getJSONArray("lineas").length(); i++){
                JSONObject currentObject = (JSONObject) Json.getJSONArray("lineas").get(i);
                Line line = new Line(currentObject.get("color").toString(),
                        currentObject.get("destino").toString(),
                        currentObject.get("nombre").toString(),
                        currentObject.get("origen").toString(),
                        Integer.parseInt(currentObject.get("id").toString()));
                lineAdapter.addLine(line);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchLines(){

        Log.d(TAG, "Search lines");
        OneTimeWorkRequest testLineRequest = new OneTimeWorkRequest.Builder(JSONRequestWorker.class).setInputData(createUrlForLines()).build();
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
                                output = StringUtils.decompress(workInfo.getOutputData().getString(JSON_REQUEST_WORKER_OUTPUT));
                                Log.d(TAG, "Work " + workInfo.getId() + " succeeded " + output);
                                processJsonLines(output);
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

    private Data createUrlForLines() {
        Data.Builder builder = new Data.Builder();
        builder.putString(JSON_REQUEST_WORKER_URL, "https://bus.delthia.com/api/lineas");
        return builder.build();
    }
}