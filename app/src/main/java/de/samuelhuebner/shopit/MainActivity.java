package de.samuelhuebner.shopit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.samuelhuebner.shopit.history.HistoryFragment;
import de.samuelhuebner.shopit.profile.ProfileFragment;
import de.samuelhuebner.shopit.shoppinglist.ShoppingListsFragment;

public class MainActivity extends AppCompatActivity {
    private ShoppingListsFragment lists;
    private ProfileFragment profile;
    private HistoryFragment history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFragments();
        setupToolbar();
    }

    @Override
    public void setSupportActionBar(@Nullable androidx.appcompat.widget.Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    /**
     * Helper method which replaces the current fragment with the given one
     * @param fragment  The new fragment that has to be visible
     */
    private void setCurrentFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.viewFragment, fragment).commit();
    }

    /**
     * Method which sets up the Toolbar for our app
     */
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Method which sets up the Fragments for the app and adds a listener for the bottom navigation bar
     */
    private void setupFragments() {
        this.lists = new ShoppingListsFragment();
        this.profile = new ProfileFragment();
        this.history = new HistoryFragment();

        setCurrentFragment(lists);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {
            switch (item.getItemId()) {
                case R.id.listViewMenuItem:
                    setCurrentFragment(lists);
                    break;
                case R.id.historyViewMenuItem:
                    setCurrentFragment(history);
                    break;
                case R.id.profileViewMenuItem:
                    setCurrentFragment(profile);
                    break;
            }
            return true;
        });
    }

    public void handleCreateListEvent(View view) {
        this.lists.handleCreateListEvent(view);
    }
}