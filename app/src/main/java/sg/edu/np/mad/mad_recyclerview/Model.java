package sg.edu.np.mad.mad_recyclerview;

import java.util.ArrayList;
import java.util.List;

public class Model {

    public static class Task {

        private String taskDescription;
        private Boolean taskCompleted;

        public Task(String description) {
            this.taskDescription = description;
            this.taskCompleted = false;
        }

        public void markAsDone() {
            this.taskCompleted = true;
        }

        public String getTaskDescription() { return this.taskDescription; }

        public Boolean getTaskCompleted() { return this.taskCompleted; }
    }

    public static class TaskList {

        private List<Task> taskList;

        public TaskList() { this.taskList = new ArrayList<>(); }

        public void addTaskToList(String description) { this.taskList.add(new Task(description)); }

        public void removeTaskAtIndexFromList(int index) { this.taskList.remove(index); }

        public Task getTaskAtIndex(Integer index) { return this.taskList.get(index); }

        public Integer countTasks() { return this.taskList.size(); }
    }

}
