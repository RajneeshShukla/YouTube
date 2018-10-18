package app.com.rajneesh.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import app.com.rajneesh.R;
import app.com.rajneesh.fragments.HomeFragment;
import app.com.rajneesh.fragments.PlayListFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Initialize ActionBar Drawer
        initActionBarDrawer(savedInstanceState);
    }


    /* Setup ActionBar Drawer */
    void initActionBarDrawer(Bundle savedInstanceState){
        mDrawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // show home fragment as default on home screen
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            mNavigationView.setCheckedItem(R.id.nav_home);
        }
    }


    /*
        Setup Navigation Menu in the drawer
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            // Home Menu
            case  R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                mNavigationView.setCheckedItem(R.id.nav_home);
                break;

            case  R.id.playlist:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PlayListFragment()).commit();
                mNavigationView.setCheckedItem(R.id.playlist);
                break;
        }

        //close Drawer
        mDrawer.closeDrawer(GravityCompat.START);
        return false;
    }


    /* Handle the back button press event */
    @Override
    public void onBackPressed() {

        // If drawer is open and back button is pressed than close the drawer otherwise do the default
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
