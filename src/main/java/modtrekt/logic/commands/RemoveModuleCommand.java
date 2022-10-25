package modtrekt.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import modtrekt.commons.core.Messages;
import modtrekt.commons.core.index.Index;
import modtrekt.logic.commands.exceptions.CommandException;
import modtrekt.logic.parser.CliSyntax;
import modtrekt.model.Model;
import modtrekt.model.module.Module;

/**
 * Deletes a module identified using it's displayed index from the module list.
 */
public class RemoveModuleCommand extends Command {
    public static final String COMMAND_PHRASE = "remove module";
    public static final String[] COMMAND_ALIASES = new String[]{"remove mod", "rm module", "rm mod"};

    public static final String MESSAGE_USAGE = COMMAND_PHRASE
            + ": Deletes the task/module identified by the index number.\n"
            + "Prefixes: " + CliSyntax.PREFIX_MODULE + ": Modules, " + CliSyntax.PREFIX_TASK + ": Tasks\n"
            + "Format: " + COMMAND_PHRASE + " " + CliSyntax.PREFIX_MODULE + " <INDEX>";

    public static final String MESSAGE_DELETE_MODULE_SUCCESS = "Deleted Module: %1$s";

    private final Index targetIndex;

    /**
     * Returns a new RemoveModuleCommand object, with no fields initialized, for use with JCommander.
     */
    public RemoveModuleCommand() {
        this.targetIndex = null;
    }

    public RemoveModuleCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Module> lastShownList = model.getFilteredModuleList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MODULE_DISPLAYED_INDEX);
        }

        Module moduleToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteModule(moduleToDelete);
        model.deleteTasksOfModule(moduleToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_MODULE_SUCCESS, moduleToDelete));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemoveModuleCommand // instanceof handles nulls
                && targetIndex.equals(((RemoveModuleCommand) other).targetIndex)); // state check
    }
}
