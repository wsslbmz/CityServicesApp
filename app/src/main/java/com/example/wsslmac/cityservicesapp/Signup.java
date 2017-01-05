package com.example.wsslmac.cityservicesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mdata;
    private DatabaseReference mdataRef;
    private static String TAG = "signup";

    private FirebaseUser user;
    private EditText etPseudo;
    private EditText etEmail;
    private EditText etPass;
    private Button btnSignup;
    private TextView gotosignin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etPseudo = (EditText) findViewById(R.id.etPseudo);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPass = (EditText) findViewById(R.id.etPass);
        btnSignup = (Button) findViewById(R.id.btnSingup);
        gotosignin = (TextView) findViewById(R.id.gotosignin);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        user = mAuth.getCurrentUser();

        mdata = FirebaseDatabase.getInstance();
        mdataRef = mdata.getReference().child("users").child(user.getUid());


        gotosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Login.class));
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser();
            }
        });


    }


    private void signupUser() {
        final String pseudo = etPseudo.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPass.getText().toString().trim();
        if (TextUtils.isEmpty(pseudo)) {
            Toast.makeText(this, "Saisie un pseudo SVP", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Saisie un E-mail SVP", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Saisie un mot de passe SVP", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("veuillez patienter svp ...");
        progressDialog.show();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mdataRef.setValue("name :",pseudo);
                            mdataRef.setValue("email :",email);
                            mdataRef.setValue("pass :",password);
                            Toast.makeText(Signup.this, "\n" + "Enregistré avec succès", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Signup.this, Main.class));
                        }
                        else
                        {
                            Toast.makeText(Signup.this, "\n" + "Erreur d'enregistrement", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}

