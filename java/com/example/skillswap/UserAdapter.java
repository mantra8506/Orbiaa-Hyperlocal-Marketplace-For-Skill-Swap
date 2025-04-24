package com.example.skillswap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Handle null values safely
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String fullName = firstName + " " + lastName;

        holder.userName.setText(fullName.trim().isEmpty() ? "Unknown User" : fullName);
        holder.userEmail.setText("Email: " + user.getEmail());
        holder.userSkill.setText("Skill: " + (user.getSkill() != null ? user.getSkill() : "Not specified"));
        holder.userExperience.setText("Experience: " + (user.getExperience() != null ? user.getExperience() : "Not specified"));
        holder.userLocation.setText("Location: " + (user.getLocation() != null ? user.getLocation() : "Not specified"));

        holder.itemView.setOnClickListener(v -> {
            Log.d("UserAdapter", "Opening profile for: " + user.getEmail());
            Intent intent = new Intent(context, UserProfile.class);
            intent.putExtra("userEmail", user.getEmail());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userEmail, userSkill, userExperience, userLocation;  // Corrected variable names

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            userSkill = itemView.findViewById(R.id.userSkill);
            userExperience = itemView.findViewById(R.id.userExperience);
            userLocation = itemView.findViewById(R.id.userLocation);
        }
    }
}
