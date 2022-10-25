package modtrekt.logic.parser;

import static modtrekt.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.IUsageFormatter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.UnixStyleUsageFormatter;

import modtrekt.commons.core.Messages;
import modtrekt.commons.util.StringUtil;
import modtrekt.logic.commands.AddModuleCommand;
import modtrekt.logic.commands.AddTaskCommand;
import modtrekt.logic.commands.CdModuleCommand;
import modtrekt.logic.commands.Command;
import modtrekt.logic.commands.DoneModuleCommand;
import modtrekt.logic.commands.EditModuleCommand;
import modtrekt.logic.commands.EditTaskCommand;
import modtrekt.logic.commands.ExitCommand;
import modtrekt.logic.commands.HelpCommand;
import modtrekt.logic.commands.RemoveModuleCommand;
import modtrekt.logic.commands.RemoveTaskCommand;
import modtrekt.logic.commands.UndoneModuleCommand;
import modtrekt.logic.commands.tasks.DoneTaskCommand;
import modtrekt.logic.commands.tasks.ListTasksCommand;
import modtrekt.logic.commands.tasks.PrioritizeTaskCommand;
import modtrekt.logic.commands.tasks.UndoneTaskCommand;
import modtrekt.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class ModtrektParser {
    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        if (userInput.isBlank()) {
            throw new ParseException(Messages.MESSAGE_MISSING_COMMAND);
        }
        // devs: Instantiate your commands here by passing it to addCommand() -
        //       you don't need any CommandParser classes anymore.
        JCommander jcommander = JCommander.newBuilder().programName("")
                .addCommand(RemoveModuleCommand.COMMAND_PHRASE, new RemoveModuleCommand(),
                         RemoveModuleCommand.COMMAND_ALIASES)
                .addCommand(RemoveTaskCommand.COMMAND_PHRASE, new RemoveTaskCommand(),
                         RemoveTaskCommand.COMMAND_ALIASES)
                .addCommand(ListTasksCommand.COMMAND_WORD, new ListTasksCommand())
                .addCommand(DoneTaskCommand.COMMAND_WORD, new DoneTaskCommand())
                .addCommand(UndoneTaskCommand.COMMAND_WORD, new UndoneTaskCommand())
                .addCommand(PrioritizeTaskCommand.COMMAND_WORD, new PrioritizeTaskCommand())
                .addCommand(EditTaskCommand.COMMAND_WORD, new EditTaskCommand())
                .addCommand(EditModuleCommand.COMMAND_WORD, new EditModuleCommand())
                .addCommand(AddTaskCommand.COMMAND_WORD, new AddTaskCommand())
                .addCommand(DoneModuleCommand.COMMAND_WORD, new DoneModuleCommand())
                .addCommand(UndoneModuleCommand.COMMAND_WORD, new UndoneModuleCommand())
                .addCommand(AddModuleCommand.COMMAND_WORD, new AddModuleCommand())
                .addCommand(AddModuleCommand.COMMAND_WORD_SHORTHAND, new AddModuleCommand())
                .build();
        try {
            // Get the tokens from the user input.
            // ARGUMENTS WITH SPACES MUST BE SURROUNDED BY DOUBLE-QUOTES.
            List<String> tokens = StringUtil.shellSplit(userInput.strip());

            // Since we're treating e.g. "add task" and "add module" as separate commands,
            // we'll consider "task" or "module" the scope of the command, and add it to the command word.
            String scope = tokens.size() >= 2 ? tokens.get(1) : null;
            String mainCommandWord = tokens.size() >= 1 ? tokens.get(0) : null;
            // support shorthand for module, no extra -m or module flag needed, hence we remove
            // it from the token list.
            if ("module".equals(scope) || "mod".equals(scope) || "task".equals(scope)) {
                tokens.remove(1);
                tokens.set(0, tokens.get(0) + " " + scope);
            }

            // Parse the command tokens with JCommander.
            // Invalid commands as well as missing, duplicate, or invalid options will throw a ParameterException.
            jcommander.parse(tokens.toArray(new String[0]));

            // This cast is safe since we only pass Command objects to jcommander::addCommand.
            return (Command) jcommander.getCommands().get(jcommander.getParsedCommand()).getObjects().get(0);
        } catch (ParameterException ex) {
            // Fallback to the legacy AB3 parser if the command is not recognized by JCommander.
            Command command = parseLegacyCommand(userInput);
            if (command != null) {
                return command;
            }

            // Discard the main parameter error message if present as it's not relevant to users.
            String parsedCommand = jcommander.getParsedCommand();
            if (parsedCommand == null) { // unknown command
                throw new ParseException(ex.getMessage()); // JCommander has its own unknown command message
            }
            JCommander filteredJCommander = jcommander.getCommands().get(parsedCommand);
            if (filteredJCommander == null) { // guarding against NPE in case JCommander internals change
                throw new ParseException(ex.getMessage());
            }

            // Add the formatted usage message to the error message.
            String message = ex.getMessage().endsWith("no main parameter was defined in your arg class")
                    ? "Syntax error. If your command arguments contain spaces, surround them with quotes."
                    : ex.getMessage();
            IUsageFormatter usageFormatter = new UnixStyleUsageFormatter(filteredJCommander);
            StringBuilder usageBuilder = new StringBuilder(message).append("\n\n");
            usageFormatter.usage(usageBuilder);

            // Rethrow the JCommander unknown command ParameterException using ModtRekt's ParseException as
            // it displays the error message in the UI.
            throw new ParseException(usageBuilder.toString());
        }
    }

    private Command parseLegacyCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }
        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {
        case RemoveModuleCommand.COMMAND_PHRASE:
            return new RemoveCommandParser().parse(arguments);
        case RemoveTaskCommand.COMMAND_PHRASE:
            return new RemoveCommandParser().parse(arguments);
        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();
        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();
        case CdModuleCommand.COMMAND_WORD:
            return new CdCommandParser().parse(arguments);
        default:
            return null;
        }
    }
}
