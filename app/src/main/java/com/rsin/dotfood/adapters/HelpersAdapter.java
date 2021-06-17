package com.rsin.dotfood.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rsin.dotfood.DataModel;
import com.rsin.dotfood.R;
import com.rsin.dotfood.ViewDetailsActivity;

import java.util.ArrayList;

public class HelpersAdapter extends RecyclerView.Adapter<HelpersAdapter.ViewHolder> {
    Context context;
    ArrayList<DataModel> data_list;

    public HelpersAdapter(Context context, ArrayList<DataModel> data_list) {
        this.context = context;
        this.data_list = data_list;
    }

    @NonNull
    @Override
    public HelpersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.rv_helpers_layout, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpersAdapter.ViewHolder holder, int position) {
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
            cardView = itemView.findViewById(R.id.wcard);
            imageView = itemView.findViewById(R.id.wimage);
            title = itemView.findViewById(R.id.wtitle);
            address = itemView.findViewById(R.id.waddress);
            des = itemView.findViewById(R.id.description_);
        }
    }
}
