package modtrekt.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import modtrekt.commons.core.Messages;
import modtrekt.commons.core.index.Index;
import modtrekt.logic.commands.exceptions.CommandException;
import modtrekt.logic.parser.CliSyntax;
import modtrekt.model.Model;
import modtrekt.model.task.Task;

/**
 * Deletes a task identified using it's displayed index from the task book.
 */
public class RemoveTaskCommand extends Command {
    public static final String COMMAND_PHRASE = "remove task";
    public static final String[] COMMAND_ALIASES = new String[]{"rm task"};

    public static final String MESSAGE_USAGE = COMMAND_PHRASE
            + ": Deletes the task/module identified by the index number.\n"
            + "Prefixes: " + CliSyntax.PREFIX_MODULE + ": Modules, " + CliSyntax.PREFIX_TASK + ": Tasks\n"
            + "Format: " + COMMAND_PHRASE + " " + CliSyntax.PREFIX_MODULE + " <INDEX>";

    public static final String MESSAGE_DELETE_TASK_SUCCESS = "Removed Task: %1$s";

    private final Index targetIndex;

    public RemoveTaskCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }
    /**
     * Returns a new RemoveTaskCommand object, with no fields initialized, for use with JCommander.
     */
    public RemoveTaskCommand() {
        this.targetIndex = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Task> lastShownList = model.getFilteredTaskList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        Task taskToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteTask(taskToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_TASK_SUCCESS, taskToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemoveTaskCommand // instanceof handles nulls
                && targetIndex.equals(((RemoveTaskCommand) other).targetIndex)); // state check
    }
}
