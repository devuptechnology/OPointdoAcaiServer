package com.devup.opointdoacai.opointdoacaiserver.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.devup.opointdoacai.opointdoacaiserver.Common.Common;
import com.devup.opointdoacai.opointdoacaiserver.Interface.ItemClickListener;
import com.devup.opointdoacai.opointdoacaiserver.Model.MyResponse;
import com.devup.opointdoacai.opointdoacaiserver.Model.Notification;
import com.devup.opointdoacai.opointdoacaiserver.Model.Request;
import com.devup.opointdoacai.opointdoacaiserver.Model.Sender;
import com.devup.opointdoacai.opointdoacaiserver.Model.Token;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.Remote.APIService;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus2 extends AppCompatActivity {

    private android.support.v7.widget.Toolbar toolbar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase db;
    DatabaseReference requests;
    FirebaseAuth mAuth;

    MaterialSpinner spinner;

    APIService mService;

    @Override
    protected void onRestart() {
        super.onRestart();

        if (Common.isConnectedToInternet(getBaseContext())) {
            loadOrders();
        }else{
            Toasty.error(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status2);

        //Iniciando Serviços
        mService = Common.getFCMClient();

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.toolbar_id_order_aguardando);
        toolbar.setTitle("Pedidos Aguardando");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Firebase
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Pedidos");
        mAuth = FirebaseAuth.getInstance();

        //Init
        recyclerView = findViewById(R.id.listOrders2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (Common.isConnectedToInternet(getBaseContext())) {
            loadOrders();
        }else{
            Toasty.error(getApplicationContext(), "Sem conexão com a Internet", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void loadOrders() {

        Query search = requests.orderByChild("status").equalTo("1");

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(search, Request.class)
                .build();

        adapter  = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull final Request model) {
                viewHolder.txtOrderId.setText("Numero do Pedido: " + adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText("Status: " + Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText("Endereço: " + model.getEndereco());
                viewHolder.txtOrderPhone.setText("Telefone: " + model.getTelefone());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        if (!isLongClick){

                            Intent orderDetailIntent = new Intent(OrderStatus2.this, OrderDetail.class);
                            Common.currentRequest = model;
                            orderDetailIntent.putExtra("OrderId", adapter.getRef(position).getKey());
                            startActivity(orderDetailIntent);
                            finish();

                        }else{

                            showUpdateDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                        }
                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout, parent, false);
                return new OrderViewHolder(itemView);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deleteOrder(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {

        requests.child(key).removeValue();

    }

    private void showUpdateDialog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus2.this);
        alertDialog.setTitle("Atualizar Pedido");
        alertDialog.setMessage("Por favor escolha o Status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Pedido", "Aguardando", "Saiu para Entrega", "Entregue");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);

                sendOrderStatusToUser(localKey, item);

            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        alertDialog.show();

    }

    private void sendOrderStatusToUser(String localKey, Request item) {

        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getTelefone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){

                            Token token = postSnapShot.getValue(Token.class);

                            //Constroi o raw payload
                            Notification notification = new Notification("Seu Pedido foi atualizado!", "O Point do Açaí");
                            Sender content = new Sender(token.getToken(), notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if (response.body().success == 1){

                                                Toasty.success(OrderStatus2.this, " O Pedido foi atualizado", Toast.LENGTH_SHORT).show();

                                            }else{

                                                Toasty.info(OrderStatus2.this, " O Pedido foi atualizado, mas a notificação falhou ao ser enviada ao cliente", Toast.LENGTH_SHORT).show();

                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                            Log.e("ERROR", t.getMessage());

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
