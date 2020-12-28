package de.samuelhuebner.shopit.history;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.adapter.HistoryEventAdapter;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.EventType;
import de.samuelhuebner.shopit.database.HistoryEvent;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private Database db;
    private HistoryEventAdapter adapter;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.db = new Database(getContext());
        this.adapter = new HistoryEventAdapter(getContext(), R.layout.history_list_item, this.db);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        this.setupListView(view.findViewById(R.id.historyListView));

        return view;
    }

    private void setupListView(ListView listView) {
        listView.setAdapter(this.adapter);
    }

    public void handleClearHistoryEvent(View view) {
        this.db.clearHistory();
        this.db.addHistoryEvent(new HistoryEvent("Cleared history", EventType.CLEARED_HISTORY));
        this.adapter.notifyDataSetChanged();
    }
}