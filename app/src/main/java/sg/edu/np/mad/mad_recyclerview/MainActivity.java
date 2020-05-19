package sg.edu.np.mad.mad_recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import sg.edu.np.mad.mad_recyclerview.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView TaskListRecyclerView;
    private EditText AddItemEditText;
    private Button AddItemButton;
    private TaskListAdapter tlAdapter;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     *
     * @param rv RecyclerView for scrolling to
     * @param taskList TaskList containing all current Tasks shown in the list
     */
    private void showNewEntry(RecyclerView rv, Model.TaskList taskList){
        //scroll to the last item of the recyclerview
        rv.scrollToPosition(taskList.countTasks() - 1);

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rv.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preload a new TaskList object
        Model.TaskList preloadedTL = new Model.TaskList();
        preloadedTL.addTaskToList("Buy milk");
        preloadedTL.addTaskToList("Send postage");
        preloadedTL.addTaskToList("Buy Android development book");

        // Link objects first
        AddItemEditText = (EditText) findViewById(R.id.addTaskEditText);
        AddItemButton = (Button) findViewById(R.id.addTaskButton);
        TaskListRecyclerView = (RecyclerView) findViewById(R.id.taskListRecyclerView);

        // Setup AddItemButton
        AddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTaskDescription = AddItemEditText.getText().toString();
                AddItemEditText.setText("");

                if (newTaskDescription.length() > 0) {
                    tlAdapter.tl.addTaskToList(newTaskDescription);
                    tlAdapter.notifyDataSetChanged();
                    showNewEntry(TaskListRecyclerView, tlAdapter.tl);
                }
            }
        });

        // Compile for RecyclerView
        tlAdapter = new TaskListAdapter(preloadedTL);
        LinearLayoutManager llManager = new LinearLayoutManager(this);

        TaskListRecyclerView.setLayoutManager(llManager);
        TaskListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        TaskListRecyclerView.setAdapter(tlAdapter);
        TaskListRecyclerView.addOnItemTouchListener(
            new RecyclerTouchListener(this, TaskListRecyclerView, new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, final int position) {
                    // Build Alert Dialog
                    String descriptionOfItemToBeDeleted = tlAdapter.tl.getTaskAtIndex(position).getTaskDescription();

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Delete");
                    builder.setMessage(Html.fromHtml(
                            "<div style='text-align: center'>Are you sure you want to delete<br /><b>" + descriptionOfItemToBeDeleted + "</b>?</div>"
                    ));
                    builder.setIcon(android.R.drawable.ic_menu_delete);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tlAdapter.tl.removeTaskAtIndexFromList(position);
                            tlAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("No", null);

                    builder.create().show();
                }
            })
        );

    }
}
