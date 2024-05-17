package udc.psi.busgo.adapters;

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
import udc.psi.busgo.objects.Line;

public class LineDetailAdapter extends RecyclerView.Adapter<LineDetailAdapter.LineDetailViewHolder> {

    private static final String TAG = "_TAG Line Detail Adapter";

    private LineDetailLayoutPlaceholderBinding binding;

    private ArrayList<String> stopsList;

    //TODO cambiar string por la implementacion de las paradas
    public LineDetailAdapter(ArrayList<String> stopsList) {
        this.stopsList = stopsList;
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

    public static class LineDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name;
        public LineDetailViewHolder(@NonNull LineDetailLayoutPlaceholderBinding binding) {
            super(binding.getRoot());
            name = binding.stopName;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(String stop){
            name.setText(stop);
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "No toques", Toast.LENGTH_SHORT).show();
        }
    }

}
