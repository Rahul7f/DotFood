package com.rsin.dotfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rsin.dotfood.DataModel;
import com.rsin.dotfood.R;
import com.rsin.dotfood.ViewDetailsActivity;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NeedyAdapter extends RecyclerView.Adapter<NeedyAdapter.ViewHolder> {
    Context context;
    ArrayList<DataModel> data_list;

    public NeedyAdapter(Context context, ArrayList<DataModel> data_list) {
        this.context = context;
        this.data_list = data_list;
    }

    @NonNull
    @Override
    public NeedyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.rv_needy_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull NeedyAdapter.ViewHolder holder, int position) {
        holder.address.setText(data_list.get(position).getAddress());
        holder.title.setText(data_list.get(position).getTitle());
       holder.des.setText(data_list.get(position).getDescription());
        Glide.with(context).load(data_list.get(position).getAll_photos().get(0)).into(holder.imageView);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewDetailsActivity.class);
                intent.putExtra("object",data_list.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView title,address,des;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.help_card);
            imageView = itemView.findViewById(R.id.image_h);
            title = itemView.findViewById(R.id.title_h);
            des = itemView.findViewById(R.id.description_helpers);
            address = itemView.findViewById(R.id.address_h);
        }
    }
}
