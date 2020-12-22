package de.samuelhuebner.shopit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.ListPosition;

public class ListPositionAdapter extends RecyclerView.Adapter<ListPositionAdapter.ViewHolder> {
    private ArrayList<ListPosition> positions;
    private Context context;

    public ListPositionAdapter(@NonNull ArrayList<ListPosition> positions) {
        this.positions = positions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.list_position_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.checkBox.setActivated(this.positions.get(position).isCompleted());
        holder.listNameText.setText(this.positions.get(position).getName());

        holder.categoryChip.setText(this.positions.get(position).getCategory().toLowerCase());
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public ImageView itemImage;
        public TextView listNameText;
        public Chip categoryChip;
        public ImageButton shareButtonView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.isCompletedCheckbox);
            itemImage = itemView.findViewById(R.id.itemImageView);
            listNameText = itemView.findViewById(R.id.listNameText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            shareButtonView = itemView.findViewById(R.id.shareButtonView);
        }
    }

    public void setItems(ArrayList<ListPosition> newPositions) {
        this.positions = newPositions;
    }
}
