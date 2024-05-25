package udc.psi.busgo.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import udc.psi.busgo.databinding.StopLayoutBinding;
import udc.psi.busgo.objects.Stop;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    private static final String TAG = "_TAG Stop Adapter";

    private StopLayoutBinding binding;

    private ArrayList<Stop> stopList;

    public StopAdapter() {
        stopList = new ArrayList<>();
    }
    public StopAdapter(ArrayList<Stop> stopList) {
        this.stopList = stopList;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = StopLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StopViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        holder.bind(stopList.get(position));
    }

    @Override
    public int getItemCount() {
        return stopList.size();
    }

    public static class StopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name, coords, osmcoords;
        public StopViewHolder(@NonNull StopLayoutBinding binding) {
            super(binding.getRoot());
            name = binding.stopName;
            coords = binding.stopCoords;
        }

        public void bind(Stop stop){
            Log.d(TAG, "Bind stop:\n Name: " + stop.getName() + "\n Id: " + stop.getId()
                    + "\n Coordenadas: " + stop.getCoords()[0] + " - " + stop.getCoords()[1]
                    + "\n osmId: " + stop.getOsmid());
            name.setText(stop.getName());
            double[] coordsArray = stop.getCoords();

            coords.setText(coordsArray[0] + " " + coordsArray[1]);
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "No toques", Toast.LENGTH_SHORT).show();
        }
    }

    public void addStop(Stop stop){
        stopList.add(stop);
        notifyItemInserted(getItemCount());
    }

}
