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
import com.devup.opointdoacai.opointdoacaiserver.Model.Vitamins;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.VitaminsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import es.dmoral.toasty.Toasty;

public class RegisterVitamins extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference list;

    FirebaseRecyclerAdapter<Vitamins, VitaminsViewHolder> adapter;

    private android.support.v7.widget.Toolbar toolbar;

    FloatingActionButton fab;

    Dialog dialog;
    EditText edt_description;
    EditText edt_price;
    Button button_confirm;

    RelativeLayout rootVitaminLayout;

    Vitamins newVitamin;

    @Override
    protected void onRestart() {
        super.onRestart();

        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vitamins);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.vitamins_toolbar);
        toolbar.setTitle("Cadastro de Vitaminas");
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
        list = database.getReference("Vitamins");

        recyclerView = findViewById(R.id.vitamins_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootVitaminLayout= findViewById(R.id.root_vitamin_layout);

        loadList();

        fab = findViewById(R.id.fab_vitamins_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddVitaminDialog();

            }
        });

    }

    private void showAddVitaminDialog() {

        dialog = new Dialog(RegisterVitamins.this);
        dialog.setContentView(R.layout.add_new_vitamins_layout);
        dialog.setTitle("");

        edt_description = dialog.findViewById(R.id.edt_description_vitamins);
        edt_price = dialog.findViewById(R.id.edt_price_vitamins);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_vitamins);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_description.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    String price = edt_price.getText().toString();

                    newVitamin = new Vitamins();
                    newVitamin.setName("Vitamina");
                    newVitamin.setDescription(edt_description.getText().toString());
                    newVitamin.setPrice(price);

                    dialog.dismiss();

                    if (newVitamin != null){

                        list.push().setValue(newVitamin);
                        Snackbar.make(rootVitaminLayout, "Nova Vitamina " + newVitamin.getName() + " foi adicionada com sucesso!", Snackbar.LENGTH_LONG)
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

        FirebaseRecyclerOptions<Vitamins> options = new FirebaseRecyclerOptions.Builder<Vitamins>()
                .setQuery(search, Vitamins.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Vitamins, VitaminsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VitaminsViewHolder holder, int position, @NonNull Vitamins model) {

                holder.vita_name.setText(model.getName());
                holder.vita_description.setText(model.getDescription());
                holder.vita_price.setText(model.getPrice());

            }

            @NonNull
            @Override
            public VitaminsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.vitamins_item, parent, false);
                return new VitaminsViewHolder(view) ;

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateVitaminDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deletVitamin(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateVitaminDialog(final String key, final Vitamins item) {

        dialog = new Dialog(RegisterVitamins.this);
        dialog.setContentView(R.layout.add_new_vitamins_layout);
        dialog.setTitle("");

        edt_description = dialog.findViewById(R.id.edt_description_vitamins);
        edt_price = dialog.findViewById(R.id.edt_price_vitamins);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_vitamins);

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

                    //Update Infos of Vitamin
                    item.setName("Vitamina");
                    item.setDescription(edt_description.getText().toString());
                    item.setPrice(price);

                        list.child(key).setValue(item);
                        Snackbar.make(rootVitaminLayout, "Vitamina " + item.getName() + " foi editada com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void deletVitamin(String key) {

        list.child(key).removeValue();

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
