package com.example.firechatapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");

    //Widgets
    EditText userET, passET, emailET;
    Button registerBtn;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initializing Widgets;
        userET = findViewById(R.id.userEditText);
        passET = findViewById(R.id.passEditText);
        emailET = findViewById(R.id.emailEditText);
        registerBtn = findViewById(R.id.buttonRegister);


        //Firebase Auth
        // auth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();

        // Adding Event Listener to Button Register
        registerBtn.setOnClickListener(v -> {
            String username_text = userET.getText().toString().trim();
            String email_text = emailET.getText().toString().trim();
            String pass_text = passET.getText().toString().trim();

            boolean isEmailValid = validateEmail(email_text);
            boolean isPasswordValid = validatePassword(pass_text);

            if (!isEmailValid || isPasswordValid) {
                return;
            }

            if (TextUtils.isEmpty(username_text) || TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)) {
                Toast.makeText(RegisterActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            } else {
                RegisterNow(email_text, username_text, pass_text);
            }
        });

    }

    private boolean validateEmail(String email_text) {

        // if the email input field is empty
        if (email_text.isEmpty()) {
            emailET.setError("Field can not be empty");
            return false;
        }

        // Matching the input email to a predefined email pattern
        else if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
            emailET.setError("Please enter a valid email address");
            return false;
        } else {
            emailET.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String pass_text) {
        // if password field is empty
        // it will display error message "Field can not be empty"
        if (pass_text.isEmpty()) {
            passET.setError("Field can not be empty");
            return false;
        }

        // if password does not matches to the pattern
        // it will display an error message "Password is too weak"
        else if (!PASSWORD_PATTERN.matcher(pass_text).matches()) {
            passET.setError("Password is too weak");
            return false;
        } else {
            passET.setError(null);
            return true;
        }
    }


    private void RegisterNow(String username, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d("Register", "firebaseAuth");
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        String userid = firebaseUser.getUid();

                        myRef = FirebaseDatabase.getInstance("https://chatfire-2d46f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("MyUsers").child(userid);

                        //HashMaps
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        hashMap.put("status", "offline");


                        Log.d("Register", myRef.toString());


                        // Opening the Main Activity after Success Registration
                        myRef.setValue(hashMap).addOnCompleteListener(task1 -> {

                            Log.d("Register", "setValue");

                            if (task1.isSuccessful()) {
                                Log.d("Register", task1.toString());
                                Intent i = new Intent(RegisterActivity.this, Login_Activity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                Log.d("Register", "failed");
                            }

                        }).addOnFailureListener(e -> {
                            Log.d("Register", e.getMessage());
                        }).addOnCanceledListener(() -> {
                            Log.d("Register", "canceled");
                        }).addOnSuccessListener(aVoid -> {
                            Log.d("Register", "Success");
                        });
                        Log.d("Register", "should success");
                    } else {
                        Toast.makeText(RegisterActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                    }

                });

    }

}