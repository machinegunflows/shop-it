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

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.HistoryEvent;

public class HistoryEventAdapter extends ArrayAdapter<HistoryEvent> {
    private final Context context;
    private final Database db;

    public HistoryEventAdapter(@NonNull Context context, int resource, Database db) {
        super(context, resource, db.getHistoryEvents());
        this.context = context;
        this.db = db;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HistoryEvent event = db.getHistoryEvents().get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.historyTypeIcon);
        TextView textView = convertView.findViewById(R.id.historyText);

        textView.setText(event.getEventText());

        String resourceName = "icon_" + event.getType().toString().toLowerCase();
        imageView.setImageResource(context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName()));

        return convertView;
    }
}
