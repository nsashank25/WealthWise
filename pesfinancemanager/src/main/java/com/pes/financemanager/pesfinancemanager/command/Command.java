// Command interface
package com.pes.financemanager.pesfinancemanager.command;

public interface Command {
    void execute();
    void undo();
}