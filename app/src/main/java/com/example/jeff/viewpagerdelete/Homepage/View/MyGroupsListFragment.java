package com.example.jeff.viewpagerdelete.Homepage.View;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jeff.viewpagerdelete.Homepage.Model.HomePage_and_QuizCode_Model;
import com.example.jeff.viewpagerdelete.R;

/*
Fragment for the Groups List - - hosted in the Homepage  Activity
By: Ryan Davis
 */

public class MyGroupsListFragment extends Fragment {

    HomePage_and_QuizCode_Model groupModel;
    ArrayAdapter<String> groupAdapter;
    ListView groupListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.groups_list_fragment_layout, container, false);

        //Model model...use this to access ArrayList for Groups...add on Quiz Code Verify callback procedure

        String[] testItems = {"do something", "do something else", "do yet anothet thing"};

        groupListView = (ListView) view.findViewById(R.id.groupsList);

        groupAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, testItems);

        groupListView.setAdapter(groupAdapter);

        Log.e("test", "tracer");

        setRetainInstance(true);

        Log.e("test", "tracer");

        return view;
    }

    //Override onAttach(Context context) { } ? ... see questions from project notes ...
    //Override onDestroy() { } ? ... see questions from project notes ...
}
