package com.devup.opointdoacai.opointdoacaiserver.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.devup.opointdoacai.opointdoacaiserver.Common.Common;
import com.devup.opointdoacai.opointdoacaiserver.Model.Comps;
import com.devup.opointdoacai.opointdoacaiserver.Model.Salads;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.FruitSaladViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import es.dmoral.toasty.Toasty;

public class RegisterFruitSalads extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference list;

    FirebaseRecyclerAdapter<Salads, FruitSaladViewHolder> adapter;

    private android.support.v7.widget.Toolbar toolbar;

    FloatingActionButton fab;

    Dialog dialog;
    EditText edt_description;
    EditText edt_price;
    Button button_confirm;

    RelativeLayout rootSaladsLayout;

    Salads newSalad;

    @Override
    protected void onRestart() {
        super.onRestart();

        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_fruit_salads);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.fruit_salads_toolbar);
        toolbar.setTitle("Cadastro de Coberturas Salada de Frutas");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Firebase Instance
        database = FirebaseDatabase.getInstance();
        list = database.getReference("Salads");

        recyclerView = findViewById(R.id.fruit_salads_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootSaladsLayout = findViewById(R.id.root_salad_layout);

        loadList();

        fab = findViewById(R.id.fab_salads_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddSaladDialog();

            }
        });

    }

    private void showAddSaladDialog() {

        dialog = new Dialog(RegisterFruitSalads.this);
        dialog.setContentView(R.layout.add_new_salads_layout);
        dialog.setTitle("Adicionar Cobertura Salada de Frutas");

        edt_description = dialog.findViewById(R.id.edt_description_salads);
        edt_price = dialog.findViewById(R.id.edt_price_salads);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_salads);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_description.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    String price = edt_price.getText().toString();

                    newSalad = new Salads();
                    newSalad.setDescription(edt_description.getText().toString());
                    newSalad.setPrice(price);
                    newSalad.setName("Salada de Frutas");

                    dialog.dismiss();

                    if (newSalad != null){

                        list.push().setValue(newSalad);
                        Snackbar.make(rootSaladsLayout, "Novo Complemento " + newSalad.getDescription() + " foi adicionado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                    }

                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void loadList() {

        Query search = list;

        FirebaseRecyclerOptions<Salads> options = new FirebaseRecyclerOptions.Builder<Salads>()
                .setQuery(search, Salads.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Salads, FruitSaladViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FruitSaladViewHolder holder, int position, @NonNull Salads model) {

                holder.fruit_cobertura.setText(model.getDescription());
                holder.fruit_price.setText(model.getPrice());

            }

            @NonNull
            @Override
            public FruitSaladViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fruit_salad_item, parent, false);
                return new FruitSaladViewHolder(view);

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateSaladDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deletSalad(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deletSalad(String key) {

        list.child(key).removeValue();

    }

    private void showUpdateSaladDialog(final String key, final Salads item) {

        dialog = new Dialog(RegisterFruitSalads.this);
        dialog.setContentView(R.layout.add_new_salads_layout);
        dialog.setTitle("Editar Cobertura Salada de Frutas");

        edt_description = dialog.findViewById(R.id.edt_description_salads);
        edt_price = dialog.findViewById(R.id.edt_price_salads);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_salads);

        //Set default value for views
        edt_description.setText(item.getDescription());
        edt_price.setText(item.getPrice());

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_description.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    dialog.dismiss();

                    String price = edt_price.getText().toString();

                    //Update Infos of Comps
                    item.setDescription(edt_description.getText().toString());
                    item.setPrice(price);
                    item.setName("Salada de Frutas");

                        list.child(key).setValue(item);
                        Snackbar.make(rootSaladsLayout, "Cobertura " + item.getDescription() + " foi editada com sucesso!", Snackbar.LENGTH_LONG)
                                .show();


                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();

    }

    @Override
    public void onBackPressed() {
    }

}
