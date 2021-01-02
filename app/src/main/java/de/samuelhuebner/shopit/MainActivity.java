package de.samuelhuebner.shopit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.samuelhuebner.shopit.database.Database;
import de.samuelhuebner.shopit.history.HistoryFragment;
import de.samuelhuebner.shopit.profile.ProfileFragment;
import de.samuelhuebner.shopit.shoppinglist.ShoppingListFragment;
import de.samuelhuebner.shopit.shoppinglist.ShoppingListsFragment;

public class MainActivity extends AppCompatActivity {

    // fragment object variables
    private ShoppingListsFragment lists;
    private ProfileFragment profile;
    private HistoryFragment history;
    private ShoppingListFragment list;

    //
    private boolean singleListMode = false;
    private String activeUuid = "";
    private Fragment currentFragment = null;
    private String currentFragmentId = "";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mAuth = FirebaseAuth.getInstance();

        this.lists = new ShoppingListsFragment();
        this.history = new HistoryFragment();
        this.profile = new ProfileFragment();

        setupFragments();
        setupToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.viewFragment);

        String activeFragmentName;

        // we have to put the current fragment and if needed the selected list uuid into the shared preferences
        if (currentFragment instanceof ShoppingListFragment) {
            activeFragmentName = "list";
            editor.putString("ACTIVE_LIST_UUID", this.activeUuid);
        } else if (currentFragment instanceof ShoppingListsFragment) {
            activeFragmentName = "lists";
        } else if (currentFragment instanceof HistoryFragment) {
            activeFragmentName = "history";
        } else {
            activeFragmentName = "profile";
        }

        editor.putString("ACTIVE_FRAGMENT", activeFragmentName);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        String fragmentName = prefs.getString("ACTIVE_FRAGMENT", "");
        switch (fragmentName) {
            case "history":
                this.history = new HistoryFragment();
                this.currentFragment = this.history;
                break;
            case "profile":
                this.profile = ProfileFragment.newInstance();
                this.currentFragment = this.profile;
                break;
            case "list":
                String uuid = prefs.getString("ACTIVE_LIST_UUID", "");
                if (Database.contains(uuid)) {
                    this.list =  ShoppingListFragment.newInstance(uuid, this);
                    this.currentFragment = this.list;
                    this.setSingleListMode(true);
                    break;
                }
            default:
                this.lists = new ShoppingListsFragment();
                this.currentFragment = this.lists;
                break;
        }

        this.currentFragmentId = (fragmentName.equals("")) ? "list" : fragmentName;
        this.setupToolbar();
        this.setupFragments();
    }

    @Override
    public void setSupportActionBar(@Nullable androidx.appcompat.widget.Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    /**
     * Helper method which replaces the current fragment with the given one
     * @param fragment  The new fragment that has to be visible
     */
    public void setCurrentFragment(@NonNull Fragment fragment, @Nullable Integer animIn, @Nullable Integer animOut) {
        if (animIn == null || animOut == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.viewFragment, fragment).commit();
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.viewFragment, fragment)
                .setCustomAnimations(animIn, animOut)
                .commit();
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
        if (currentFragment == null) {
            this.currentFragment = this.lists;
        }

        setCurrentFragment(currentFragment, R.anim.fragment_fade_enter, R.anim.fragment_open_exit);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        switch (currentFragmentId) {
            case "history":
                bottomNav.setSelectedItemId(R.id.historyViewMenuItem);
                break;
            case "profile":
                bottomNav.setSelectedItemId(R.id.profileViewMenuItem);
                break;
            default:
                bottomNav.setSelectedItemId(R.id.listViewMenuItem);
                break;
        }

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Button b = findViewById(R.id.switchButton);
            switch (item.getItemId()) {
                case R.id.listViewMenuItem:
                    if (singleListMode) {
                        b.setVisibility(View.VISIBLE);
                        setCurrentFragment(list, R.anim.fragment_fade_enter, R.anim.slide_out);
                        break;
                    }
                    setCurrentFragment(lists, R.anim.fragment_fade_enter, R.anim.fragment_open_exit);
                    break;
                case R.id.historyViewMenuItem:
                    b.setVisibility(View.INVISIBLE);
                    setCurrentFragment(history, null, null);
                    break;
                case R.id.profileViewMenuItem:
                    b.setVisibility(View.INVISIBLE);
                    setCurrentFragment(profile, null, null);
                    break;
            }
            return true;
        });
    }

    public void handleCreateListEvent(View view) {
        this.lists.handleCreateListEvent(view);
    }

    public void handleCreatePosEvent(View view) { this.list.handleCreatePosEvent(view); }

    public void handleSavePosEvent(View view) { this.list.handleSavePosEvent(view); }

    public void handleSwitchToAllEvent(View view) {
        setSingleListMode(false);
        this.activeUuid = "";
        this.setCurrentFragment(this.lists, null, null);
    }

    public Fragment createNewSingleListFragment(String uuid) {
        this.activeUuid = uuid;
        this.list = ShoppingListFragment.newInstance(uuid, this);
        return this.list;
    }

    public void setSingleListMode(boolean singleListMode) {
        Button b = findViewById(R.id.switchButton);
        if (singleListMode) {
            b.setVisibility(View.VISIBLE);
        } else {
            b.setVisibility(View.INVISIBLE);
        }
        this.singleListMode = singleListMode;
    }

    public void handleClearHistoryEvent(View view) {
        this.history.handleClearHistoryEvent(view);
    }
}