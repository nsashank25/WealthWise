package com.pes.financemanager.pesfinancemanager.command;

import java.util.Stack;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CommandHistory {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    @Transactional
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear(); // Clear redo stack when new command is executed
        System.out.println("Command executed, undo stack size: " + undoStack.size() + ", redo stack size: 0");
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    @Transactional
    public void undo() {
        if (canUndo()) {
            System.out.println("Undoing command, undo stack size before: " + undoStack.size());
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
            System.out.println("Command undone, redo stack size after: " + redoStack.size());
        }
    }

    @Transactional
    public void redo() {
        if (canRedo()) {
            System.out.println("Redoing command, redo stack size before: " + redoStack.size());
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            System.out.println("Command redone, undo stack size after: " + undoStack.size());
        } else {
            System.out.println("Cannot redo: Redo stack is empty");
        }
    }
}
