package view.undo;

/*
 * Daniel Huson, 2021
 */
public interface Command {
    /**
     * Undoes the latest command.
     */
    void undo();

    /**
     * Redoes the latest command, which is similar to execute. the latter one is therefore not provided.
     */
    void redo();

    String name();

    /**
     * Gives a boolean saying if the latest command is undoable
     * @return
     */
    boolean canUndo();

    /**
     * Gives a boolean saying if the latest command is redoable
     * @return
     */
    boolean canRedo();
}
