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
import com.devup.opointdoacai.opointdoacaiserver.Model.Tops;
import com.devup.opointdoacai.opointdoacaiserver.R;
import com.devup.opointdoacai.opointdoacaiserver.ViewHolder.TopChoicesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import es.dmoral.toasty.Toasty;

public class RegisterTops extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseDatabase database;
    private DatabaseReference list;

    FirebaseRecyclerAdapter<Tops, TopChoicesViewHolder> adapter;

    private android.support.v7.widget.Toolbar toolbar;

    FloatingActionButton fab;

    Dialog dialog;
    EditText edt_name;
    EditText edt_description;
    EditText edt_price_one;
    EditText edt_price_two;
    EditText edt_price_three;
    Spinner spinner;
    String top_spinner_selected;
    Button button_confirm;

    RelativeLayout rootTopLayout;

    Tops newTop;

    @Override
    protected void onRestart() {
        super.onRestart();

        loadList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tops);

        //Toolbar - Instanciando
        toolbar = findViewById(R.id.tops_toolbar);
        toolbar.setTitle("Cadastro Top 10 do Point");
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
        list = database.getReference("Tops");

        recyclerView = findViewById(R.id.tops_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootTopLayout = findViewById(R.id.root_top_layout);

        loadList();

        fab = findViewById(R.id.fab_tops_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddTopDialog();

            }
        });

    }

    private void showAddTopDialog() {

        dialog = new Dialog(RegisterTops.this);
        dialog.setContentView(R.layout.add_new_tops_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_tops);
        edt_description = dialog.findViewById(R.id.edt_description_tops);
        edt_price_one = dialog.findViewById(R.id.edt_price_tops_1);
        edt_price_two = dialog.findViewById(R.id.edt_price_tops_2);
        edt_price_three = dialog.findViewById(R.id.edt_price_tops_3);
        spinner = dialog.findViewById(R.id.spinner_tops_number);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_tops);

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price_one.getText()) ||
                        TextUtils.isEmpty(edt_price_two.getText()) || TextUtils.isEmpty(edt_price_three.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    String price_1 = edt_price_one.getText().toString();
                    String price_2 = edt_price_two.getText().toString();
                    String price_3 = edt_price_three.getText().toString();
                    String top_number = String.valueOf(spinner.getSelectedItem());

                    newTop = new Tops();
                    newTop.setName(edt_name.getText().toString());
                    newTop.setDescription(edt_description.getText().toString());
                    newTop.setPrice_one(price_1);
                    newTop.setPrice_two(price_2);
                    newTop.setPrice_three(price_3);
                    newTop.setTop_number(top_number);

                    dialog.dismiss();

                    if (newTop != null){

                        list.push().setValue(newTop);
                        Snackbar.make(rootTopLayout, "Novo Top 10 do Point " + newTop.getName() + " foi adicionado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();

                    }

                }

            }
        });

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this, R.array.numbers_tops_array, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);

        spinner.setOnItemSelectedListener(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void loadList() {

        Query search = list;

        FirebaseRecyclerOptions<Tops> options = new FirebaseRecyclerOptions.Builder<Tops>()
                .setQuery(search, Tops.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Tops, TopChoicesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TopChoicesViewHolder holder, int position, @NonNull Tops model) {

                holder.top_name.setText(model.getName());
                holder.top_description.setText(model.getDescription());
                holder.top_number.setText(model.getTop_number());

            }

            @NonNull
            @Override
            public TopChoicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.top_choices_item, parent, false);
                return new TopChoicesViewHolder(view);

            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE)){

            showUpdateTopDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        }else if (item.getTitle().equals(Common.DELETE)){

            deletTop(adapter.getRef(item.getOrder()).getKey());

        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateTopDialog(final String key, final Tops item) {

        dialog = new Dialog(RegisterTops.this);
        dialog.setContentView(R.layout.add_new_tops_layout);
        dialog.setTitle("");

        edt_name = dialog.findViewById(R.id.edt_nome_tops);
        edt_description = dialog.findViewById(R.id.edt_description_tops);
        edt_price_one = dialog.findViewById(R.id.edt_price_tops_1);
        edt_price_two = dialog.findViewById(R.id.edt_price_tops_2);
        edt_price_three = dialog.findViewById(R.id.edt_price_tops_3);
        spinner = dialog.findViewById(R.id.spinner_tops_number);
        button_confirm = dialog.findViewById(R.id.btn_confirm_add_tops);

        //Set default value for views
        edt_name.setText(item.getName());
        edt_description.setText(item.getDescription());
        edt_price_one.setText(item.getPrice_one());
        edt_price_two.setText(item.getPrice_two());
        edt_price_three.setText(item.getPrice_three());
        setSpinner( R.id.spinner_tops_number, R.array.numbers_tops_array, item.getTop_number() );

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(edt_name.getText()) || TextUtils.isEmpty(edt_price_one.getText()) ||
                        TextUtils.isEmpty(edt_price_two.getText()) || TextUtils.isEmpty(edt_price_three.getText())){
                    Toasty.error(getBaseContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }else {

                    dialog.dismiss();

                    String price_1 = edt_price_one.getText().toString();
                    String price_2 = edt_price_two.getText().toString();
                    String price_3 = edt_price_three.getText().toString();
                    String top_number = String.valueOf(spinner.getSelectedItem());

                    item.setName(edt_name.getText().toString());
                    item.setDescription(edt_description.getText().toString());
                    item.setPrice_one(price_1);
                    item.setPrice_two(price_2);
                    item.setPrice_three(price_3);
                    item.setTop_number(top_number);


                        list.child(key).setValue(item);
                        Snackbar.make(rootTopLayout, "O Top 10 do Point " + item.getName() + " foi editado com sucesso!", Snackbar.LENGTH_LONG)
                                .show();


                }

            }
        });

        ArrayAdapter<CharSequence> adapter_spinner = ArrayAdapter.createFromResource(this, R.array.numbers_tops_array, android.R.layout.simple_spinner_item);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter_spinner);

        spinner.setOnItemSelectedListener(this);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void setSpinner(int spinner_tops_number, int numbers_tops_array, String top_number) {

        Spinner spinner = dialog.findViewById(spinner_tops_number);
        String[] number = getResources().getStringArray(numbers_tops_array);

        for( int i = 0; i < number.length; i++ ){
            if( number[i].equals(top_number) ){
                spinner.setSelection( i );
                break;
            }else if( number[i].equals(top_number) ){
                spinner.setSelection( i );
                break;
            }
        }

    }

    private void deletTop(String key) {

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

        top_spinner_selected = adapterView.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
