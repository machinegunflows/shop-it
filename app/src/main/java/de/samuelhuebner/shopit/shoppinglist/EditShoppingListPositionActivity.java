package de.samuelhuebner.shopit.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Category;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.ListPosition;
import de.samuelhuebner.shopit.database.ShoppingItem;

public class EditShoppingListPositionActivity extends AppCompatActivity {
    private Database db;
    private EditText itemNameInput;
    private EditText itemNotesInput;
    private EditText itemLinkInput;
    private ListPosition listPosition;
    private Spinner categorySpinner;
    private boolean changed;
    private int selectedCategory;
    private int viewPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list_position);

        this.db = new Database(this);
        this.itemNameInput = findViewById(R.id.shoppingItemNameInput);
        this.itemLinkInput = findViewById(R.id.shoppingItemLinkInput);
        this.itemNotesInput = findViewById(R.id.shoppingItemNotes);
        this.categorySpinner = findViewById(R.id.categorySpinner);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                setupEditWindow(extras);
            }
        }
    }

    private void setupEditWindow(Bundle extras) {
        int pos = extras.getInt("LIST_POSITION", -1);
        String listUuid = extras.getString("LIST_UUID", null);

        if (pos == -1 || listUuid == null) return;

        this.listPosition = this.db.getShoppingList(listUuid).getPositions().get(pos);
        this.viewPos = pos;
        this.itemNameInput.setText(listPosition.getName());
        this.itemLinkInput.setText(listPosition.getShoppingItem().getItemUrl());
        this.itemNotesInput.setText(listPosition.getShoppingItem().getNotes());

        ArrayList<String> categories = new ArrayList<>();
        categories.add("no-category");

        Object[] tmp = Category.values();
        for (Object o : tmp) {
            categories.add(o.toString().toLowerCase());
        }

        int selectedCategory = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).toLowerCase().equals(this.listPosition.getCategory().toLowerCase())) {
                selectedCategory = i;
                break;
            }
        }

        this.categorySpinner.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, categories));
        this.categorySpinner.setSelection(selectedCategory);

        View.OnKeyListener onKeyListener = (v, keyCode, event) -> {
            this.changed = true;
            return true;
        };

        this.itemNameInput.setOnKeyListener(onKeyListener);
        this.itemNotesInput.setOnKeyListener(onKeyListener);
        this.itemLinkInput.setOnKeyListener(onKeyListener);
        this.selectedCategory = this.categorySpinner.getSelectedItemPosition();
    }

    public void handleSavePosEvent(View view) {

        // hides the keyboard when the value was saved
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        Intent result = new Intent();
        result.putExtra("VIEW_POS", this.viewPos);
        result.putExtra("ITEM_NAME", this.itemNameInput.getText().toString());
        result.putExtra("ITEM_NOTES", this.itemNotesInput.getText().toString());
        result.putExtra("ITEM_URL", this.itemLinkInput.getText().toString());

        String selectedCategory = this.categorySpinner.getSelectedItem().toString();
        if (!selectedCategory.equals("no-category")) {
            result.putExtra("ITEM_CATEGORY", selectedCategory.toUpperCase());
        }

        setResult(Activity.RESULT_OK, result);
        finish();
    }

    public void handleAddImageEvent(View view) {

    }
}