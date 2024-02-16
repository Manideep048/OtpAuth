package com.example.otpauth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextPhone, editTextAddress;
    private RadioGroup radioGroupGender;
    private Button buttonSubmit;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Corrected to match your layout file

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // UI references
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        // Validate inputs (omitted for brevity)

        // Attempt to create a user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User registration success
                        String userId = mAuth.getCurrentUser().getUid();
                        User user = new User(editTextName.getText().toString().trim(),
                                editTextEmail.getText().toString().trim(),
                                ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString(),
                                editTextPhone.getText().toString().trim(),
                                editTextAddress.getText().toString().trim());
                        mDatabase.child("users").child(userId).setValue(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to store additional info", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Registration failure
                        Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // User model class
    public static class User {
        public String name, email, gender, phone, address;

        public User(String name, String email, String gender, String phone, String address) {
            this.name = name;
            this.email = email;
            this.gender = gender;
            this.phone = phone;
            this.address = address;
        }
    }
}
