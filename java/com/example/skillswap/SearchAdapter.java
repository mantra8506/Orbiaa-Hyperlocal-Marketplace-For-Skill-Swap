package com.example.skillswap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<String> searchList;
    private OnItemClickListener listener;

    // Interface for Click Events
    public interface OnItemClickListener {
        void onItemClick(String selectedCategory);
    }

    public SearchAdapter(List<String> searchList, OnItemClickListener listener) {
        this.searchList = searchList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String searchItem = searchList.get(position);
        holder.tvCategory.setText(searchItem);

        // Pass selected item to the listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(searchItem));
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
