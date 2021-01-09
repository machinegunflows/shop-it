package de.samuelhuebner.shopit.shoppinglist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import de.samuelhuebner.shopit.MainActivity;
import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.adapter.ListPositionAdapter;
import de.samuelhuebner.shopit.database.Category;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.EventType;
import de.samuelhuebner.shopit.database.HistoryEvent;
import de.samuelhuebner.shopit.database.ListPosition;
import de.samuelhuebner.shopit.database.ShoppingItem;
import de.samuelhuebner.shopit.database.ShoppingList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_UUID = "LIST_UUID";

    private String listUUID;
    private Database db;
    private ShoppingList list;

    // we need to keep those references to show the create window
    private ConstraintLayout layout;
    private CardView cardView;
    private RecyclerView recyclerView;

    // all references needed for the background
    private final ColorDrawable swipeBackground = new ColorDrawable(Color.parseColor("#FF0000"));
    private Drawable deleteIcon;

    // the adapter reference
    private ListPositionAdapter adapter;

    // context variables
    private Context context;
    private MainActivity mainActivity;

    /**
     * These variables keep track of the previously deleted item so that the undo button works properly
     */
    private int deletedPos = -1;
    private ListPosition deletedListPos = null;
    private Toolbar bar;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uuid          The uuid of the corresponding list
     * @param mainActivity  The main activity
     *
     * @return A new instance of fragment ShoppingListFragment.
     */
    public static ShoppingListFragment newInstance(String uuid, MainActivity mainActivity) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();
        args.putString(LIST_UUID, uuid);
        fragment.setArguments(args);
        fragment.mainActivity = mainActivity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.db = new Database(getContext());
        if (getArguments() != null) {
            listUUID = getArguments().getString(LIST_UUID);
            Log.d("Fragment:", listUUID);

            list = db.getShoppingList(listUUID);
        }

        this.adapter = new ListPositionAdapter(list.getPositions(), this.db);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View newView = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        this.context = newView.getContext();
        this.deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.icon_deleted_pos);
        this.adapter.setContext(context);
        this.adapter.setFragment(this);

        setupView(newView);
        setupSpinner(newView);
        return newView;
    }

    private void setupSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.categorySpinner);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("no-category");

        Object[] tmp = Category.values();
        for (Object o : tmp) {
            categories.add(o.toString().toLowerCase());
        }

        spinner.setAdapter(new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, categories));
    }

    /**
     * Sets up the ShoppingListFragment view
     *
     * @param view  The view
     */
    private void setupView(View view) {
        // Setting the child toolbar
        this.bar = view.findViewById(R.id.shoppingListToolbar);
        bar.setTitle(this.list.getName());
        bar.inflateMenu(R.menu.shopping_list_settings_menu);

        bar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.editMenuEntry:
                    Intent editListIntent = new Intent(getActivity(), EditShoppingListActivity.class);
                    editListIntent.putExtra("LIST_UUID", listUUID);
                    startActivityForResult(editListIntent, 801);
                    break;
                case R.id.deleteMenuEntry:
                    db.deleteShoppingList(list.getUuid());
                    HistoryEvent deleteEvent = new HistoryEvent("Deleted shopping list: " + list.getName(), EventType.DELETED_LIST);
                    db.addHistoryEvent(deleteEvent);

                    this.mainActivity.handleSwitchToAllEvent(getView());
                    break;
                case R.id.shareMenuEntry:
                    String shareText = list.getName() + ":";
                    for (ListPosition pos : list.getPositions()) {
                        shareText += "\n- " + pos.getName();
                    }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    sendIntent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    view.getContext().startActivity(shareIntent);
            }

            return true;
        });

        // now we have to make the card view invisible
        cardView = view.findViewById(R.id.newItemCardView);
        cardView.setVisibility(View.INVISIBLE);

        // finally we have to connect the list view to the top of our constraint layout
        layout = view.findViewById(R.id.parentLayoutShoppingListView);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(R.id.shoppingPositionsListView, ConstraintSet.TOP, R.id.shoppingListToolbar, ConstraintSet.BOTTOM);
        constraintSet.applyTo(layout);

        this.setupRecyclerView(view);
    }

    /**
     * Sets up the recycler view (touch helper, click listeners)
     * @param view
     */
    private void setupRecyclerView(View view) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deletedPos = viewHolder.getAdapterPosition();
                deletedListPos = list.getPositions().get(deletedPos);
                db.deleteListPosition(deletedListPos);
                adapter.notifyItemRemoved(deletedPos);

                Snackbar.make(viewHolder.itemView, deletedListPos.getName() + " deleted.", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v -> {
                            db.addListPosition(deletedListPos, deletedListPos.getId());
                            list.addPosition(deletedListPos, deletedPos);
                            adapter.notifyItemInserted(deletedPos);

                            deletedListPos = null;
                            deletedPos = -1;
                        })
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int iconMargin = itemView.getHeight() - deleteIcon.getIntrinsicHeight() / 2;

                swipeBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                deleteIcon.setBounds(itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth(),
                        itemView.getTop() + iconMargin,
                        itemView.getRight() - iconMargin,
                        itemView.getBottom() - iconMargin);

                swipeBackground.draw(c);
                deleteIcon.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };


        recyclerView = view.findViewById(R.id.shoppingPositionsListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        recyclerView.setAdapter(this.adapter);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!(requestCode == 801) && !(requestCode == 802)) return;
        if (!(resultCode == Activity.RESULT_OK)) return;

        if (requestCode == 801) {
            bar.setTitle(this.list.getName());
        } else {
            if (data != null) {
                int viewPos = data.getIntExtra("VIEW_POS", 0);
                String itemName = data.getStringExtra("ITEM_NAME");
                String itemNotes = data.getStringExtra("ITEM_NOTES");
                String itemUrl = data.getStringExtra("ITEM_URL");
                String itemCategory = data.getStringExtra("ITEM_CATEGORY");

                ListPosition listPosition = this.list.getPositions().get(viewPos);
                listPosition.setName(itemName);
                listPosition.getShoppingItem().setNotes(itemNotes);
                listPosition.getShoppingItem().setItemUrl(itemUrl);
                listPosition.getShoppingItem().setCategory(itemCategory);
                db.savePosition(listPosition);
                this.adapter.notifyItemChanged(viewPos);
                HistoryEvent updatedEvent = new HistoryEvent("Updated " + listPosition.getName(), EventType.MODIFIED_POSITION );
                db.addHistoryEvent(updatedEvent);
            }
        }
    }

    public void handleCreatePosEvent(View view) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(R.id.shoppingPositionsListView, ConstraintSet.TOP, R.id.newItemCardView, ConstraintSet.BOTTOM);
        constraintSet.applyTo(layout);

        cardView.setVisibility(View.VISIBLE);

        TextView input = cardView.findViewById(R.id.newPositionName);
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager)this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @SuppressLint("SetTextI18n")
    public void handleSavePosEvent(View view) {
        // hides the keyboard when the value was saved
        InputMethodManager imm = (InputMethodManager) this.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        // gets the category spinner and name edit text views
        Spinner spinner = cardView.findViewById(R.id.categorySpinner);
        EditText positionName = cardView.findViewById(R.id.newPositionName);
        EditText positionNotes = cardView.findViewById(R.id.newPositionNotes);
        EditText positionLink = cardView.findViewById(R.id.newPositionLink);

        String value = spinner.getSelectedItem().toString();
        Category newCat;
        if (value.isEmpty() || value.equals("no-category")) {
            newCat = null;
        } else {
            newCat = Category.valueOf(value.toUpperCase());
        }

        ListPosition newListPos = new ListPosition(new ShoppingItem(positionName.getText().toString(), newCat), 1, listUUID);
        newListPos.getShoppingItem().setItemUrl(positionLink.getText().toString());
        newListPos.getShoppingItem().setNotes(positionNotes.getText().toString());
        this.list.addPosition(newListPos);
        this.db.addListPosition(newListPos);

        String historyText = "Added entry: " + newListPos.getName() + " to list: " + this.list.getName();
        this.db.addHistoryEvent(new HistoryEvent(historyText, EventType.CREATED_POSITION));

        spinner.setSelection(0);
        positionName.setText("Text");
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(R.id.shoppingPositionsListView, ConstraintSet.TOP, R.id.shoppingListToolbar, ConstraintSet.BOTTOM);
        constraintSet.applyTo(layout);

        cardView.setVisibility(View.INVISIBLE);
        this.adapter.notifyDataSetChanged();
    }
}