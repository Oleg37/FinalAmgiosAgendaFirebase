/*
 * Copyright (c) 2021. ArseneLupin0.
 *
 * Licensed under the GNU General Public License v3.0
 *
 * https://www.gnu.org/licenses/gpl-3.0.html
 *
 * Permissions of this strong copyleft license are conditioned on making available complete source
 * code of licensed works and modifications, which include larger works using a licensed work,
 * under the same license. Copyright and license notices must be preserved. Contributors provide
 * an express grant of patent rights.
 */

package es.miapp.ad.ej4amigosagendafirebase.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Permissions {

    /**
     * Pedimos todos nuestros permisos.
     */
    private final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS
    };

    private final int PERMISSION_ALL = 1;
    private Context context;
    private Activity activity;
    private ViewBinding b;

    public Permissions(Context context, Activity activity, ViewBinding b) {
        this.context = context;
        this.activity = activity;
        this.b = b;
    }

    /**
     * Método para ver los permisos en forma de bucle (Método, más corto existente), noi hace falta
     * uso de onRequestPermissionResult. Solo se pedirán los permisos que están en el archivo de
     * manifiesto y son completamente requeridos por nuestra aplicación.
     *
     * @param permissions Permisos pasados por un array propio de permisos.
     *
     * @return Los permisos garantizados.
     */
    public boolean hasAllPerms(String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Método muy parecido a {@link #hasAllPerms(String...)} solo que en este contamos los
     * permisos que son denegados por el usuario o no los tiene activados aún.
     *
     * @param permissions Permisos pasados por un array propio de permisos.
     *
     * @return Número de permisos denegados.
     */
    public int permissionCount(String... permissions) {
        int permissionCount = 0;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                permissionCount++;
            }
        }
        return permissionCount;
    }

    /**
     * Método para explicar los permisos necesarios para ejecutar la aplicación 1 a 1.
     *
     * @param permissions Permisos pasados por un array propio de permisos.
     */
    private void explainPermission(String... permissions) {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setTitle("Se necesitan " + permissionCount(permissions) + " permisos...");

        StringBuilder perms = new StringBuilder();
        for (String permission : permissions) {
            if (!Manifest.permission.READ_CONTACTS.equals(permission)) {
                break;
            }

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                perms.append("- Permiso para leer los contactos\n");
            }
        }
        builder.setMessage("Se necesitan los siguiente permisos para un funcionamiento correcto de la App:\n\n" + perms);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) ->
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_ALL));
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    /**
     * Método público general para pedir los permisos.
     */
    public void askPermissions() {
        if (hasAllPerms(PERMISSIONS)) {
            explainPermission(PERMISSIONS);
        }
    }

    /**
     * Método estático para el diálogo cuando no se tienen permisos.
     */
    public void permissionsApp() {
        new MaterialAlertDialogBuilder(context)
                .setTitle("Se requieren permisos...")
                .setMessage("Se necesitan una serie de permisos para poder seguir y utlizar la aplicación correctamente." +
                        " En caso contrario la aplicación procederá a cerrarse ya que no tiene los permisos suficientes.")
                .setPositiveButton("¡Perfecto!", (dialog, which) -> {
                    if (hasAllPerms(getPERMISSIONS())) {
                        askPermissions();
                    } else {
                        Snackbar.make(b.getRoot().getRootView(), "Tienes todos los permisos", Snackbar.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }).setNegativeButton("No, salir", (dialog, which) -> System.exit(0))
                .create().show();
    }
}