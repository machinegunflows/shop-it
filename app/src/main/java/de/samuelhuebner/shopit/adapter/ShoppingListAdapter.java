package de.samuelhuebner.shopit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Database;

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
        String name = db.getShoppingListsNames().get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shopping_lists_item, parent, false);
        }

        TextView listName = convertView.findViewById(R.id.shoppingListsName);
        listName.setText(name);

        return convertView;
    }
}
