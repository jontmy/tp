package modtrekt.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import modtrekt.commons.core.Messages;
import modtrekt.commons.core.index.Index;
import modtrekt.logic.commands.exceptions.CommandException;
import modtrekt.logic.parser.converters.DeadlineConverter;
import modtrekt.logic.parser.converters.DescriptionConverter;
import modtrekt.logic.parser.converters.IndexConverter;
import modtrekt.logic.parser.converters.ModCodeConverter;
import modtrekt.model.Model;
import modtrekt.model.module.ModCode;
import modtrekt.model.task.Deadline;
import modtrekt.model.task.Description;
import modtrekt.model.task.Task;


/**
 * Edits a task in the task book.
 */
@Parameters(commandDescription = "Edits a a task in task book.")
public class EditTaskCommand extends Command {

    public static final String COMMAND_WORD = "edit task";

    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Task successfully edited: %1$s";

    @Parameter(description = "Index of the task to edit", required = true, converter = IndexConverter.class)
    private Index targetIndex;

    @Parameter(names = "-c", description = "New module code for the task",
            converter = ModCodeConverter.class)
    private ModCode targetModule;

    @Parameter(names = "-d", description = "New deadline for the task",
            converter = DeadlineConverter.class)
    private LocalDate targetDeadline;

    @Parameter(names = "-ds", description = "New description for the task, quoted if longer than one word",
            converter = DescriptionConverter.class)
    private Description targetDescription;

    /**
     * Returns a new EditTaskCommand object, with no fields initialized, for use with JCommander.
     */
    public EditTaskCommand() {
    }

    /**
     * Creates an EditTaskCommand to edit the specified {@code Task}
     *
     * @param targetIndex       the index of the task to edit
     * @param targetModule      the ModCode that you want to change to
     * @param targetDeadline    the Deadline that you want to change to
     * @param targetDescription the description that you want to change to
     */
    public EditTaskCommand(Index targetIndex, ModCode targetModule, LocalDate targetDeadline,
                           Description targetDescription) {
        this.targetIndex = targetIndex;
        this.targetModule = targetModule;
        this.targetDeadline = targetDeadline;
        this.targetDescription = targetDescription;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Task> lastShownList = model.getFilteredTaskList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        Task taskToEdit = lastShownList.get(targetIndex.getZeroBased());

        if (targetModule == null && targetDeadline == null && targetDescription == null) {
            throw new CommandException("Please enter a parameter to edit\n"
                    + "\t-c  New module code for the task\n"
                    + "\t-d  New deadline for the task\n"
                    + "\t-ds New description for the task");
        }

        boolean isDone = taskToEdit.isDone();
        Task.Priority priority = taskToEdit.getPriority();
        ModCode code = targetModule != null ? targetModule : taskToEdit.getModule();
        if (!model.hasModuleWithModCode(code)) {
            throw new CommandException(String.format("Module code %s does not exist.",
                    code.toString()));
        }
        Description description = targetDescription != null ? targetDescription : taskToEdit.getDescription();
        LocalDate deadline = targetDeadline != null
                ? targetDeadline
                : taskToEdit instanceof Deadline
                ? ((Deadline) taskToEdit).getDueDate()
                : null;

        Task newTask = deadline != null
                ? new Deadline(description, code, deadline, isDone, priority)
                : new Task(description, code, isDone, priority);

        model.deleteTask(taskToEdit);
        model.addTask(newTask);
        return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, newTask));
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, targetModule, targetDeadline, targetDescription);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other instanceof EditTaskCommand) {
            EditTaskCommand newOther = (EditTaskCommand) other;
            return ((targetIndex == null && newOther.targetIndex == null) || targetIndex.equals(newOther.targetIndex))
                    && ((targetModule == null && newOther.targetModule == null)
                    || targetModule.equals(newOther.targetModule))
                    && ((targetDescription == null && newOther.targetDescription == null)
                    || targetDescription.equals(newOther.targetDescription))
                    && ((targetDeadline == null && newOther.targetDeadline == null)
                    || targetDeadline.equals(newOther.targetDeadline));
        }

        return false;
    }
}
