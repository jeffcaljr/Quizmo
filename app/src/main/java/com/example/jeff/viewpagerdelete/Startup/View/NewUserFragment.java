package com.example.jeff.viewpagerdelete.Startup.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeff.viewpagerdelete.R;

import static com.example.jeff.viewpagerdelete.R.id.view;

/**
 * Created by jamy on 3/22/17.
 */

public class NewUserFragment extends Fragment {

//    private Button saveButton;
//    private SaveUserClickListener mSaveUserClickListener;
//    private String newUid;
//    private String newFirstName;
//    private String newLastName;
//    private EditText uidField;
//    private EditText firstNameField;
//    private EditText lastNameField;
//
//    public interface SaveUserClickListener{
//        public void onSaveUserClick(String uid, String fName, String lName);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.start_new_user_fragment, container, false);
//
//        uidField = (EditText) view.findViewById(R.id.newUserUID);
//        firstNameField = (EditText) view.findViewById(R.id.newUserFirstName);
//        lastNameField = (EditText) view.findViewById(R.id.newUserLastName);
//        saveButton = (Button) view.findViewById(R.id.newUserSaveButton);
//
//
//
//        lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//
//                if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) {
//                    if (keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                        newUid = uidField.getText().toString();
//                        newFirstName = firstNameField.getText().toString();
//                        newLastName = lastNameField.getText().toString();
//                        if (newFirstName.isEmpty() || newLastName.isEmpty() || newUid.isEmpty()) {
//                            Toast toast = Toast.makeText(getActivity(), "A valid user ID and Screen Name are required.", Toast.LENGTH_LONG);
//                            toast.show();
//                        } else {
//                            mSaveUserClickListener.onSaveUserClick(newUid, newFirstName, newLastName);
//                            return true;
//                        }
//                    }
//                }
//                return false;
//            }
//        });
//        TextView.OnEditorActionListener nextField = new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                boolean handled = false;
//                if(actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_NEXT){
//                    if (keyEvent == null || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                        int[] textFields = new int[]{R.id.newUserUID, R.id.newUserFirstName, R.id.newUserLastName};
//                        for (int i = 0; i < textFields.length - 1; i++) {
//                            if (textFields[i] == textView.getId()) {
//                                view.findViewById(textFields[i + 1]).requestFocus();
//                                handled = true;
//                            }
//                        }
//                    }
//                }
//                return handled;
//            }
//        };
//        firstNameField.setOnEditorActionListener(nextField);
//        uidField.setOnEditorActionListener(nextField);
//
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                newUid = uidField.getText().toString().replaceAll("\\s+","");
//                newFirstName = firstNameField.getText().toString().replaceAll("\\s+","");
//                newLastName = lastNameField.getText().toString().replaceAll("\\s+","");
//
//                if (newFirstName.isEmpty() || newLastName.isEmpty() || newUid.isEmpty()) {
//                    Toast toast = Toast.makeText(getActivity(), "A valid user ID and Screen Name are required.", Toast.LENGTH_LONG);
//                    toast.show();
//                }
//                else{
//                    mSaveUserClickListener.onSaveUserClick(newUid, newFirstName, newLastName);
//                }
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        Activity a;
//
//        if (context instanceof Activity) {
//            a = (Activity) context;
//            mSaveUserClickListener = (SaveUserClickListener) a;
//        }
//    }



}

