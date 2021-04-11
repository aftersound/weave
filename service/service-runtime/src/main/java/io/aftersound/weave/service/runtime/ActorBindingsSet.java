package io.aftersound.weave.service.runtime;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncControl;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.process.Processor;
import io.aftersound.weave.process.ProcessorControl;
import io.aftersound.weave.process.ProcessorFactory;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheFactory;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.metadata.param.Validation;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.request.Validator;
import io.aftersound.weave.service.security.Auth;
import io.aftersound.weave.service.security.AuthControl;
import io.aftersound.weave.service.security.AuthHandler;

class ActorBindingsSet {
    ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings;
    ActorBindings<KeyControl, KeyGenerator, Object> cacheKeyGeneratorBindings;
    ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings;
    ActorBindings<Validation, Validator, Messages> validatorBindings;
    ActorBindings<DeriveControl, Deriver, ParamValueHolder> deriverBindings;
    ActorBindings<CodecControl, CodecFactory, Codec> codecFactoryBindings;
    ActorBindings<ValueFuncControl, ValueFuncFactory, ValueFunc> valueFuncFactoryBindings;
    ActorBindings<AuthControl, AuthHandler, Auth> authHandlerBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> serviceExecutorBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> adminServiceExecutorBindings;
    ActorBindings<ProcessorControl, ProcessorFactory, Processor> processorFactoryBindings;
}
