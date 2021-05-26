package com.example.firechatapps.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firechatapps.Adapter.UserAdapter;
import com.example.firechatapps.Model.ChatList;
import com.example.firechatapps.Model.Users;
import com.example.firechatapps.R;
import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Users> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    RecyclerView recyclerView;

    private List<ChatList> usersList;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chats,
                container,
                false);

        recyclerView = view.findViewById(R.id.recyler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fuser.getUid());


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                // Loop for all users:
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatlist = snapshot.getValue(ChatList.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }




    private void chatList() {

        // Getting all recent chats;
       mUsers = new ArrayList<>();
       reference = FirebaseDatabase.getInstance().getReference("MyUsers");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               mUsers.clear();

               for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                   Users user = snapshot.getValue(Users.class);
                   for (ChatList chatlist : usersList){

                       if(user.getId().equals(chatlist.getId())){
                           mUsers.add(user);
                       }

                   }
               }

               userAdapter = new UserAdapter(getContext(), mUsers, true);
               recyclerView.setAdapter(userAdapter);


           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }

}