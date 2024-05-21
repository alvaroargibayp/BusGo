package udc.psi.busgo.adapters;

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

    private StopLayoutBinding binding;

    private ArrayList<Stop> stopList;

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
            name.setText(stop.getName());
            coords.setText(stop.getCoords().toString());
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "No toques", Toast.LENGTH_SHORT).show();
        }
    }

}
