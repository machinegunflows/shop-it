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

public class CreateListActivity extends AppCompatActivity {

    private TextView newListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

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

        result.putExtra("LIST_NAME", name);

        // hides the keyboard when the value was saved
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

        setResult(Activity.RESULT_OK, result);
        finish();
    }
}