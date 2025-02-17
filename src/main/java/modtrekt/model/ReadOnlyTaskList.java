package modtrekt.model;

import javafx.collections.ObservableList;
import modtrekt.model.task.Task;

/**
 * Unmodifiable view of a task book
 */
public interface ReadOnlyTaskList {
    /**
     * Returns an unmodifiable view of the tasks list.
     * This list will not contain any duplicate tasks.
     */
    ObservableList<Task> getTaskList();
}
