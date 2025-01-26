package io.aftersound.func;

/**
 * Any {@link FuncFactory} which works as subordinate under a {@link MasterFuncFactory}
 * must extend this
 */
public abstract class MasterAwareFuncFactory implements FuncFactory {

    protected FuncFactory masterFuncFactory;

    /**
     * Caller will make this aware of master {@link FuncFactory}
     *
     * @param masterFuncFactory - the master func factory to be aware of
     */
    void setMasterFuncFactory(FuncFactory masterFuncFactory) {
        this.masterFuncFactory = masterFuncFactory;
    }

}
