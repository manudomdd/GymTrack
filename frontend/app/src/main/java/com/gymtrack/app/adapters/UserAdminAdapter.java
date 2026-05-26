package com.gymtrack.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gymtrack.app.R;

import java.util.List;
import java.util.Map;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.UserViewHolder> {

    private final List<Map<String, Object>> userList;
    private final OnUserDeleteListener deleteListener;

    public interface OnUserDeleteListener {
        void onDeleteClick(Map<String, Object> user);
    }

    public UserAdminAdapter(List<Map<String, Object>> userList, OnUserDeleteListener deleteListener) {
        this.userList = userList;
        this.deleteListener = deleteListener;
    }

    public void updateList(List<Map<String, Object>> newList) {
        this.userList.clear();
        this.userList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);
        
        holder.tvName.setText((String) user.get("nombre"));
        holder.tvEmail.setText((String) user.get("username"));
        holder.tvType.setText((String) user.get("tipoUsuario"));

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvType;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_admin_user_name);
            tvEmail = itemView.findViewById(R.id.tv_admin_user_email);
            tvType = itemView.findViewById(R.id.tv_admin_user_type);
            btnDelete = itemView.findViewById(R.id.btn_admin_delete_user);
        }
    }
}
