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

    public interface OnStopClickListener{
        public void OnClick(View view, int position, Stop stop);
    }

    private static StopAdapter.OnStopClickListener clickListener;
    public void setClickListener(StopAdapter.OnStopClickListener stopClickListener){
        clickListener = stopClickListener;
    }

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

        public TextView id, name;
        private Stop stop;
        public StopViewHolder(@NonNull StopLayoutBinding binding) {
            super(binding.getRoot());
            id = binding.stopId;
            name = binding.stopName;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Stop stop){
//            Log.d(TAG, "Bind stop:\n Name: " + stop.getName() + "\n Id: " + stop.getId()
//                    + "\n Coordenadas: " + stop.getCoords()[0] + " - " + stop.getCoords()[1]
//                    + "\n osmId: " + stop.getOsmid());
            id.setText("" + stop.getId());
            name.setText(stop.getName());
            this.stop = stop;
        }
        @Override
        public void onClick(View v) {
            if (clickListener != null){
                clickListener.OnClick(v, getAdapterPosition(), this.stop);
            }
        }
    }

    public void addStop(Stop stop){
        stopList.add(stop);
        notifyItemInserted(getItemCount());
    }

}
