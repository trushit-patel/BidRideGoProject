package com.bidridego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bidridego.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText firstName;
    private EditText lastName;
    private EditText contact;
    private Button register;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String num = "1234567890";

        firstName = findViewById(R.id.firstName);
        firstName.setFilters(new InputFilter[]{new SymbolInputFilter(alpha)});
        lastName = findViewById(R.id.lastName);
        lastName.setFilters(new InputFilter[]{new SymbolInputFilter(alpha)});
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        contact.setFilters(new InputFilter[]{new SymbolInputFilter(num)});
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        register = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences("BidRigeGo", Context.MODE_PRIVATE);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!validateEmpty(email))
                        validateAndHandleEmail();
                }
            }
        });
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    validateEmpty(firstName);
                }
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    validateEmpty(lastName);
                }
            }
        });
        contact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!validateEmpty(contact))
                        validateContact();
                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!validateEmpty(password))
                        validatePasswordLength();
                }
            }
        });
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateConfirmPassword();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_firstName = firstName.getText().toString().trim();
                String txt_lastName = lastName.getText().toString().trim();
                String txt_email = email.getText().toString().trim();
                String txt_contact = contact.getText().toString().trim();
                String txt_password = password.getText().toString().trim();
                String txt_confirmPassword = confirmPassword.getText().toString().trim();

                User user = new User(txt_firstName, txt_lastName, txt_contact, "user");

                if (hasError()) {
                        Toast.makeText(getApplicationContext(),"Remove above errors",Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(user, txt_email, txt_password);
                }
            }
        });
    }
    private boolean hasError(){
        return validateEmpty(firstName) || validateEmpty(lastName) || validateEmpty(contact) || validateEmpty(email)
                || validateEmpty(password) || validatePasswordLength() || validateConfirmPassword() || validateContact()
                || validateAndHandleEmail();
    }
    private boolean validatePasswordLength(){
        String pass = password.getText().toString().trim();
        if(pass.length()<6)
        {
            password.setError("Password length must be 6 characters");
            return true;
        }else{
            password.setError(null);
            return false;
        }
    }
    private boolean validateConfirmPassword(){
        String pass = password.getText().toString().trim();
        String confirmpass = confirmPassword.getText().toString().trim();
        if(!pass.equals(confirmpass)) {
            confirmPassword.setError("Should be same as password");
            return true;
        }
        else
            confirmPassword.setError(null);
        return false;
    }
    private boolean validateContact(){
        String number = contact.getText().toString().trim();
        if(number.length()!=10)
        {
            contact.setError("Number can be 10 digits only");
            return true;
        }
        else
            contact.setError(null);
        return false;
    }
    private boolean validateEmpty(EditText et){
        String enteredText = et.getText().toString().trim();
        if(enteredText == null|| enteredText.isEmpty())
        {
            et.setError("Field cannot be empty");
            return true;
        }
        else{
            et.setError(null);
        }
        return false;
    }
    private boolean validateAndHandleEmail() {
        String enteredText = email.getText().toString().trim();

        if (!isValidEmail(enteredText)) {
            email.setError("Invalid email address");
            return true;
        }
        else {
            email.setError(null);
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        // Perform your email validation here
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void registerUser(User user, String email, String passWord) {

        auth.createUserWithEmailAndPassword(email, passWord).addOnCompleteListener(RegisterActivity.this, task -> {
            if (task.isSuccessful()) {

                DatabaseReference usersRef = firebaseDatabase.getReference("users");
                String userId = auth.getCurrentUser().getUid();
                user.setId(userId);
                usersRef.child(userId).setValue(user).addOnCompleteListener(RegisterActivity.this, databaseTask -> {
                    if (databaseTask.isSuccessful()) {
                        toast(RegisterActivity.this, "Registration successful!");
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        logout();
                        startActivity(intent);
//                        finish();
                    } else {
                        auth.getCurrentUser().delete().addOnCompleteListener(authTask -> {
                            if (authTask.isSuccessful()) {
                                toast(RegisterActivity.this, "Failed to save user data! User account deleted.");
                            } else {
                                toast(RegisterActivity.this, "Failed to save user data! Failed to delete user account.");
                            }
                        });
                    }
                });
            } else {
                toast(RegisterActivity.this, "Registration failed!");
            }
        });
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
    private void toast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    public class SymbolInputFilter implements InputFilter {

        private String allowedSymbols;

        public SymbolInputFilter(String allowedSymbols) {
            this.allowedSymbols = allowedSymbols;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            StringBuilder filteredStringBuilder = new StringBuilder();

            for (int i = start; i < end; i++) {
                char currentChar = source.charAt(i);
                if (allowedSymbols.indexOf(currentChar) != -1) {
                    filteredStringBuilder.append(currentChar);
                }
            }

            return filteredStringBuilder.toString();
        }
    }
}