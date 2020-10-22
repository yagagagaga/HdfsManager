package hdfsmanager.util;

import javax.swing.*;

public enum MsgType {
    ERROR(JOptionPane.ERROR_MESSAGE, JRootPane.ERROR_DIALOG),
    INFORMATION(JOptionPane.INFORMATION_MESSAGE, JRootPane.INFORMATION_DIALOG),
    WARNING(JOptionPane.WARNING_MESSAGE, JRootPane.WARNING_DIALOG),
    QUESTION(JOptionPane.QUESTION_MESSAGE, JRootPane.QUESTION_DIALOG),
    PLAIN(JOptionPane.PLAIN_MESSAGE, JRootPane.PLAIN_DIALOG);

    int level;
    int style;
    MsgType(int level, int style) {
        this.level = level;
        this.style = style;
    }

    int getLevel() {return level;}
    int getStyle() {return style;}
}