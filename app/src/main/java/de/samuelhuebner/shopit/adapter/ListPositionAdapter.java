package de.samuelhuebner.shopit.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import de.samuelhuebner.shopit.MainActivity;
import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.EventType;
import de.samuelhuebner.shopit.database.HistoryEvent;
import de.samuelhuebner.shopit.database.ListPosition;
import de.samuelhuebner.shopit.database.ShoppingList;
import de.samuelhuebner.shopit.shoppinglist.EditShoppingListActivity;
import de.samuelhuebner.shopit.shoppinglist.EditShoppingListPositionActivity;

public class ListPositionAdapter extends RecyclerView.Adapter<ListPositionAdapter.ViewHolder> {
    private final Database db;
    private ArrayList<ListPosition> positions;
    private Context context;
    private Fragment fragment;

    public ListPositionAdapter(@NonNull ArrayList<ListPosition> positions, Database db) {
        this.positions = positions;
        this.db = db;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
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
        holder.itemImage.setImageResource(R.drawable.ic_category_24px);

        holder.setItemClickListener((v, pos) -> {
            if (v.getId() == R.id.isCompletedCheckbox) {
                CheckBox cB = (CheckBox) v;
                ListPosition listPosition = this.positions.get(pos);
                listPosition.setCompleted(cB.isChecked());
                this.db.updatePosStatus(listPosition.getId(), cB.isChecked());
                Log.d("Listener", "updating checkbox");
            } else if (v.getId() == R.id.shareButtonView){
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
            } else {
                Intent editListIntent = new Intent(v.getContext(), EditShoppingListPositionActivity.class);
                editListIntent.putExtra("LIST_POSITION", pos);
                editListIntent.putExtra("LIST_UUID", this.positions.get(pos).getListUuid());
                this.fragment.startActivityForResult(editListIntent, 802);
            }
        });

        holder.setItemLongClickListener((v, pos) -> {
            ListPosition listPosition = this.positions.get(pos);
            new MaterialAlertDialogBuilder(v.getContext())
                    .setTitle(listPosition.getName())
                    .setNeutralButton("Cancel", null)
                    .setNegativeButton("Edit", (dialog, which) -> {
                        Intent editListIntent = new Intent(v.getContext(), EditShoppingListPositionActivity.class);
                        editListIntent.putExtra("LIST_POSITION", pos);
                        editListIntent.putExtra("LIST_UUID", listPosition.getListUuid());
                        ((Activity) context).startActivityForResult(editListIntent, 802);
                    })
                    .setPositiveButton("Delete", ((dialog, which) -> {
                        db.deleteListPosition(listPosition);
                        HistoryEvent deleteEvent = new HistoryEvent("Deleted " + listPosition.getName() + " from " + db.getShoppingList(listPosition.getListUuid()).getName() + ".", EventType.DELETED_POS);
                        db.addHistoryEvent(deleteEvent);

                        notifyItemRemoved(pos);
                        Snackbar.make(v, listPosition.getName() + " deleted.", Snackbar.LENGTH_LONG)
                                .setAction("Undo", view -> {
                                   db.addListPosition(listPosition, listPosition.getId());
                                   db.getShoppingList(listPosition.getListUuid()).addPosition(listPosition, pos);
                                   notifyItemRemoved(pos);

                                   HistoryEvent restoredEvent = new HistoryEvent("Restored " + listPosition.getName(), EventType.RESTORED_POS);
                                   db.addHistoryEvent(restoredEvent);
                                })
                                .show();
                    }))
                    .show()
                    .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        });
    }

    @Override
    public int getItemCount() {
        return positions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CheckBox checkBox;
        public ImageView itemImage;
        public TextView listNameText;
        public Chip categoryChip;
        public Button shareButtonView;

        private ItemClickListener itemClickListener;
        private ItemLongClickListener itemLongClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.isCompletedCheckbox);
            itemImage = itemView.findViewById(R.id.itemImageView);
            listNameText = itemView.findViewById(R.id.listNameText);
            categoryChip = itemView.findViewById(R.id.categoryChip);
            shareButtonView = itemView.findViewById(R.id.shareButtonView);

            checkBox.setOnClickListener(this);
            shareButtonView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public void setItemLongClickListener(ItemLongClickListener itemLongClickListener) {
            this.itemLongClickListener = itemLongClickListener;
        }

        @Override
        public boolean onLongClick(View v) {
            this.itemLongClickListener.onItemLongClick(v, getLayoutPosition());
            return true;
        }

        interface ItemClickListener {
            void onItemClick(View v, int pos);
        }

        interface ItemLongClickListener {
            void onItemLongClick(View v, int pos);
        }
    }

    public void setItems(ArrayList<ListPosition> newPositions) {
        this.positions = newPositions;
    }
}
