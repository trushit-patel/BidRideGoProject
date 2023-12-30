package com.bidridego.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.bidridego.DriverMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class BecomeDriverDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Become a Driver")
                .setMessage("Do you want to become a driver?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences preferences = getActivity().getSharedPreferences("BidRigeGo", Context.MODE_PRIVATE);

                    /**
                     * TODO: update the role of the user to driver
                     * **/
                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).child("role").setValue("driver");
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("localRole", "driver");
                        editor.apply();
                    }
                    Intent intent = new Intent(getContext(), DriverMainActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Handle "No" button click or dismiss the dialog
                    dialog.dismiss();
                })
                .create();
    }
}
