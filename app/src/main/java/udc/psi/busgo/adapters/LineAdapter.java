package udc.psi.busgo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import udc.psi.busgo.databinding.LineLayoutBinding;
import udc.psi.busgo.objects.Line;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineViewHolder> {

    private LineLayoutBinding binding;

    private ArrayList<Line> lineList;

    public LineAdapter(ArrayList<Line> lineList) {
        this.lineList = lineList;
    }

    @NonNull
    @Override
    public LineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LineLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LineViewHolder holder, int position) {
        holder.bind(lineList.get(position));
    }

    @Override
    public int getItemCount() {
        return lineList.size();
    }

    public static class LineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name, origin, destination;
        public LineViewHolder(@NonNull LineLayoutBinding binding) {
            super(binding.getRoot());
            name = binding.lineName;
            origin = binding.lineOrigin;
            destination = binding.lineDestination;
        }

        public void bind(Line line){
            name.setText(line.getName());
            origin.setText(line.getOrigin());
            destination.setText(line.getDestination());
        }
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "No toques", Toast.LENGTH_SHORT).show();
        }
    }

}
