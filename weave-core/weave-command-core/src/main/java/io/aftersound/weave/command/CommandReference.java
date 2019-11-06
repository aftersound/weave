package io.aftersound.weave.command;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommandReference {

    private String resource;
    private String action;

    private Options options;

    public static CommandReference of(String action, String resource, Option... options) {
        CommandReference cmdRef = new CommandReference();
        cmdRef.setAction(action);
        cmdRef.setResource(resource);

        Options opts = new Options();
        for (Option option : options) {
            opts.addOption(option);
        }
        cmdRef.setOptions(opts);
        return cmdRef;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public String getHelp() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(pw,
                formatter.getWidth(),
                action + " " + resource,
                null,
                options,
                formatter.getLeftPadding(),
                formatter.getDescPadding(),
                null,
                false);
        pw.flush();

        return stringWriter.toString();
    }

}
