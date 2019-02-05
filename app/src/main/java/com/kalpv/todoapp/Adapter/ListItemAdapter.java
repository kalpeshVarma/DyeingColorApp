package com.kalpv.todoapp.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kalpv.todoapp.MainActivity;
import com.kalpv.todoapp.R;
import com.kalpv.todoapp.ToDo;

import java.util.List;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> implements View.OnCreateContextMenuListener, View.OnClickListener {


    MainActivity mainActivity;
    List<ToDo> toDoList;

    public ListItemAdapter(MainActivity mainActivity, List<ToDo> toDoList) {
        this.mainActivity = mainActivity;
        this.toDoList = toDoList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {

        holder.item_title.setText(toDoList.get(position).getTitle() + " (" + toDoList.get(position).getParentId() + " Kg.)");
        holder.item_description.setText(toDoList.get(position).getDescription());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
//                mainActivity.title_et.setText(toDoList.get(position).getTitle());
//                mainActivity.description_et.setText(toDoList.get(position).getDescription());
//                mainActivity.materialSpinner.setSelectedIndex(Integer.parseInt(toDoList.get(position).getParentId())-1);
//
//                mainActivity.isUpdate = true;
//                mainActivity.idUpdate = toDoList.get(position).getId();
            }
        });

    }


    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

    }
}

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    ItemClickListener itemClickListener;
    TextView item_title, item_description;

    public ListItemViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        item_title = itemView.findViewById(R.id.item_title);
        item_description = itemView.findViewById(R.id.item_description);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//        contextMenu.setHeaderTitle("Select the action");
        contextMenu.add(0, 0, getAdapterPosition(), "Delete");
        contextMenu.add(0, 0, getAdapterPosition(), "Update");
    }
}
