package com.example.whatsupp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


public class GroupChattActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMeassageButton;
    private EditText userMeassageinput;
    private ScrollView mScrollView;
    private TextView displayTextMessage;
    private  String currentGroupName,currentUserId,currentUserName,currentDate,currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GroupNameRef,GroupMessageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatt);

        currentGroupName=getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChattActivity.this,currentGroupName, Toast.LENGTH_SHORT).show();


        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
           GroupNameRef=FirebaseDatabase.getInstance().getReference().child("Group").child(currentGroupName);
           mAuth=FirebaseAuth.getInstance();
          currentUserId=mAuth.getCurrentUser().getUid();




        InitializField();

        getUserInfo();

        sendMeassageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveMessageInfoToDatabase();
                userMeassageinput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded( DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged( DataSnapshot dataSnapshot,  String s) {
                if(dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved( DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved( DataSnapshot dataSnapshot,  String s) {

            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }



    private void InitializField() {
        mToolbar=(Toolbar)findViewById(R.id.group_chatt_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMeassageButton=(ImageButton)findViewById(R.id.send_message_button);
        userMeassageinput=(EditText)findViewById(R.id.input_group_meassage);
        displayTextMessage=(TextView)findViewById(R.id.group_chatt_text_display);
        mScrollView=(ScrollView)findViewById(R.id.my_scroll_view);



    }
    private void getUserInfo() {

        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    private void SaveMessageInfoToDatabase() {
        String messageKey=GroupNameRef.push().getKey();

        String message =userMeassageinput.getText().toString();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write message first....", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd,yyyy");
            currentDate=currentDateFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm:ss a");
            currentTime=currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object> groupMessageKey= new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);
            GroupMessageKeyRef=GroupNameRef.child(messageKey);

            HashMap<String ,Object> messageInfoMap=new HashMap<>();
            messageInfoMap.put("Name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            GroupMessageKeyRef.updateChildren(messageInfoMap);

        }



    }
    private void DisplayMessages(DataSnapshot dataSnapshot) {

        Iterator iterator=dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();
            displayTextMessage.append(chatName +":\n "+chatMessage +"\n"+chatTime +"  " +chatDate+ "\n\n\n");



        }
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }



}
