package com.bidridego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private Button register;
    private Button login;
    private FirebaseAuth mAuth;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        register  =findViewById(R.id.register);
        login = findViewById(R.id.login);

        register.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this , RegisterActivity.class)));

        login.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this , LoginActivity.class)));
        // Check if the user is already authenticated
        checkAuthentication();
    }
    private void checkAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
            String userId = currentUser.getUid();


            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve the user's role
                        String role = dataSnapshot.child("role").getValue(String.class);

                        if (role != null) {
                            // Use the user's role as needed
                            // For example, you can log it, show different UI elements, etc.
                            Log.d("UserRole", "User's role: " + role);
                            saveUserDataToLocalPreferences(role);
                            navigateToNextScreen();
                        } else {
                            // Handle the case where the role is not available
                            Log.e("UserRole", "User's role is null");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("FirebaseError", "Error getting user's role: " + databaseError.getMessage());
                }
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void saveUserDataToLocalPreferences(String role) {
        // Use SharedPreferences to store user data locally
        // Replace "your_preferences_name" with a unique name for your preferences file
        preferences = getSharedPreferences("BidRigeGo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("registeredRole", role);

        if(!preferences.contains("localRole")){
            editor.putString("localRole", "user");
        }
        editor.apply();
    }
    private void navigateToNextScreen() {
        // Replace this with the activity you want to navigate to when the user is already logged in
        String userRole = preferences.getString("localRole", "");
        Intent intent;
        // Inflate the menu based on the user's role
        if ("driver".equals(userRole)) {
            intent = new Intent(HomeActivity.this, DriverMainActivity.class);
        } else {
            intent = new Intent(HomeActivity.this, UserMainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}