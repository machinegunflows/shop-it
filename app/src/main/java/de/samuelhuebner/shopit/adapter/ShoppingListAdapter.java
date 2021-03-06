package de.samuelhuebner.shopit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.ShoppingItem;
import de.samuelhuebner.shopit.database.ShoppingList;

public class ShoppingListAdapter extends ArrayAdapter<String> {
    private Database db;
    private Context context;

    public ShoppingListAdapter(@NonNull Context context, int resource, Database db) {
        super(context, resource, db.getShoppingListsNames());
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ShoppingList list = db.getShoppingLists()[position];

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shopping_lists_item, parent, false);
        }

        TextView listName = convertView.findViewById(R.id.shoppingListsName);
        TextView statusText = convertView.findViewById(R.id.statusCompleted);
        listName.setText(list.getName());

        if (list.isComplete() && (list.getItemCount() != 0)) {
            ImageView imageView = convertView.findViewById(R.id.isCompletedIcon);
            imageView.setImageResource(R.drawable.ic_done_black_24dp);
            statusText.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        } else {
            String s = "" + list.getCompleted() + "/" + list.getItemCount();
            statusText.setText(s);
        }

        return convertView;
    }
}
