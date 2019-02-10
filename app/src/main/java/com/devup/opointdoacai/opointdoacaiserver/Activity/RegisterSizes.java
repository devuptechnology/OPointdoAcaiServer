package com.devup.opointdoacai.opointdoacaiserver.Activity;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.devup.opointdoacai.opointdoacaiserver.Common.Common;
import com.devup.opointdoacai.opointdoacaiserver.Model.Comps;
import com.devup.opointdoacai.opointdoacaiserver.Model.Sizes;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.SizesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import es.dmoral.toasty.Toasty;

public class RegisterSizes extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference list;

    FirebaseRecyclerAdapter<Sizes, SizesViewHolder> adapter;

    private android.support.v7.widget.Toolbar toolbar;

    FloatingActionButton fab;

    Dialog dialog;
    EditText edt_name;
    EditText edt_price;
    Button button_confirm;

    RelativeLayout rootSizesLayout;

    Sizes newSize;

    @Override
    protected void onRestart() {
        super.onRestart();

        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sizes);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.sizes_toolbar);
        toolbar.setTitle("Cadastro de Tamanhos de Copos");
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
        list = database.getReference("Sizes");

        recyclerView = findViewById(R.id.sizes_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootSizesLayout = findViewById(R.id.root_size_layout);

        loadList();

        fab = findViewById(R.id.fab_sizes_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddSizeDialog();

            }
        });

    }

    private void showAddSizeDialog() {

        dialog = new Dialog(RegisterSizes.this);
        dialog.setContentView(R.layout.add_new_sizes_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_sizes);
        edt_price = dialog.findViewById(R.id.edt_price_sizes);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_sizes);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    String price = edt_price.getText().toString();

                    newSize = new Sizes();
                    newSize.setName(edt_name.getText().toString());
                    newSize.setPrice(price);

                    dialog.dismiss();

                    if (newSize != null){

                        list.push().setValue(newSize);
                        Snackbar.make(rootSizesLayout, "Novo Tamanho " + newSize.getName() + " foi adicionado com sucesso!", Snackbar.LENGTH_LONG)
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

        FirebaseRecyclerOptions<Sizes> options = new FirebaseRecyclerOptions.Builder<Sizes>()
                .setQuery(search, Sizes.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Sizes, SizesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SizesViewHolder holder, int position, @NonNull Sizes model) {

                holder.size_name.setText(model.getName());
                holder.size_price.setText(model.getPrice());

            }

            @NonNull
            @Override
            public SizesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sizes_item, parent, false);
                return new SizesViewHolder(view);

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateSizeDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deletSize(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateSizeDialog(final String key, final Sizes item) {

        dialog = new Dialog(RegisterSizes.this);
        dialog.setContentView(R.layout.add_new_sizes_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_sizes);
        edt_price = dialog.findViewById(R.id.edt_price_sizes);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_sizes);

        //Set default value for views
        edt_name.setText(item.getName());
        edt_price.setText(item.getPrice());

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    dialog.dismiss();

                    String price = edt_price.getText().toString();

                    item.setName(edt_name.getText().toString());
                    item.setPrice(price);

                        list.child(key).setValue(item);
                        Snackbar.make(rootSizesLayout, "Tamanho " + item.getName() + " foi editado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();


                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void deletSize(String key) {

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
