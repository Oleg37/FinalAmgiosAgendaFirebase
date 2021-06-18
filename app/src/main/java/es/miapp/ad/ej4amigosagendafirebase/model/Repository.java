package es.miapp.ad.ej4amigosagendafirebase.model;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import es.miapp.ad.ej4amigosagendafirebase.model.pojo.Friend;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Repository {

    private Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private Friend friend;

    private FirebaseUser currentUser;

    private MutableLiveData<List<Friend>> listMutableFriends = new MutableLiveData<>();

    public Repository(Context context) {
        this.context = context;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Friend -> Insert - Update - Delete
    ///////////////////////////////////////////////////////////////////////////

    public void insertFriend(Friend friend) {
        Log.v("XYZ", friend.toString());
        db.collection("friend")
                .document(String.valueOf(friend.getId()))
                .set(friend)
                .addOnSuccessListener(command -> {
                    Log.v("XYZ", "Agregado con éxito");
                })
                .addOnFailureListener(e -> Log.v("XYZ", e.getMessage()));
    }

    public void updateFriend(Friend friend) {
        db.collection("friend")
                .document(String.valueOf(friend.getId())).set(friend)
                .addOnSuccessListener(command -> Log.v("XYZ", "Actualizado con éxito"))
                .addOnFailureListener(e -> Log.v("XYZ", e.getMessage()));
    }

    public void deleteFriend(Friend friend) {
        db.collection("friend")
                .document(String.valueOf(friend.getId())).delete()
                .addOnSuccessListener(command -> {
                    Log.v("XYZ", "Eliminado con éxito");
                    getAllFriends();
                })
                .addOnFailureListener(e -> Log.v("XYZ", e.getMessage()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // List Operations
    ///////////////////////////////////////////////////////////////////////////

    public void getAllFriends() {
        db.collection("friend")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.v("XYZ", String.valueOf(task.getException()));
                        return;
                    }

                    listMutableFriends.setValue(Objects.requireNonNull(task.getResult()).toObjects(Friend.class));
                });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extra Operations
    ///////////////////////////////////////////////////////////////////////////

    public void deleteAll() {
        for (int i = 0; i < 100; i++) {
            db.collection("friend")
                    .document(String.valueOf(i)).delete()
                    .addOnSuccessListener(command -> {
                        Log.v("XYZ", "Eliminado con éxito");
                        getAllFriends();
                    })
                    .addOnFailureListener(e -> Log.v("XYZ", e.getMessage()));
        }
    }
}
