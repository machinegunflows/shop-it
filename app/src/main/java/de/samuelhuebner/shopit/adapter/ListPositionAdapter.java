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
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.ListPosition;

public class ListPositionAdapter extends RecyclerView.Adapter<ListPositionAdapter.ViewHolder> {
    private final Database db;
    private ArrayList<ListPosition> positions;
    private Context context;

    public ListPositionAdapter(@NonNull ArrayList<ListPosition> positions, Database db) {
        this.positions = positions;
        this.db = db;
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
        holder.checkBox.setChecked(this.positions.get(position).isCompleted());
        holder.listNameText.setText(this.positions.get(position).getName());
        holder.categoryChip.setText(this.positions.get(position).getCategory().toLowerCase());

        holder.setItemClickListener((v, pos) -> {
            CheckBox cB = (CheckBox) v;
            ListPosition listPos = this.positions.get(pos);
            listPos.setCompleted(cB.isChecked());
            this.db.updatePosStatus(listPos.getId(), cB.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CheckBox checkBox;
        public ImageView itemImage;
        public TextView listNameText;
        public Chip categoryChip;
        public ImageButton shareButtonView;

        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.isCompletedCheckbox);
            itemImage = itemView.findViewById(R.id.itemImageView);
            listNameText = itemView.findViewById(R.id.listNameText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            shareButtonView = itemView.findViewById(R.id.shareButtonView);

            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        interface ItemClickListener {
            void onItemClick(View v, int pos);
        }
    }

    public void setItems(ArrayList<ListPosition> newPositions) {
        this.positions = newPositions;
    }
}
