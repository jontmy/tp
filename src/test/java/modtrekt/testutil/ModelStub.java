package modtrekt.testutil;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import modtrekt.commons.core.GuiSettings;
import modtrekt.model.Model;
import modtrekt.model.ReadOnlyModuleList;
import modtrekt.model.ReadOnlyTaskBook;
import modtrekt.model.ReadOnlyUserPrefs;
import modtrekt.model.module.ModCode;
import modtrekt.model.module.Module;
import modtrekt.model.task.Task;

/**
 * Model stub that implements getCurrentModule() and setCurrentModule() methods for testing.
 */
public class ModelStub implements Model {
    private ModCode currentModule = null;

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {

    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return null;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return null;
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {

    }

    @Override
    public Path getTaskBookFilePath() {
        return null;
    }

    @Override
    public Path getModuleListFilePath() {
        return null;
    }

    @Override
    public void setTaskBookFilePath(Path taskBookFilePath) {

    }

    @Override
    public void setModuleListFilePath(Path moduleListFilePath) {

    }

    @Override
    public void setTaskBook(ReadOnlyTaskBook taskBook) {

    }

    @Override
    public ReadOnlyTaskBook getTaskBook() {
        return null;
    }

    @Override
    public void setModuleList(ReadOnlyModuleList moduleList) {

    }

    @Override
    public ReadOnlyModuleList getModuleList() {
        return null;
    }

    @Override
    public void deleteTask(Task target) {

    }

    @Override
    public boolean hasModule(Module module) {
        return false;
    }

    @Override
    public boolean hasModuleWithModCode(ModCode code) {
        return false;
    }

    @Override
    public void updateModuleTaskCount(Task t) {

    }

    @Override
    public void deleteTasksOfModule(Module target) {

    }

    @Override
    public void addTask(Task t) {

    }

    @Override
    public void deleteModule(Module target) {

    }

    @Override
    public Module parseModuleFromCode(ModCode code) {
        return null;
    }

    @Override
    public ModCode getCurrentModule() {
        return currentModule;
    }

    @Override
    public void setCurrentModule(ModCode code) {
        currentModule = code;
    }

    @Override
    public void updateTaskModule(ModCode oldCode, ModCode newCode) {

    }

    @Override
    public void setTask(Task target, Task editedTask) {

    }

    @Override
    public void setDoneModuleTasksAsDone(ModCode code) {

    }

    @Override
    public ObservableList<Task> getFilteredTaskList() {
        return null;
    }

    @Override
    public void updateFilteredTaskList(Predicate<Task> predicate) {

    }

    @Override
    public void addModule(Module module) {

    }

    @Override
    public void setModule(Module target, Module editedModule) {

    }

    @Override
    public ObservableList<Module> getFilteredModuleList() {
        return null;
    }

    @Override
    public void updateFilteredModuleList(Predicate<Module> predicate) {

    }
}

