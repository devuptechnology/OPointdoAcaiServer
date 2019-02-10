package com.devup.opointdoacai.opointdoacaiserver.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.devup.opointdoacai.opointdoacaiserver.R;

public class OrderPannel extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    private Button btn_status_pedidos;
    private Button btn_status_aguardando;
    private Button btn_status_saiuparaentrega;
    private Button btn_status_historico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pannel);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.toolbar_id_order_pannel);
        toolbar.setTitle("Gerenciar Pedidos");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_status_pedidos = findViewById(R.id.btn_status_pedidos);
        btn_status_aguardando = findViewById(R.id.btn_status_aguardando);
        btn_status_saiuparaentrega = findViewById(R.id.btn_status_saiuparaentrega);
        btn_status_historico = findViewById(R.id.btn_status_historico);

        btn_status_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrderPannel.this, OrderStatus.class);
                startActivity(intent);

            }
        });

        btn_status_aguardando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrderPannel.this, OrderStatus2.class);
                startActivity(intent);

            }
        });

        btn_status_saiuparaentrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrderPannel.this, OrderStatus3.class);
                startActivity(intent);

            }
        });

        btn_status_historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrderPannel.this, OrderHistory.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
