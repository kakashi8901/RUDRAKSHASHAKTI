package com.rudraksha.rudrakshashakti.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rudraksha.rudrakshashakti.Pojo.PoojaList;
import com.rudraksha.rudrakshashakti.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PoojaListAdapter extends RecyclerView.Adapter<PoojaListAdapter.PoojaListViewHolder>{

    Context context;
    ArrayList<PoojaList> poojaList;
    List<String> list;

    public PoojaListAdapter(Context context, ArrayList<PoojaList> poojaList,List<String> list){
        this.context = context;
        this.poojaList= poojaList;
        this.list = list;
    }

    @NotNull
    @Override
    public PoojaListAdapter.PoojaListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pooja_check_list, parent, false);
        return new PoojaListAdapter.PoojaListViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull PoojaListAdapter.PoojaListViewHolder holder, int position) {
        final PoojaList poojaListModel = poojaList.get(position);
        list = new ArrayList<String>();
        if(poojaListModel.getPoojaId().equals("Select All")){
            holder.item.setTextColor(Color.parseColor("#fff"));
        }
        holder.item.setText(poojaListModel.getPoojaId());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    list.add(poojaListModel.getPoojaId());
                }else{
                    list.remove(poojaListModel.getPoojaId());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return poojaList.size();
    }

    public static class PoojaListViewHolder extends RecyclerView.ViewHolder {
        CheckBox item;

        public PoojaListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            item = (CheckBox)itemView.findViewById(R.id.checkBox);

        }
    }
}
