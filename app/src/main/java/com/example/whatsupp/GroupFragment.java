package com.example.whatsupp;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private View GroupFragmentView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_group=new ArrayList<>();
    private DatabaseReference GroupRef;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     GroupFragmentView= inflater.inflate(R.layout.fragment_group, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

     intializeFields();

     RetriveAndDisplayGroups();

     list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             String currentGroupName= parent.getItemAtPosition(position).toString();

             Intent groupChatIntent=new Intent(getContext(), GroupChattActivity.class);
             groupChatIntent.putExtra("groupName",currentGroupName);
             startActivity(groupChatIntent);

         }
     });

        return GroupFragmentView;

    }



    private void intializeFields() {


        list_view=(ListView)GroupFragmentView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list_of_group);
        list_view.setAdapter(arrayAdapter);

    }
    private void RetriveAndDisplayGroups() {


        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                Set<String> sett=new HashSet<>();

                Iterator iterator=dataSnapshot.getChildren().iterator();

                while (iterator.hasNext())

                {
                    sett.add(((DataSnapshot)iterator.next()).getKey());

                }

                list_of_group.clear();
                list_of_group.addAll(sett);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
