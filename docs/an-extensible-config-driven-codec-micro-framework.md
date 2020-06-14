# An Extensible Config-driven Codec Micro-framework

The codec micro-framework is designed for config-driven 
- data processing job framework
- data loader framework
- data service framework

The objective is to enable applications built on top of such frameworks to be able to handle data, whose schema can 
only be known at runtime, or better to be known at runtime. 

It is built on top of [CAP component structure](https://aftersound.github.io/weave/control-actor-product-component-structure), 
which makes codec system highly extensible and completely config driven.

Let's look into an example to see how this codec micro-framework works and how it could enable applications to have
very dynamic config-driven behavior with regard to data codec at runtime.

## An example

```java
package io.aftersound.weave.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.*;
import io.aftersound.weave.pojo.Name;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class CodecCoreAndExtensionsTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static ActorRegistry<CodecFactory> codecFactoryRegistry;

    @BeforeClass
    public static void init() throws Exception {
        ActorBindings<CodecControl, io.aftersound.weave.codec.CodecFactory, Codec> codecFactoryBindings = 
            ActorBindingsUtil.loadActorBindings(
                Arrays.asList(
                        "io.aftersound.weave.codec.jackson.JacksonCodecFactory",
                        "io.aftersound.weave.codec.jackson.SmileJacksonCodecFactory",
                        "io.aftersound.weave.codec.jackson.BsonJacksonCodecFactory"
                ),
                CodecControl.class,
                Codec.class,
                false
        );
        codecFactoryRegistry = new ActorFactory<>(codecFactoryBindings).createActorRegistryFromBindings(false);
    }

    private String getJson() throws Exception {
        Map<String, String> map = MapBuilder.hashMap()
                .kv("firstName", "9")
                .kv("lastName", "Falcon")
                .build();
        return MAPPER.writeValueAsString(map);
    }

    private JsonNode getJsonNode() throws Exception {
        Map<String, String> map = MapBuilder.hashMap()
                .kv("firstName", "9")
                .kv("lastName", "Falcon")
                .build();
        String json = MAPPER.writeValueAsString(map);
        return MAPPER.readTree(json);
    }

    private Name getName() {
        Name name = new Name();
        name.setFirstName("9");
        name.setLastName("Falcon");
        return name;
    }

    @Test(expected = CodecException.class)
    public void testNonLoadedCodec() {
        Codec.REGISTRY.get().getCodec("ThriftCodec(io.aftersound.weave.thrift.Name,Compact)", codecFactoryRegistry);
    }

    @Test
    public void testClassSmileCodec1() throws Exception {
        final Name name = getName();

        Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec(
            "JacksonCodec(io.aftersound.weave.pojo.Name,Smile)", 
            codecFactoryRegistry
        );

        // encode
        Object encoded = codec.encoder().encode(name);
        // persist
        assertTrue(encoded instanceof byte[]);

        // read from persistence
        // decode
        Object decoded = codec.decoder().decode(encoded);
        assertTrue(decoded instanceof Name);
        Name decodedName = (Name) decoded;
        assertEquals("9", decodedName.getFirstName());
        assertEquals("Falcon", decodedName.getLastName());
    }

    @Test
    public void testClassSmileCodec2() throws Exception {
        final Name name = getName();

        Codec<Name, byte[]> codec = Codec.REGISTRY.get().getCodec(
            "JacksonCodec(io.aftersound.weave.pojo.Name,Smile)", 
            codecFactoryRegistry
        );

        // encode
        byte[] encoded = codec.encoder().encode(name);
        // persist

        // read from persistence
        // decode
        Name decoded = codec.decoder().decode(encoded);
        assertEquals("9", decoded.getFirstName());
        assertEquals("Falcon", decoded.getLastName());

        // prove data encoded in Smile cannot be decoded by other codec
        DecodeException decodeException = null;
        try {
            Codec<JsonNode, byte[]> otherCodec = Codec.REGISTRY.get().getCodec(
                "JacksonCodec(io.aftersound.weave.pojo.Name,Bson)", 
                codecFactoryRegistry
            );
            otherCodec.decoder().decode(encoded);
        } catch (DecodeException e) {
            decodeException = e;
        }
        assertNotNull(decodeException);
    }
    
}

```

## Explanation

Typically, during initialization phase, data processing framework, data loader framework or micro-service framework 
need to 
- initialize codec micro-framework.
- load desired codec extensions, more specifically, load implementations of CodecFactory.
- create instances of loaded CodecFactory implementations, and bind each with corresponding control type

```java

ActorBindings<CodecControl, io.aftersound.weave.codec.CodecFactory, Codec> codecFactoryBindings = 
    ActorBindingsUtil.loadActorBindings(
        Arrays.asList(
                "io.aftersound.weave.codec.jackson.JacksonCodecFactory",
                "io.aftersound.weave.codec.jackson.SmileJacksonCodecFactory",
                "io.aftersound.weave.codec.jackson.BsonJacksonCodecFactory"
        ),
        CodecControl.class,
        Codec.class,
        false
);
ActorRegistry<CodecFactory> codecFactoryRegistry = new ActorFactory<>(codecFactoryBindings)
    .createActorRegistryFromBindings(false);

```

Once framework is initialized, applications can get hold of desired instance of codec by providing codec spec in order to 
encode data or decode data.

```java

Codec<Name, byte[]> codec = Codec.REGISTRY.get().getCodec(
    "JacksonCodec(io.aftersound.weave.pojo.Name,Smile)", 
    codecFactoryRegistry
);

Name name = getNameToBePersisted();
byte[] encoded = codec.encoder().encode(name);
persistDataToDatabase(encoded);

```

```java

Codec<Object, Object> codec = Codec.REGISTRY.get().getCodec(
    "JacksonCodec(io.aftersound.weave.pojo.Name,Smile)", 
    codecFactoryRegistry
);

Records records = fetchRecordsFromDatabase();
List<Object> values = new ArrayList<>();
for (Record record : records) {
    byte[] fieldValue = record.getFieldValue(0);
    Object value = codec.decoder().decode(fieldValue);
    values.add(value);
}

```

Note, 
- every codec instance is lazily instantiated once and only one, but cached in CodecRegistry in association with codec spec
- codec spec is described as textual expression in a slight variation of prefix polish notation


## Codec Extensions
- All actual codec functionalities are provided by extensions.
- Source code of available extensions could be found at [codec-extensions](https://github.com/aftersound/weave-managed-extensions/tree/master/codec-extensions).
  - note there is a special ChainedCodec, which supports two level codec-chain-of-responsibility. Example below,
    
    ChainedCodec(JacksonCodec(JsonNode,Json),AvroCodec(twitter.meesage,GitBasedSchemaProvider(https://github.com/aftersound/weave/sample-avro-schemas),Json,Binary))

    Read as 
      - encode JsonNode to JSON string, then encode JSON into AVRO binary format with schema from given provider.
      - decode byte array in AVRO with schema 'name' from specified provider to JSON string, then decode JSON to JsonNode object.

- New extension could be created by following [codec-extension-development-guide](https://aftersound.github.io/weave/extension-point-codec-factory).
- To enable framework runtime and applications to support new type of codec, make new codec factory visible to framework by adding it to the bindings.

 
 
