package io.aftersound.weave.dependency;

import io.aftersound.weave.common.NamedType;

import java.util.List;

public interface Declaration {

    /**
     * @return
     *      a list of {@link NamedType} required by a component. Null is not allowed
     */
    List<NamedType<?>> getRequired();

}
