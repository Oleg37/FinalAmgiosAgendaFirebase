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

package es.miapp.ad.ej4amigosagendafirebase.view.fragments;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import es.miapp.ad.ej4amigosagendafirebase.R;
import es.miapp.ad.ej4amigosagendafirebase.databinding.FragmentImportContactsBinding;
import es.miapp.ad.ej4amigosagendafirebase.model.pojo.Friend;
import es.miapp.ad.ej4amigosagendafirebase.util.Permissions;
import es.miapp.ad.ej4amigosagendafirebase.view.adapters.ContactAdapter;
import es.miapp.ad.ej4amigosagendafirebase.view.listeners.InterfaceListenerContact;
import es.miapp.ad.ej4amigosagendafirebase.viewmodel.ViewModel;

public class ImportContacts extends Fragment implements InterfaceListenerContact {

    private FragmentImportContactsBinding b;
    private ViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentImportContactsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        Permissions permissions = new Permissions(requireContext(), requireActivity(), b);

        if (permissions.hasAllPerms(permissions.getPERMISSIONS())) {
            permissions.permissionsApp();
            b.tvNoContacts.setText("Se necesitan permisos adiccionales");
        } else {
            b.tvNoContacts.setText("❗No tienes nigún contacto todavía❗");
            init();
        }
    }

    public void init() {
        ContentResolver cr = requireActivity().getContentResolver();

        String[] getInfoContacto = new String[]{
                ContactsContract.Data._ID,
                ContactsContract.Data.DISPLAY_NAME
        };

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, getInfoContacto, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {

            b.tvNoContacts.setVisibility(View.GONE);

            ContactAdapter adapter = new ContactAdapter(requireActivity(), cur, this);

            b.rvContactList.setAdapter(adapter);
            b.rvContactList.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @Override
    public void onClickContact(Friend friend, List<String> phNumber) {
        if (phNumber.size() == 1) {
            friend.setPhNumber(phNumber.get(0));
            viewModel.insertFriend(friend);
            NavHostFragment.findNavController(ImportContacts.this).navigate(R.id.action_nav_importFragment_to_nav_listFragment);
        } else if (phNumber.isEmpty()) {
            requireActivity().runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("No existe un número")
                        .setMessage("Este contacto no tiene ningún número de teléfono asignado.")
                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                        .show();
            });

        } else {
            requireActivity().runOnUiThread(() -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Auto selección de número")
                        .setMessage("Se va a seleccionar el primer número disponible.")
                        .setPositiveButton("Aceptar", (dialog, which) -> {

                            friend.setPhNumber(phNumber.get(0));
                            viewModel.insertFriend(friend);
                            NavHostFragment.findNavController(ImportContacts.this).navigate(R.id.action_nav_importFragment_to_nav_listFragment);
                        })
                        .show();
            });
        }
    }
}