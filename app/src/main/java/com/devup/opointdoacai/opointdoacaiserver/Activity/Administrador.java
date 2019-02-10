package com.devup.opointdoacai.opointdoacaiserver.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devup.opointdoacai.opointdoacaiserver.Model.Token;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Administrador extends AppCompatActivity {

    private Button btnPedidos;
    private Button btnLogout;
    private Button btnDeletePedidos;
    private Button btnRegisterProducts;

    private FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        mAuth = FirebaseAuth.getInstance();

//        updateToken(FirebaseInstanceId.getInstance().getToken());

        btnPedidos = findViewById(R.id.pedidos_btn);
        btnLogout = findViewById(R.id.logout_btn);
        btnDeletePedidos = findViewById(R.id.delete_all_orders_btn);
        btnRegisterProducts = findViewById(R.id.register_products_btn);

        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Administrador.this, OrderPannel.class);
                startActivity(intent);

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Intent intent = new Intent(Administrador.this, PhoneAuth.class);
                startActivity(intent);
                finish();

            }
        });

        btnDeletePedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Firebase
                db = FirebaseDatabase.getInstance();
                requests = db.getReference("Pedidos");

                requests.removeValue();

            }
        });

        btnRegisterProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Administrador.this, RegisterProducts.class);
                startActivity(intent);

            }
        });
    }

    private void updateToken(String token) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token, true);
//        tokens.child(mAuth.getCurrentUser().getPhoneNumber()).setValue(data);

    }

    @Override
    public void onBackPressed() {
    }
}
