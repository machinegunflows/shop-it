package de.samuelhuebner.shopit.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import de.samuelhuebner.shopit.MainActivity;
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
        ListPosition listPos = this.positions.get(position);
        holder.checkBox.setChecked(listPos.isCompleted());
        holder.listNameText.setText(listPos.getName());
        holder.categoryChip.setText(listPos.getCategory().toLowerCase());
        holder.itemImage.setImageResource(R.drawable.ic_shopping_basket_black_24dp);

        holder.setItemClickListener((v, pos) -> {
            if (v.getId() == R.id.isCompletedCheckbox) {
                CheckBox cB = (CheckBox) v;
                ListPosition listPosition = this.positions.get(pos);
                listPosition.setCompleted(cB.isChecked());
                this.db.updatePosStatus(listPosition.getId(), cB.isChecked());
                Log.d("Listener", "updating checkbox");
            } else {
                ListPosition sharedPosition = this.positions.get(pos);

                String shareText = "Hello, \nI would like to share the following item with you:\n\n";
                shareText += sharedPosition.getName();

                if (!sharedPosition.getShoppingItem().getItemUrl().isEmpty()) {
                    shareText += "\n\nYou can find it here:\n " + sharedPosition.getShoppingItem().getItemUrl() + " \n";
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                v.getContext().startActivity(shareIntent);
            }
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
        public Button shareButtonView;

        private ItemClickListener itemClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.isCompletedCheckbox);
            itemImage = itemView.findViewById(R.id.itemImageView);
            listNameText = itemView.findViewById(R.id.listNameText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            shareButtonView = itemView.findViewById(R.id.shareButtonView);

            checkBox.setOnClickListener(this);
            shareButtonView.setOnClickListener(this);
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
