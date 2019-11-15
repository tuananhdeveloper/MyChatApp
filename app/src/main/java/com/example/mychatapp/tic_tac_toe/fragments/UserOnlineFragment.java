package com.example.mychatapp.tic_tac_toe.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mychatapp.Model.User;
import com.example.mychatapp.R;
import com.example.mychatapp.adapters.UserOnlineAdapter;
import com.example.mychatapp.tic_tac_toe.PlayActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserOnlineFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference reference, reference1;
    private FirebaseUser firebaseUser;
    private List<User> mList;
    private UserOnlineAdapter adapter;
    private Spinner spinner;
    private Button btnInvite;
    private String currentName;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        currentName = firebaseUser.getDisplayName();
        mList= new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_user_online, container, false);
        spinner = view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        btnInvite = view.findViewById(R.id.btn_invite);
        btnInvite.setOnClickListener(this);
        showUsersOnline();
        return view;
    }

    public void showUsersOnline(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(!firebaseUser.getUid().equals(user.getId()) && user.isOnline()){
                        mList.add(user);
                    }
                }

                adapter = new UserOnlineAdapter(getContext(), mList);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (adapter == null) return;
        if(adapter != null && adapter.getCount() == 0)  return;
        final User user = mList.get(spinner.getSelectedItemPosition());
        reference = FirebaseDatabase.getInstance().getReference().child("tic-tac-toe").child("invitation");
        Map<String, Object> map = new HashMap<>();
        map.put("idUser1", firebaseUser.getUid());
        map.put("Name", currentName);
        map.put("idUser2", user.getId());
        reference.push().updateChildren(map);

        // play
        reference1 = FirebaseDatabase.getInstance().getReference("tic-tac-toe");
        reference1.child("room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.child("idPlayer1").getValue().equals(firebaseUser.getUid())){
                        String roomId = snapshot.getKey();
                        Bundle bundle = new Bundle();
                        bundle.putString("room_id", roomId);
                        bundle.putString("nameReceiver", user.getName());
                        bundle.putInt("player", 1);

                        try{
                            Intent intent = new Intent(getActivity(), PlayActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            reference1.child("room").removeEventListener(this);
                            getActivity().finish();
                        }
                        catch (NullPointerException e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
