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
import com.devup.opointdoacai.opointdoacaiserver.Model.Juices;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.JuicesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import es.dmoral.toasty.Toasty;

public class RegisterJuices extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference list;

    FirebaseRecyclerAdapter<Juices, JuicesViewHolder> adapter;

    private android.support.v7.widget.Toolbar toolbar;

    FloatingActionButton fab;

    Dialog dialog;
    EditText edt_name;
    EditText edt_price;
    Spinner spinner;
    String base_spinner_selected;
    Button button_confirm;

    RelativeLayout rootJuiceLayout;

    Juices newJuice;

    @Override
    protected void onRestart() {
        super.onRestart();

        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_juices);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.juices_toolbar);
        toolbar.setTitle("Cadastro de Sucos");
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
        list = database.getReference("Juices");

        recyclerView = findViewById(R.id.juices_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootJuiceLayout = findViewById(R.id.root_juice_layout);

        loadList();

        fab = findViewById(R.id.fab_juices_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddJuiceDialog();

            }
        });

    }

    private void showAddJuiceDialog() {

        dialog = new Dialog(RegisterJuices.this);
        dialog.setContentView(R.layout.add_new_juices_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_juices);
        edt_price = dialog.findViewById(R.id.edt_price_juices);
        spinner = dialog.findViewById(R.id.spinner_base_juices);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_juices);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    String price = edt_price.getText().toString();
                    String base = String.valueOf(spinner.getSelectedItem());
                    
                    newJuice = new Juices();
                    if (base.equals("Leite")){
                        newJuice.setDescription("Com Leite - 700 ml");
                    }else if (base.equals("Agua")){
                        newJuice.setDescription("Com Àgua - 700 ml");
                    }
                    newJuice.setName(edt_name.getText().toString());
                    newJuice.setPrice(price);
                    newJuice.setBase(base);

                    dialog.dismiss();

                    if (newJuice != null){

                        list.push().setValue(newJuice);
                        Snackbar.make(rootJuiceLayout, "Novo Suco " + newJuice.getName() + " foi adicionado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                    }

                }

            }
        });

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this, R.array.base_juices_array, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);

        spinner.setOnItemSelectedListener(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void loadList() {

        Query search = list;

        FirebaseRecyclerOptions<Juices> options = new FirebaseRecyclerOptions.Builder<Juices>()
                .setQuery(search, Juices.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Juices, JuicesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JuicesViewHolder holder, int position, @NonNull Juices model) {

                holder.juice_name.setText(model.getName());
                holder.juice_description.setText(model.getDescription());
                holder.juice_price.setText(model.getPrice());

            }

            @NonNull
            @Override
            public JuicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.juices_item, parent, false);
                return new JuicesViewHolder(view);

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

            deletJuice(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateCompDialog(final String key, final Juices item) {

        dialog = new Dialog(RegisterJuices.this);
        dialog.setContentView(R.layout.add_new_juices_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_juices);
        edt_price = dialog.findViewById(R.id.edt_price_juices);
        spinner = dialog.findViewById(R.id.spinner_base_juices);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_juices);

        //Set default value for views
        edt_name.setText(item.getName());
        edt_price.setText(item.getPrice());
        setSpinner( R.id.spinner_base_juices, R.array.base_juices_array, item.getBase() );

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    dialog.dismiss();

                    String price = edt_price.getText().toString();
                    String base = String.valueOf(spinner.getSelectedItem());

                    if (base.equals("Leite")){
                        item.setDescription("Com Leite - 700 ml");
                    }else if (base.equals("Agua")){
                        item.setDescription("Com Àgua - 700 ml");
                    }
                    //Update Infos of Comps
                    item.setName(edt_name.getText().toString());
                    item.setPrice(price);
                    item.setBase(base);

                        list.child(key).setValue(item);
                        Snackbar.make(rootJuiceLayout, "O Suco " + item.getName() + " foi ditado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                }

            }
        });

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this, R.array.base_juices_array, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);

        spinner.setOnItemSelectedListener(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void setSpinner(int spinner_base_juices, int base_juices_array, String base) {

        Spinner spinner = dialog.findViewById( spinner_base_juices );
        String[] bases = getResources().getStringArray(base_juices_array);

        for( int i = 0; i < bases.length; i++ ){
            if( bases[i].equals(base) ){
                spinner.setSelection( i );
                break;
            }else if( bases[i].equals(base) ){
                spinner.setSelection( i );
                break;
            }
        }

    }

    private void deletJuice(String key) {

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        base_spinner_selected = adapterView.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
