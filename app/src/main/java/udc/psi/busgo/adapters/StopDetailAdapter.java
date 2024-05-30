package udc.psi.busgo.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import udc.psi.busgo.databinding.StopDetailLayoutPlaceholderBinding;

public class StopDetailAdapter extends RecyclerView.Adapter<StopDetailAdapter.StopDetailViewHolder> {

    private static final String TAG = "_TAG Stop Detail Adapter";

    private StopDetailLayoutPlaceholderBinding binding;

    private ArrayList<String> lineList;

    public StopDetailAdapter(ArrayList<String> lineList) {
        this.lineList = lineList;
    }

    public StopDetailAdapter() {
        this.lineList = new ArrayList<>();
    }

    @NonNull
    @Override
    public StopDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = StopDetailLayoutPlaceholderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StopDetailViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StopDetailViewHolder holder, int position) {
        holder.bind(lineList.get(position));
    }

    @Override
    public int getItemCount() {
        return lineList.size();
    }

    public static class StopDetailViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public StopDetailViewHolder(@NonNull StopDetailLayoutPlaceholderBinding binding) {
            super(binding.getRoot());
            name = binding.lineName;
        }

        public void bind(String line){
            name.setText(line);
        }
    }

    public void addLine(String name, int id){
        lineList.add(name);
        notifyItemInserted(getItemCount());
    }
}
