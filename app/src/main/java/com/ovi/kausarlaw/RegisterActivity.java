package com.ovi.kausarlaw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class RegisterActivity extends AppCompatActivity {
    EditText username, password, email;
    Button btn_register;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        btn_register = findViewById(R.id.btn_register);
        auth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameFromText = username.getText().toString();
                String emailFromText = email.getText().toString();
                String passwordFromText = password.getText().toString();
                //calling register function
                if (TextUtils.isEmpty(usernameFromText) || TextUtils.isEmpty(emailFromText) || TextUtils.isEmpty(passwordFromText)){
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else
                {
                    register(usernameFromText, emailFromText, passwordFromText);
                }

            }
        });

    }

    // method for authentication
    private void register ( String username, String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                   FirebaseUser user =  auth.getCurrentUser();
                    assert user != null;
                    String userId = user.getUid();
                   reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                   // hashmap for putting data on database
                    HashMap<String, String> hashMapForSendingLoginValueAtOnce = new HashMap<>();
                    hashMapForSendingLoginValueAtOnce.put("username", username);
                    hashMapForSendingLoginValueAtOnce.put("id", userId);
                    hashMapForSendingLoginValueAtOnce.put("imageUrl", "Default");
                    reference.setValue(hashMapForSendingLoginValueAtOnce).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });

                }else {
                    Toast.makeText(RegisterActivity.this,"you cant register with this mail & password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}