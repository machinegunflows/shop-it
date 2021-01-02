package de.samuelhuebner.shopit.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.EventType;
import de.samuelhuebner.shopit.database.HistoryEvent;
import de.samuelhuebner.shopit.database.ShoppingList;

public class EditShoppingListActivity extends AppCompatActivity {

    private Database db;
    private EditText listName;
    private ShoppingList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);

        this.db = new Database(this);
        this.listName = findViewById(R.id.listNameInput);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                setupEditWindow(extras);
            }
        }

        this.listName.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void setupEditWindow(Bundle extras) {
        String uuid = extras.getString("LIST_UUID", null);

        if (uuid == null) return;

        this.list = this.db.getShoppingList(uuid);
        this.listName.setText(this.list.getName());
    }

    public void handleCreateListEvent(View view) {
        Intent result = new Intent();

        String name = String.valueOf(listName.getText());

        // if no text is in the TextView we have to show a corresponding message!
        if (name.length() == 0) {
            Snackbar.make(view, "A list name has to be provided!", Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.createListButton))
                    .show();
            return;
        }

        // updates the changes and creates a new history event
        list.setName(name);
        db.updateList(this.list);
        db.addHistoryEvent(new HistoryEvent("Modified shopping list: " + list.getName(), EventType.MODIFIED_LIST));

        // puts the UUID as a result (in case its needed)
        result.putExtra("LIST_UUID", list.getUuid());

        // hides the keyboard when the value was saved
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        setResult(Activity.RESULT_OK, result);
        finish();
    }
}