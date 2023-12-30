package com.bidridego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.bidridego.databinding.UserActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

public class UserMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private UserActivityMainBinding binding;
    DrawerLayout drawer;
    NavController navController;
    SharedPreferences preferences;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = UserActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.userAppBarMain.toolbar);

        // Initialize SharedPreferences
        preferences = getSharedPreferences("BidRigeGo", Context.MODE_PRIVATE);
        setupNavigationDrawer();
    }
    private void setupNavigationDrawer() {
        drawer = binding.drawerLayout;
        navigationView = binding.navView;
        // Set the listener for navigation item clicks
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_upcoming_trips, R.id.nav_past_trips, R.id.nav_payment, R.id.nav_become_driver, R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        navigationView.setNavigationItemSelectedListener(this);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_home) {
            navController.navigate(R.id.nav_home);
        } else if (item.getItemId() == R.id.nav_become_driver) {
                Log.d("UserMainActivity", "Become a Driver clicked");
                navController.navigate(R.id.action_nav_become_driver_to_dialog_become_driver);
        } else if(item.getItemId() == R.id.nav_logout) {
            logout();
        } else if(item.getItemId() == R.id.nav_upcoming_trips) {
            navController.navigate(R.id.nav_upcoming_trips);
        } else if(item.getItemId() == R.id.nav_past_trips) {
            navController.navigate(R.id.nav_past_trips);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    public void navigateToMyTrips() {
        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.nav_upcoming_trips);
            navController.navigate(R.id.nav_upcoming_trips);
        }
    }
}