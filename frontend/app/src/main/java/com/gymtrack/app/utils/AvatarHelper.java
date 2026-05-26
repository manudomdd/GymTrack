package com.gymtrack.app.utils;

import com.gymtrack.app.R;

/**
 * Clase utilitaria para la gestión de avatares en la aplicación.
 * <p>
 * Proporciona un mapeo seguro entre los identificadores en formato cadena de texto 
 * almacenados en la base de datos (e.g. "avatar_1") y los recursos dibujables 
 * locales de Android correspondientes. Incluye un fallback seguro hacia un icono 
 * por defecto (ic_person) en caso de discrepancias o valores nulos.
 * </p>
 *
 * @author Manuel Dominguez
 * @version 1.0
 * @since 26/05/2025
 */
public class AvatarHelper {

    public static int getAvatarResource(String avatarName) {
        if (avatarName == null) return R.drawable.ic_person; // Default fallback if needed
        switch (avatarName) {
            case "avatar_1":
                return R.drawable.avatar_1;
            case "avatar_2":
                return R.drawable.avatar_2;
            case "avatar_3":
                return R.drawable.avatar_3;
            default:
                return R.drawable.ic_person; // Fallback para cualquier otro caso
        }
    }
}
