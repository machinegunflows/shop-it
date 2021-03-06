package de.samuelhuebner.shopit.shoppinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.samuelhuebner.shopit.MainActivity;
import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.adapter.ShoppingListAdapter;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.EventType;
import de.samuelhuebner.shopit.database.HistoryEvent;
import de.samuelhuebner.shopit.database.ListPosition;
import de.samuelhuebner.shopit.database.ShoppingList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // private variables
    private Database db;
    private ShoppingListAdapter adapter;

    public ShoppingListsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingLists.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingListsFragment newInstance(String param1, String param2) {
        ShoppingListsFragment fragment = new ShoppingListsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.db = new Database(getContext());
        this.adapter = new ShoppingListAdapter(getContext(), R.layout.shopping_lists_item, db);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_lists, container, false);

        // now we can setup our list view
        this.setupListView(view.findViewById(R.id.shoppingListsView));

        // and return the view
        return  view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!(requestCode == 800) && !(requestCode == 801)) return;
        if (!(resultCode == Activity.RESULT_OK)) return;

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Event handler that handles the creation of a new list
     *
     * @param view      The requesting view
     */
    public void handleCreateListEvent(View view) {
        Intent newListIntent = new Intent(getActivity(), CreateListActivity.class);
        startActivityForResult(newListIntent, 800);
    }

    /**
     * Method which sets up the list view for all shopping lists
     *
     * @param listView      The ListView which has to be setup
     */
    private void setupListView(ListView listView) {
        listView.setAdapter(this.adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            this.handleListInteractionEvent(adapterView, i);
        });

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            TextView listName = view.findViewById(R.id.shoppingListsName);

            new MaterialAlertDialogBuilder(view.getContext())
                    .setTitle(listName.getText())
                    .setNeutralButton("Cancel", null)
                    .setNegativeButton("Share", (dialog, which) -> {
                        ShoppingList list = this.db.getShoppingLists()[position];
                        String shareText = list.getName() + ":";
                        for (ListPosition pos : list.getPositions()) {
                            shareText += "\n- " + pos.getName();
                        }

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        sendIntent.setType("text/plain");

                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        HistoryEvent shareEvent = new HistoryEvent("Shared " + list.getName() + ".", EventType.SHARED_LIST);
                        db.addHistoryEvent(shareEvent);
                        view.getContext().startActivity(shareIntent);
                    })
                    .setPositiveButton("Delete", ((dialog, which) -> {
                        ShoppingList deleted = db.getShoppingLists()[position];
                        db.deleteShoppingList(deleted.getUuid());
                        HistoryEvent deleteEvent = new HistoryEvent("Deleted shopping list: " + deleted.getName(), EventType.DELETED_LIST);
                        db.addHistoryEvent(deleteEvent);
                        this.adapter.notifyDataSetChanged();
                    }))
                    .show()
                    .getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

            return true;
        });
    }

    public void handleListInteractionEvent(AdapterView<?> adapterView, int pos) {
        MainActivity main = (MainActivity)getActivity();

        if (main == null) return;

        main.setSingleListMode(true);
        Fragment f = main.createNewSingleListFragment(db.getShoppingLists()[pos].getUuid());
        main.setCurrentFragment(f, null, null);
    }
}