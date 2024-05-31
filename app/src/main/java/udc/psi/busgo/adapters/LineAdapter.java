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

import udc.psi.busgo.databinding.LineLayoutBinding;
import udc.psi.busgo.objects.Line;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineViewHolder> {

    private static final String TAG = "_TAG Line Adapter";

    public interface OnLineClickListener{
        public void OnClick(View view, int position, Line line);
    }

    private static OnLineClickListener clickListener;
    public void setClickListener(OnLineClickListener lineClickListener){
        clickListener = lineClickListener;
    }

    private LineLayoutBinding binding;

    private ArrayList<Line> lineList;

    public LineAdapter(ArrayList<Line> lineList) {
        this.lineList = lineList;
    }
    public LineAdapter() {
        this.lineList = new ArrayList<>();
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

        public TextView name, origin, separator, destination;
        private Line line;
        public LineViewHolder(@NonNull LineLayoutBinding binding) {
            super(binding.getRoot());
            name = binding.lineName;
            origin = binding.lineOrigin;
            separator = binding.lineSeparator;
            destination = binding.lineDestination;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Line line){
            //Log.d(TAG, "Bind line:\n Name: " + line.getName() + "\n Origin: " + line.getOrigin()
            //        + "\n Destination: " + line.getDestination());
            name.setText(line.getName());
            origin.setText(line.getOrigin());
            separator.setText(" " + separator.getText().toString() + " ");
            destination.setText(line.getDestination());
            this.line = line;
        }
        @Override
        public void onClick(View v) {
            if (clickListener != null){
                clickListener.OnClick(v, getAdapterPosition(), this.line);
            }
        }
    }
    public void addLine(Line line){
        lineList.add(line);
        notifyItemInserted(getItemCount());
    }

}
