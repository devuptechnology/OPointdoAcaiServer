package com.devup.opointdoacai.opointdoacaiserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.devup.opointdoacai.opointdoacaiserver.Common.Common;
import com.devup.opointdoacai.opointdoacaiserver.Interface.ItemClickListener;
import com.devup.opointdoacai.opointdoacaiserver.R;

public class CompsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

    public TextView comp_name;
    public TextView comp_price;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CompsViewHolder(@NonNull View itemView) {
        super(itemView);

        comp_name = itemView.findViewById(R.id.txt_comp_name);
        comp_price = itemView.findViewById(R.id.txt_comp_price);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Selecione a ação");

        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);

    }
}
