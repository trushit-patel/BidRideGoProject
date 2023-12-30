package com.bidridego.driver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.bidridego.UserMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SwitchUserDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = getContext().getSharedPreferences("BidRigeGo", Context.MODE_PRIVATE);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Switch to User")
                .setMessage("Do you want to switch to User?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences preferences = getActivity().getSharedPreferences("BidRigeGo", Context.MODE_PRIVATE);

                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getUid()).child("role").setValue("user");
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("localRole", "user");
                        editor.apply();
                    }

                    /**
                     * TODO: update the role of the user to driver
                     * **/

                    Intent intent = new Intent(getContext(), UserMainActivity.class);
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
