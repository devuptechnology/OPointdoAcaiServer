package com.devup.opointdoacai.opointdoacaiserver.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devup.opointdoacai.opointdoacaiserver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterProducts extends AppCompatActivity {

    private Button regCompsButton;
    private Button regTopsButton;
    private Button regJuicesButton;
    private Button regVitaminsButton;
    private Button regSaladsButton;
    private Button regSizesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_products);

        regCompsButton = findViewById(R.id.register_comps_btn);
        regTopsButton = findViewById(R.id.register_tops_btn);
        regJuicesButton = findViewById(R.id.register_juices_btn);
        regVitaminsButton = findViewById(R.id.register_vitamins_btn);
        regSaladsButton = findViewById(R.id.register_coberturas_fruit_salad_btn);
        regSizesButton = findViewById(R.id.register_sizes_btn);

        regCompsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterProducts.this, RegisterComps.class);
                startActivity(intent);

            }
        });

        regTopsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterProducts.this, RegisterTops.class);
                startActivity(intent);

            }
        });

        regJuicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterProducts.this, RegisterJuices.class);
                startActivity(intent);

            }
        });

        regVitaminsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterProducts.this, RegisterVitamins.class);
                startActivity(intent);

            }
        });

        regSaladsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterProducts.this, RegisterFruitSalads.class);
                startActivity(intent);

            }
        });

        regSizesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterProducts.this, RegisterSizes.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();

    }
}
