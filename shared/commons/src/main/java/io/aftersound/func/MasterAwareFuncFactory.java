package io.aftersound.func;

public abstract class MasterAwareFuncFactory implements FuncFactory {

    protected FuncFactory masterFuncFactory;

    public void setMasterFuncFactory(FuncFactory masterFuncFactory) {
        this.masterFuncFactory = masterFuncFactory;
    }

}
