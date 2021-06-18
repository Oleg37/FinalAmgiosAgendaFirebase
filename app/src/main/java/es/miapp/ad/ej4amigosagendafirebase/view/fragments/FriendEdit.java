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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.GregorianCalendar;

import es.miapp.ad.ej4amigosagendafirebase.databinding.FragmentFriendEditBinding;
import es.miapp.ad.ej4amigosagendafirebase.model.pojo.Friend;
import es.miapp.ad.ej4amigosagendafirebase.viewmodel.ViewModel;

public class FriendEdit extends Fragment implements CalendarView.OnDateChangeListener {

    private FragmentFriendEditBinding b;
    private ViewModel viewModel;
    private Friend friend;
    private long date = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentFriendEditBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        friend = viewModel.getFriend();

        init();
    }

    private void init() {
        if (friend.getDateOfBirth() != 0) {
            b.cvDOFBirth.setDate(friend.getDateOfBirth());
            date = friend.getDateOfBirth();
        }

        b.etName.setText(friend.getName());
        b.etPhone.setText(friend.getPhNumber());
        b.cvDOFBirth.setOnDateChangeListener(this);

        b.btSave.setOnClickListener(v -> verificarDatos());
    }

    private void verificarDatos() {
        b.tiName.setErrorEnabled(false);
        b.tiPhone.setErrorEnabled(false);
        boolean validado = true;

        if (b.etName.getText().toString().isEmpty()) {
            b.etName.setError("No deje el campo vacío");
            validado = false;
        } else if (b.etPhone.getText().toString().isEmpty()) {
            b.tiPhone.setError("No deje el campo vacío");
            validado = false;
        }
        if (validado) {
            friend.setName(b.etName.getText().toString());
            friend.setPhNumber(b.etPhone.getText().toString());
            friend.setDateOfBirth(date);

            viewModel.updateFriend(friend);
            NavHostFragment.findNavController(FriendEdit.this).popBackStack();
        }
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(year, month, dayOfMonth);
        date = calendar.getTime().getTime();
    }
}