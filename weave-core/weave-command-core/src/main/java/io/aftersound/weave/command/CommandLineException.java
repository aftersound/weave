package io.aftersound.weave.command;

public class CommandLineException extends Exception {

    private final String help;

    CommandLineException(String message) {
        super(message);
        this.help = null;
    }

    CommandLineException(String message, String help) {
        super(message + "\n" + help);
        this.help = help;
    }

    public String getHelp() {
        return help;
    }

}
