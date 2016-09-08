package com.dwolford.workouttracker;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//This is a test
public class AbWorkout extends Activity {

    Button back;
    ListView workoutExerciseList;
    WorkoutTracker tracker;
    AbViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ab_workout);


        /**
         * The database used to pull all exercises for this workout routine
         */
        final DatabaseHandler db = new DatabaseHandler(this);
        final SQLiteDatabase database = db.getReadableDatabase();

        final Context context = this;

        /**
         * Get all exercises from Triceps and Chest workout and put in arraylist
         */
        workoutExerciseList = (ListView)findViewById(R.id.listView10);
        final List<String> arrayWorkoutExercises = new ArrayList<String>();
        final ArrayAdapter<String> exListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, arrayWorkoutExercises);
        workoutExerciseList.setAdapter(exListAdapter);


                //Holds all the available exercises
        final List<WorkoutTracker> exerciseList = db.getRepetitionWorkoutRoutine(context, database, "Abs");

        /**
         * Populates the listView with each exercise for this workout routine. This includes the
         * each exercise name, the number of repetitions, the weight of the exercise, and any
         * comments included.
         */
        List<AbWorkout.RepListViewItem> repProgressList = new ArrayList<>();
        for(int i = 0; i<exerciseList.size(); i++) {
            final int j = i;
            repProgressList.add(new AbWorkout.RepListViewItem()
            {{
                    REPETITIONS = exerciseList.get(j).getReps();
                    WEIGHT = exerciseList.get(j).getWeight();
                    COMMENT = exerciseList.get(j).getComment();
                    EXERCISE_NAME = exerciseList.get(j).getExerciseName();
                }});


        }
        adapter = new AbViewAdapter(this, repProgressList);
        workoutExerciseList.setAdapter(adapter);


        final CheckBox checkBox = (CheckBox)findViewById(R.id.CheckBox);

        workoutExerciseList.setItemChecked(ListView.CHOICE_MODE_MULTIPLE, true);

        /*
            Upon selection of an exercise, it is highlighted and its name, date,
            and number of reps is stored in repetition progress table
        */
        workoutExerciseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {

                CheckBox check = (CheckBox) findViewById(R.id.CheckBox);
                //check.setChecked(true);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
                //Get today's date formatted like this: dd-MM-yy
                String exDate = format.format(calendar.getTime());


                //When exercise is selected, change the color and make it unselectable.
                myAdapter.getChildAt(myItemInt).setBackgroundColor(Color.MAGENTA);
                myAdapter.getChildAt(myItemInt).setEnabled(false);
                myAdapter.getChildAt(myItemInt).setClickable(false);
                myView.setClickable(true);


                tracker = exerciseList.get(myItemInt);
                db.addNewRepProgress(new WorkoutTracker(tracker.getExerciseName(), tracker.getReps(), tracker.getWeight(), tracker.getComment(), exDate), context, database);
                Toast.makeText(context.getApplicationContext(), "Workout Progress was successfully saved.", Toast.LENGTH_LONG).show();

            }
        });


        back = (Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ab_workout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class RepListViewItem
    {
        public int REPETITIONS;
        public double WEIGHT;
        public String COMMENT;
        public String EXERCISE_NAME;
        public String DATE;
    }
}
