package com.example.jeff.viewpagerdelete.Startup.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.R;

/**
 * Created by jamy on 3/22/17.
 */

public class NewUserFragment extends Fragment {

    private Button saveButton;
    private SaveUserClickListener mSaveUserClickListener;
    private String newUid;
    private String newFirstName;
    private String newLastName;


    public interface SaveUserClickListener{
        public void onSaveUserClick(String uid, String fName, String lName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_new_user_fragment, container, false);

        final EditText uidField = (EditText) view.findViewById(R.id.newUserUserName);
        final EditText firstNameField = (EditText) view.findViewById(R.id.newUserFirstName);
        final EditText lastNameField = (EditText) view.findViewById(R.id.newUserLastName);
        saveButton = (Button) view.findViewById(R.id.newUserSaveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newUid = uidField.getText().toString();
                newFirstName = firstNameField.getText().toString();
                newLastName = lastNameField.getText().toString();

                if (newFirstName.isEmpty() || newLastName.isEmpty() || newUid.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity(), "A valid user ID and Screen Name are required.", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    mSaveUserClickListener.onSaveUserClick(newUid, newFirstName, newLastName);
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity) {
            a = (Activity) context;
            mSaveUserClickListener = (SaveUserClickListener) a;
        }
    }


}

