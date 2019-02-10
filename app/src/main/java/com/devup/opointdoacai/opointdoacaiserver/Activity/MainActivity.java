package com.devup.opointdoacai.opointdoacaiserver.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devup.opointdoacai.opointdoacaiserver.Common.Common;
import com.devup.opointdoacai.opointdoacaiserver.Model.Token;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;

    //Authentication
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Firestore Backend
    private FirebaseFirestore mFirestore;

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if (Common.isConnectedToInternet(getBaseContext())) {

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                    if (currentUser != null) {

                        updateToken(FirebaseInstanceId.getInstance().getToken());

                        final String user_id = mAuth.getCurrentUser().getUid();
                        String token_id = FirebaseInstanceId.getInstance().getToken();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("token_id", token_id);

                        mFirestore.collection("Users").document(user_id).update(userMap);

                    } else {
                        Intent intent = new Intent(MainActivity.this, PhoneAuth.class);
                        startActivity(intent);
                        finish();
                    }

                }
            };

        }else{
            Toasty.error(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, false);
        tokens.child(mAuth.getCurrentUser().getPhoneNumber()).setValue(data);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.login_button_main);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
