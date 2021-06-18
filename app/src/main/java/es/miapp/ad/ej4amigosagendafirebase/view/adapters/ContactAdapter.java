package es.miapp.ad.ej4amigosagendafirebase.view.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.miapp.ad.ej4amigosagendafirebase.databinding.ItemContactBinding;
import es.miapp.ad.ej4amigosagendafirebase.model.pojo.Friend;
import es.miapp.ad.ej4amigosagendafirebase.util.ScheduleThread;
import es.miapp.ad.ej4amigosagendafirebase.view.listeners.InterfaceListenerContact;
import es.miapp.ad.ej4amigosagendafirebase.viewmodel.ViewModel;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private final Context context;
    private final ContentResolver cr;
    private final InterfaceListenerContact listener;
    private final Cursor cursor;
    private final ViewModel viewModel;

    public ContactAdapter(Context context, Cursor cursor, InterfaceListenerContact listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
        cr = context.getContentResolver();
        viewModel = new ViewModelProvider((FragmentActivity) context).get(ViewModel.class);
    }

    @NotNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(ItemContactBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.init(position);
        holder.b.getRoot().setOnClickListener(v -> listener.onClickContact(holder.addAmigo(), holder.phones));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public ItemContactBinding b;
        public List<String> phones = new ArrayList<>();
        private String id;

        public ContactViewHolder(@NonNull ItemContactBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        public void init(int position) {
            cursor.moveToPosition(position);

            b.tvNameDate.setText(cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME)));
            id = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts._ID));

            Cursor cursor = cr
                    .query(ContactsContract.Contacts.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

            if (cursor == null) {
                return;
            }

            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + id,
                    null,
                    null);

            if (phones == null) {
                return;
            }

            ScheduleThread.threadExecutorPool.execute(() -> {
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(
                            phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    this.phones.add(phoneNumber.replaceAll("\\s", ""));
                    if (this.phones.size() > 1) {
                        b.tvHourPhone.setText("Hay más de un teléfono");
                    } else if (this.phones.size() == 0) {
                        b.tvHourPhone.setText("No hay números disponibles");
                    } else {
                        b.tvHourPhone.setText(phoneNumber);
                    }
                }
                phones.close();
            });
            cursor.close();
        }

        public Friend addAmigo() {
            return new Friend(Long.parseLong(id), b.tvNameDate.getText().toString(), null, 0);
        }
    }
}
