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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.devup.opointdoacai.opointdoacaiserver.Common.Common;
import com.devup.opointdoacai.opointdoacaiserver.Model.Comps;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.CompsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import es.dmoral.toasty.Toasty;

public class RegisterComps extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference list;

    FirebaseRecyclerAdapter<Comps, CompsViewHolder> adapter;

    private android.support.v7.widget.Toolbar toolbar;

    FloatingActionButton fab;

    Dialog dialog;
    EditText edt_name;
    EditText edt_description;
    EditText edt_price;
    Spinner spinner;
    String type_spinner_selected;
    Button button_confirm;

    RelativeLayout rootCompLayout;

    Comps newComp;

    @Override
    protected void onRestart() {
        super.onRestart();

        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_comps);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.comps_toolbar);
        toolbar.setTitle("Cadastro de Complementos");
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
        list = database.getReference("Comps");

        recyclerView = findViewById(R.id.comps_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootCompLayout = findViewById(R.id.root_comp_layout);

        loadList();

        fab = findViewById(R.id.fab_comp_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddCompDialog();

            }
        });

    }

    private void showAddCompDialog() {

        dialog = new Dialog(RegisterComps.this);
        dialog.setContentView(R.layout.add_new_comp_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_comp);
        edt_description = dialog.findViewById(R.id.edt_description_comp);
        edt_price = dialog.findViewById(R.id.edt_price_comp);
        spinner = dialog.findViewById(R.id.spinner_comp_types);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_comp);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    String price = edt_price.getText().toString();
                    String type = String.valueOf(spinner.getSelectedItem());

                    newComp = new Comps();
                    newComp.setName(edt_name.getText().toString());
                    newComp.setDescription(edt_description.getText().toString());
                    newComp.setPrice(price);
                    newComp.setType(type);

                    dialog.dismiss();

                    if (newComp != null){

                        list.push().setValue(newComp);
                        Snackbar.make(rootCompLayout, "Novo Complemento " + newComp.getName() + " foi adicionado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                    }

                }

            }
        });

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this, R.array.types_comp_array, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);

        spinner.setOnItemSelectedListener(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void loadList() {

        Query search = list;

        FirebaseRecyclerOptions<Comps> options = new FirebaseRecyclerOptions.Builder<Comps>()
                .setQuery(search, Comps.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Comps, CompsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CompsViewHolder holder, int position, @NonNull Comps model) {

                holder.comp_name.setText(model.getName());
                holder.comp_price.setText(model.getPrice());

            }

            @NonNull
            @Override
            public CompsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comp_item, parent, false);
                return new CompsViewHolder(view);

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateCompDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deletComp(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void deletComp(String key) {

        list.child(key).removeValue();

    }

    private void showUpdateCompDialog(final String key, final Comps item) {

        dialog = new Dialog(RegisterComps.this);
        dialog.setContentView(R.layout.add_new_comp_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_comp);
        edt_description = dialog.findViewById(R.id.edt_description_comp);
        edt_price = dialog.findViewById(R.id.edt_price_comp);
        spinner = dialog.findViewById(R.id.spinner_comp_types);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_comp);

        //Set default value for views
        edt_name.setText(item.getName());
        edt_description.setText(item.getDescription());
        edt_price.setText(item.getPrice());
        setSpinner( R.id.spinner_comp_types, R.array.types_comp_array, item.getType() );

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price.getText()) || TextUtils.isEmpty(edt_name.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    dialog.dismiss();

                        String price = edt_price.getText().toString();
                        String type = String.valueOf(spinner.getSelectedItem());

                        //Update Infos of Comps
                        item.setName(edt_name.getText().toString());
                        item.setDescription(edt_description.getText().toString());
                        item.setPrice(price);
                        item.setType(type);

                        list.child(key).setValue(item);
                        Snackbar.make(rootCompLayout, "Complemento " + item.getName() + " editado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                }

            }
        });

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this, R.array.types_comp_array, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);

        spinner.setOnItemSelectedListener(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void setSpinner(int spinner_comp_types, int types_comp_array, String type) {

        Spinner spinner = dialog.findViewById( spinner_comp_types );
        String[] cidades = getResources().getStringArray(types_comp_array);

        for( int i = 0; i < cidades.length; i++ ){
            if( cidades[i].equals(type) ){
                spinner.setSelection( i );
                break;
            }else if( cidades[i].equals(type) ){
                spinner.setSelection( i );
                break;
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        type_spinner_selected = adapterView.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
