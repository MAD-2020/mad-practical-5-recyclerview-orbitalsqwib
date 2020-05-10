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

    private class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView TaskDescriptionTextView;
        CheckBox TaskCompleteCheckBox;
        ConstraintLayout TaskItemConstraintLayout;

        public TaskViewHolder(View itemView) {
            super(itemView);
            this.TaskDescriptionTextView = (TextView) itemView.findViewById(R.id.taskDescription);
            this.TaskCompleteCheckBox = (CheckBox) itemView.findViewById(R.id.taskCheckBox);
            this.TaskItemConstraintLayout = (ConstraintLayout) itemView.findViewById(R.id.taskListItemLayout);
        }
    }

    public class TaskListAdapter extends RecyclerView.Adapter<TaskViewHolder> {
        private Model.TaskList tl;

        public TaskListAdapter(Model.TaskList taskList) {
            this.tl = taskList;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            TaskViewHolder viewHolder;

            View item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.tasklist_item,
                    parent,
                    false
            );

            viewHolder = new TaskViewHolder(item);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Model.Task t = tl.getTaskAtIndex(position);
            holder.TaskDescriptionTextView.setText(t.getTaskDescription());
            holder.TaskCompleteCheckBox.setChecked(t.getTaskCompleted());
        }

        @Override
        public int getItemCount() {
            return tl.countTasks();
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private RecyclerViewClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerViewClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }

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
