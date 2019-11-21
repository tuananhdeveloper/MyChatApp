package com.example.mychatapp.tic_tac_toe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatapp.R;
import com.example.mychatapp.tic_tac_toe.adapters.CustomBaseAdapter;
import com.example.mychatapp.tic_tac_toe.fragments.EndGameDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private GridView gridView;
    private int player;
    private String roomId;
    private DatabaseReference reference;
    private Map<String, Integer> listCoordinate;
    private int turn;
    private TextView turn1, turn2;
    private TextView player1, player2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        init();
    }

    public void init(){
        turn1 = findViewById(R.id.turn1);
        turn2 = findViewById(R.id.turn2);

        player1 = findViewById(R.id.player1);
        player1.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        player2 = findViewById(R.id.player2);

        gridView = findViewById(R.id.gird_view);
        final CustomBaseAdapter adapter = new CustomBaseAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        Bundle bundle = getIntent().getExtras();
        player = bundle.getInt("player");
        roomId = bundle.getString("room_id");

        listCoordinate = new HashMap<>();
        reference = FirebaseDatabase.getInstance().getReference("tic-tac-toe");
        if(player == 1){
            reference.child("room").child(roomId).child("turn").setValue(1);
            player2.setText(bundle.getString("nameReceiver"));
        }
        else{
            player2.setText(bundle.getString("nameInviter"));
        }
        reference.child("room").child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    Toast.makeText(PlayActivity.this, "The player has exited!", Toast.LENGTH_SHORT).show();
                    gridView.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        turnPlayer();
        reference.child("room").child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue() == null) return;
                if(!dataSnapshot.getKey().equals("idPlayer1") && !dataSnapshot.getKey().equals("idPlayer2") && !dataSnapshot.getKey().equals("turn")){
                    listCoordinate.put(dataSnapshot.getKey(), dataSnapshot.getValue(Integer.class));
                    if(dataSnapshot.getValue(Integer.class) != player){
                        showItem(dataSnapshot);
                        String strkey = dataSnapshot.getKey();
                        String str[] = strkey.split("-");
                        int p = dataSnapshot.getValue(Integer.class);

                        if(isWinner(p, Integer.parseInt(str[0]), Integer.parseInt(str[1]))){
                            gridView.setEnabled(false);
                            EndGameDialog dialog = new EndGameDialog(PlayActivity.this, "You lost!", "Play again?", reference, roomId, gridView);
                            dialog.setCancelable(false);
                            dialog.show(getSupportFragmentManager(), "dialog_endgame");
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String coordinate = position/8 + "-" + position%8;
        if(!listCoordinate.containsKey(coordinate)){
            if(player == 1 && turn == 1){
                ImageView img = view.findViewById(R.id.item);
                img.setImageResource(R.drawable.ic_x);
                Map<String, Object> map = new HashMap<>();
                map.put(coordinate, 1);
                reference.child("room").child(roomId).updateChildren(map);

                reference.child("room").child(roomId).child("turn").setValue(2);
            }
            else if(player == 2 && turn == 2){
                ImageView img = view.findViewById(R.id.item);
                img.setImageResource(R.drawable.ic_o);

                Map<String, Object> map = new HashMap<>();
                map.put(coordinate, 2);
                reference.child("room").child(roomId).updateChildren(map);

                reference.child("room").child(roomId).child("turn").setValue(1);
            }
            if(isWinner(player, position/8, position%8)){

                gridView.setEnabled(false);
                EndGameDialog dialog = new EndGameDialog(this, "You win!", "Play again?", reference, roomId, gridView);
                dialog.setCancelable(false);
                dialog.show(getSupportFragmentManager(), "dialog_endgame");
            }
        }

    }

    public void turnPlayer(){
        reference.child("room").child(roomId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("turn")){
                    turn = dataSnapshot.getValue(Integer.class);
                    if(turn == player){
                        turn1.setVisibility(View.VISIBLE);
                        turn2.setVisibility(View.INVISIBLE);
                    }
                    else{
                        turn2.setVisibility(View.VISIBLE);
                        turn1.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("turn")){
                    turn = dataSnapshot.getValue(Integer.class);
                    if(turn == player){
                        turn1.setVisibility(View.VISIBLE);
                        turn2.setVisibility(View.INVISIBLE);
                    }
                    else{
                        turn2.setVisibility(View.VISIBLE);
                        turn1.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showItem(DataSnapshot dataSnapshot){
        String str = dataSnapshot.getKey();
        String[] tmp = str.split("-");
        int num1 = Integer.parseInt(tmp[0]);
        int num2 = Integer.parseInt(tmp[1]);
        int position = num1*8 + num2;

        View view = gridView.getChildAt(position);
        if(view == null) return;
        ImageView imgView = view.findViewById(R.id.item);
        if(player == 1){
            imgView.setImageResource(R.drawable.ic_o);
        }
        else if(player == 2){
            imgView.setImageResource(R.drawable.ic_x);
        }
    }

    public boolean isWinner(int player, int x, int y){
        int x1 = x;
        int y1 = y;
        int num = 1;
        //row
        x1--;
        while(x1 >= 0 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            x1--;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1 + 6, y1)){
                    return true;
                }
                return false;
            }
        }

        x1 = x; y1 = y;
        x1++;
        while(x1 <= 7 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            x1++;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1 - 6, y1)){
                    return true;
                }
                return false;
            }
        }

        //column
        x1 = x; y1 = y;
        num = 1;
        y1--;
        while(y1 >= 0 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            y1--;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1, y1 + 6)){
                    return true;
                }
                return false;
            }
        }

        x1 = x; y1 = y;
        y1++;
        while(y1 <= 7 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            y1++;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1, y1 - 6)){
                    return true;
                }
                return false;
            }
        }

        //line 1
        x1 = x; y1 = y;
        num = 1;
        x1--;
        y1--;
        while(x1 >= 0 && y1 >= 0 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            x1--;
            y1--;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1 + 6, y1 + 6)){
                    return true;
                }
                return false;
            }
        }

        x1 = x; y1 = y;
        x1++;
        y1++;
        while(x1 <= 7 && y1 <= 7 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            x1++;
            y1++;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1 - 6, y1 - 6)){
                    return true;
                }
                return false;
            }
        }

        //line 2
        x1 = x; y1 = y;
        num = 1;
        x1++;
        y1--;
        while(x1 <= 7 && y1 >= 0 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            x1++;
            y1--;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1 - 6, y1 + 6)){
                    return true;
                }
                return false;
            }
        }

        x1 = x; y1 = y;
        x1--;
        y1++;
        while(x1 >= 0 && y1 <= 7 && listCoordinate.containsKey(x1 + "-" + y1)){
            if(listCoordinate.get(x1 + "-" + y1) != player) break;
            num++;
            x1--;
            y1++;
            if(num == 5){
                if(!isBlocked(player, x1, y1, x1 + 6, y1 - 6)){
                    return true;
                }
                return false;
            }
        }

        return false;
    }

    public boolean isBlocked(int player, int x1, int y1, int x2, int y2){

        if(x1 >= 0 && x1 <= 7 && y1 >= 0 && y1 <= 7 && x2 >= 0 && x2 <= 7 && y2 >= 0 && y2 <= 7){
            if(listCoordinate.containsKey(x1 + "-" + y1) && listCoordinate.containsKey(x2 + "-" + y2)){
                if(listCoordinate.get(x1 + "-" + y1) != player && listCoordinate.get(x2 + "-" + y2) != player){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reference.child("room").child(roomId).removeValue();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit game?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }
}
