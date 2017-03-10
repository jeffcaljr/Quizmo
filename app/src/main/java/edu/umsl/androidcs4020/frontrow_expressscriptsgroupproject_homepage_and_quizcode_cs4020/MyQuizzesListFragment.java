package edu.umsl.androidcs4020.frontrow_expressscriptsgroupproject_homepage_and_quizcode_cs4020;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/*
Fragment for the Quizzes List - - hosted in the Homepage  Activity
By: Ryan Davis
 */

public class MyQuizzesListFragment extends Fragment {

    HomePage_and_QuizCode_Model groupModel;
    ArrayAdapter<String> quizAdapter;
    ListView quizListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.quizzes_list_fragment_layout, container, false);

        //test data source
        String[] testItems = {"USA", "Denmark", "South Africa"};

        quizListView = (ListView) view.findViewById(R.id.quizzesList);

        quizAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, testItems);

        quizListView.setAdapter(quizAdapter);

        //load quizzes from sqlite

        ArrayList<Quiz> quizzes = new ArrayList<>();

        quizzes.add(loadedQuizzes)
                ...
        ...
        ...

        quizListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(this, QuizLoaderActivity.class);
                    i.putExtra("quiz", quizzes.get(position));
                    i.startActivity();
            }
        });

//        quizListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                if (position == 0) {
//                    Toast.makeText(getActivity(), "You clicked the 1st item!", Toast.LENGTH_SHORT).show();
//                } else if (position == 1) {
//                    Toast.makeText(getActivity(), "You clicked the 2nd item!", Toast.LENGTH_SHORT).show();
//                } else if (position == 2) {
//                    Toast.makeText(getActivity(), "You clicked the 3rd item!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });

        Log.e("test", "tracer");

        setRetainInstance(true);

        Log.e("test", "tracer");

        return view;
    }


    //Override onAttach(Context context) { } ? ... see questions from project notes ...

    //Override onDestroy() { } ? ... see questions from project notes ...
}
