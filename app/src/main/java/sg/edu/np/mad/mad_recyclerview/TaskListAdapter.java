package sg.edu.np.mad.mad_recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskListAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    public Model.TaskList tl;

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
