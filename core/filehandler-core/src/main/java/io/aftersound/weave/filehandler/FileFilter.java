package io.aftersound.weave.filehandler;

public abstract class FileFilter<CONTROL extends FileFilterControl> {

    protected final CONTROL control;

    protected FileFilter(CONTROL filterControl) {
        this.control = filterControl;
    }

    /**
     * Test if given candidate file path could go through
     * @param candidate
     *          - file path to be tested
     * @return accepted or not
     */
    public abstract boolean accept(String candidate);

}
