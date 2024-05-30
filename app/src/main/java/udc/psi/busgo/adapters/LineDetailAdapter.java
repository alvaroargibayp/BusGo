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

import udc.psi.busgo.LineDetail;
import udc.psi.busgo.databinding.FragmentLineDetailBinding;
import udc.psi.busgo.databinding.LineDetailLayoutPlaceholderBinding;
import udc.psi.busgo.databinding.LineLayoutBinding;
import udc.psi.busgo.objects.Bus;
import udc.psi.busgo.objects.Line;
import udc.psi.busgo.objects.Stop;

public class LineDetailAdapter extends RecyclerView.Adapter<LineDetailAdapter.LineDetailViewHolder> {

    private static final String TAG = "_TAG Line Detail Adapter";

    private LineDetailLayoutPlaceholderBinding binding;

    private ArrayList<Stop> stopsList;

    public LineDetailAdapter(ArrayList<Stop> stopsList) {
        this.stopsList = stopsList;
    }

    public LineDetailAdapter() {
        this.stopsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public LineDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LineDetailLayoutPlaceholderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LineDetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LineDetailViewHolder holder, int position) {
        holder.bind(stopsList.get(position));
    }

    @Override
    public int getItemCount() {
        return stopsList.size();
    }

    public static class LineDetailViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public LineDetailViewHolder(@NonNull LineDetailLayoutPlaceholderBinding binding) {
            super(binding.getRoot());
            name = binding.stopName;
        }

        public void bind(Stop stop){
            name.setText(stop.getName());
        }
    }

    public void addStop(Stop stop){
        stopsList.add(stop);
        notifyItemInserted(getItemCount());
    }

    public void addBus(Bus bus, String stopName){
        for(int i = 0; i < stopsList.size(); i++){
            if (stopsList.get(i).getName().equals(stopName)){
                Stop stop = stopsList.get(i);
                stop.setName(stopName + "*");
                notifyItemChanged(i);
            }
        }
    }
}
