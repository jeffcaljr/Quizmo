package com.example.jeff.viewpagerdelete.Homepage.Database;

import android.content.Context;
import android.os.AsyncTask;

import com.example.jeff.viewpagerdelete.Homepage.Model.Course;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Database.IndividualQuizPersistence;
import com.example.jeff.viewpagerdelete.IndividualQuiz.Model.Quiz;
import com.example.jeff.viewpagerdelete.Startup.Model.UserDataSource;

/**
 * Created by Jeff on 4/16/17.
 */

public class QuizLoadTask extends AsyncTask<Void, Void, Quiz> {

    private Context context;
    private Course course;

    public QuizLoadTaskListener listener = null;

    public QuizLoadTask(Context context, Course course) {
        this.context = context.getApplicationContext();
        this.course = course;
    }

    @Override
    protected Quiz doInBackground(Void... voids) {
        return IndividualQuizPersistence.sharedInstance(context)
            .readIndividualQuizFromDatabase(course.getQuiz().getId().trim(),
                UserDataSource.getInstance().getUser().getUserID());
    }

    //    @Override
//    protected Quiz doInBackground() {
//        return IndividualQuizPersistence.sharedInstance(context).readIndividualQuizFromDatabase(courses.getQuiz().getId().trim());
//    }

    @Override
    protected void onPostExecute(Quiz quiz) {
        super.onPostExecute(quiz);
        listener.onQuizLoadResponse(this.course, quiz);

    }

    public interface QuizLoadTaskListener{
        void onQuizLoadResponse(Course course, Quiz quiz);
    }
}
