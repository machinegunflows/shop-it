package de.samuelhuebner.shopit.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import de.samuelhuebner.shopit.R;
import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.database.EventType;
import de.samuelhuebner.shopit.database.HistoryEvent;
import de.samuelhuebner.shopit.database.ShoppingList;

public class CreateListActivity extends AppCompatActivity {
    private TextView newListName;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        this.db = new Database(this);

        newListName = findViewById(R.id.listNameInput);
        newListName.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * Button click handler
     * @param view      The view
     */
    public void handleCreateListEvent(View view) {
        Intent result = new Intent();

        String name = String.valueOf(newListName.getText());

        // if no text is in the TextView we have to show a corresponding message!
        if (name.length() == 0) {
            Snackbar.make(view, "A list name has to be provided!", Snackbar.LENGTH_SHORT)
                    .setAnchorView(findViewById(R.id.createListButton))
                    .show();
            return;
        }

        // creates the new list
        ShoppingList list = db.createShoppingList(name);
        db.addHistoryEvent(new HistoryEvent("Added new shopping list: " + list.getName(), EventType.CREATED_LIST));

        // puts the UUID as a result (in case its needed)
        result.putExtra("LIST_UUID", list.getUuid());

        // hides the keyboard when the value was saved
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        setResult(Activity.RESULT_OK, result);
        finish();
    }
}