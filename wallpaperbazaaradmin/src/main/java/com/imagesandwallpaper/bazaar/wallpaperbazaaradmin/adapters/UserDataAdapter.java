package com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.R;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.CategoryModel;
import com.imagesandwallpaper.bazaar.wallpaperbazaaradmin.models.UserData.UserDataModel;

import java.util.ArrayList;
import java.util.List;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder> {
    List<UserDataModel> userDataModelList = new ArrayList<>();
    Context context;
    UserDataClickInterface userDataClickInterface;

    public UserDataAdapter(Context context, UserDataClickInterface userDataClickInterface) {
        this.context = context;
        this.userDataClickInterface = userDataClickInterface;
    }

    @NonNull
    @Override
    public UserDataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_data_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDataAdapter.ViewHolder holder, int position) {
        holder.userName.setText(userDataModelList.get(position).getName());
        holder.userEmail.setText(userDataModelList.get(position).getEmail());
        holder.itemView.setOnClickListener(view -> {
            userDataClickInterface.onClicked(userDataModelList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return userDataModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<UserDataModel> userDataModels){
        userDataModelList.clear();
        userDataModelList.addAll(userDataModels);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
        }
    }
}
