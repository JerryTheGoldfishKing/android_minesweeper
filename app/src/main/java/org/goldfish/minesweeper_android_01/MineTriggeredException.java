package org.goldfish.minesweeper_android_01;

public class MineTriggeredException extends Exception {
    int row,col;
    public MineTriggeredException(String message) {
        super(message);
    }

    public MineTriggeredException(String message, int col, int row) {
        this(message);
        this.col = col;
        this.row = row;
    }
}
