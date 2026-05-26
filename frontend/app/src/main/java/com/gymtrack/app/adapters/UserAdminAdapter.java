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

/**
 * Adaptador del RecyclerView que muestra el listado de usuarios en el panel
 * de administración.
 * <p>
 * Cada elemento de la lista presenta el nombre, el username y el rol del usuario,
 * junto con un botón de eliminación que delega la acción en la actividad
 * contenedora a través de la interfaz {@link OnUserDeleteListener}.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.UserViewHolder> {

    private final List<Map<String, Object>> userList;
    private final OnUserDeleteListener deleteListener;

    /**
     * Interfaz de escucha para la acción de eliminación de usuarios.
     * La actividad que instancie este adaptador debe implementarla para
     * recibir el mapa de datos del usuario seleccionado.
     */
    public interface OnUserDeleteListener {
        /**
         * Se invoca cuando el usuario pulsa el botón de eliminar en un elemento.
         *
         * @param user mapa con los datos del usuario a eliminar
         */
        void onDeleteClick(Map<String, Object> user);
    }

    /**
     * Crea una nueva instancia del adaptador.
     *
     * @param userList       lista mutable de mapas con los datos de cada usuario
     * @param deleteListener listener que gestiona la acción de borrado
     */
    public UserAdminAdapter(List<Map<String, Object>> userList, OnUserDeleteListener deleteListener) {
        this.userList = userList;
        this.deleteListener = deleteListener;
    }

    /**
     * Reemplaza el contenido del adaptador con una nueva lista de usuarios
     * y notifica al RecyclerView para que actualice la vista.
     *
     * @param newList nueva lista de usuarios a mostrar
     */
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

    /**
     * Enlaza los datos del usuario en la posición indicada con las vistas del ViewHolder.
     *
     * @param holder   ViewHolder que contiene las vistas del elemento
     * @param position posición del elemento en la lista
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Map<String, Object> user = userList.get(position);

        holder.tvName.setText((String) user.get("nombre"));
        holder.tvEmail.setText((String) user.get("username"));
        holder.tvType.setText((String) user.get("tipoUsuario"));

        // Propagar el evento de borrado al listener con los datos del usuario seleccionado.
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

    /**
     * ViewHolder que mantiene referencias a las vistas de un elemento de la lista.
     */
    static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvEmail;
        TextView tvType;
        Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tv_admin_user_name);
            tvEmail   = itemView.findViewById(R.id.tv_admin_user_email);
            tvType    = itemView.findViewById(R.id.tv_admin_user_type);
            btnDelete = itemView.findViewById(R.id.btn_admin_delete_user);
        }
    }
}
