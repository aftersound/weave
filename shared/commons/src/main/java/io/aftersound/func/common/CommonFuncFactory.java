package io.aftersound.func.common;

import io.aftersound.func.Descriptor;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommonFuncFactory extends MasterAwareFuncFactory {

    private final List<MasterAwareFuncFactory> subordinates;
    private final List<Descriptor> funcDescriptors;

    public CommonFuncFactory() {
        List<MasterAwareFuncFactory> subordinates = Arrays.asList(
                new Base64FuncFactory(),
                new BasicFuncFactory(),
                new BooleanFuncFactory(),
                new CollectionsFuncFactory(),
//                new DateFuncFactory(),
                new DoubleFuncFactory(),
//                new FileFuncFactory(),
                new FloatFuncFactory(),
//                new HashFuncFactory(),
//                new HexFuncFactory(),
//                new InputStreamFuncFactory(),
                new IntegerFuncFactory(),
//                new ListFuncFactory(),
                new LongFuncFactory(),
                new MapFuncFactory(),
                new ObjectFuncFactory(),
//                new RangeFuncFactory(),
//                new RecordFuncFactory(),
//                new ResourceFuncFactory(),
                new ShortFuncFactory(),
                new StringFuncFactory()
//                new URLFuncFactory()
        );

        List<Descriptor> funcDescriptors = new ArrayList<>();
        for (FuncFactory funcFactory : subordinates) {
            funcDescriptors.addAll(funcFactory.getFuncDescriptors());
        }

        this.subordinates = subordinates;
        this.funcDescriptors = Collections.unmodifiableList(funcDescriptors);
    }

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return funcDescriptors;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        for (MasterAwareFuncFactory funcFactory : subordinates) {
            funcFactory.setMasterFuncFactory(masterFuncFactory);
            Func<IN, OUT> func = funcFactory.create(directive);
            if (func != null) {
                return func;
            }
        }
        return null;
    }

}
