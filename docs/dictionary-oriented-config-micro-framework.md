# A Dictionary-oriented Config Micro-framework

The config micro-framework presented in this article is designed for config-driven applications. Its objective is to 
make applications, especially those having to deal with a lot of configurables, to handle configuration in consistent 
manner, regardless of if it's about settings of connecting to a database or behavior tunables.

It is adopted in all client extensions of Weave, which helps create a unified form to represent settings of connection  
toward any system a Weave application instance has to connect to, no matter the system is Kafka, Cassandra, Couchbase, 
or Open Swift, HDFS, etc, etc.

Let's jump into an example to see how this config micro-framework works and how it could help applications handle config
cleanly.

## An example

First, a dictionary which defines config keys with regard to connecting to Apache Kafka Cluster as consumer.  
```java
package io.aftersound.weave.example;

import io.aftersound.weave.common.CommonKeyFilters;
import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.IntegerParser;
import io.aftersound.weave.common.parser.LongParser;
import io.aftersound.weave.common.parser.StringParser;

import java.util.Collection;
import java.util.List;

public class ConsumerConfigKeyDictionary extends Dictionary {

    public static final Key<String> GROUP_ID = Key.of(
            "group.id",
            new StringParser()
    ).description(
            "A unique string that identifies the consumer group this consumer belongs to. This property is required if "
            + "the consumer uses either the group management functionality by using <code>subscribe(topic)</code> or "
            + "the Kafka-based offset management strategy."
    ).markAsRequired();

    public static final Key<String> GROUP_INSTANCE_ID = Key.of(
            "group.instance.id",
            new StringParser()
    ).description(
            "A unique identifier of the consumer instance provided by the end user. Only non-empty strings are "
            + "permitted. If set, the consumer is treated as a static member, which means that only one instance with "
            + " this ID is allowed in the consumer group at any time. This can be used in combination with a larger "
            + " session timeout to avoid group rebalances caused by transient unavailability (e.g. process restarts). "
            + "If not set, the consumer will join the group as a dynamic member, which is the traditional behavior."
    );

    public static final Key<Integer> MAX_POLL_RECORDS = Key.of(
            "max.poll.records",
            new IntegerParser().defaultValue(500)
    ).description(
            "The maximum number of records returned in a single call to poll()."
    );

    public static final Key<Long> MAX_POLL_INTERVAL = Key.of(
            "max.poll.interval.ms",
            new LongParser().defaultValue(30000L)
    ).description(
            "The maximum delay between invocations of poll() when using "
            + "consumer group management. This places an upper bound on the amount of time that the consumer can be "
            + "idle before fetching more records. If poll() is not called before expiration of this timeout, then the "
            + "consumer is considered failed and the group will rebalance in order to reassign the partitions to "
            + "another member. For consumers using a non-null <code>group.instance.id</code> which reach this timeout, "
            + "partitions will not be immediately reassigned. Instead, the consumer will stop sending heartbeats and "
            + "partitions will be reassigned after expiration of <code>session.timeout.ms</code>. This mirrors the "
            + " behavior of a static consumer which has shutdown."
    );

    public static final Key<Long> SESSION_TIMEOUT = Key.of(
            "session.timeout.ms",
            new LongParser().defaultValue(10000L)
    ).description(
            "The timeout used to detect client failures when using "
            + "Kafka's group management facility. The client sends periodic heartbeats to indicate its liveness "
            + "to the broker. If no heartbeats are received by the broker before the expiration of this session "
            + "timeout, then the broker will remove this client from the group and initiate a rebalance. Note that the "
            + "value must be in the allowable range as configured in the broker configuration by "
            + "<code>group.min.session.timeout.ms</code> and <code>group.max.session.timeout.ms</code>."
    );

    public static final Key<String> BOOTSTRAP_SERVERS = Key.of(
            "bootstrap.servers",
            new StringParser()
    ).description(
            "A list of host/port pairs to use for establishing the initial connection to the Kafka cluster. The client "
            + "will make use of all servers irrespective of which servers are specified here for bootstrapping&mdash;"
            + "this list only impacts the initial hosts used to discover the full set of servers. This list should be "
            + "in the form <code>host1:port1,host2:port2,...</code>. Since these servers are just used for the initial "
            + " connection to discover the full cluster membership (which may change dynamically), this list need not "
            + "contain the full set of servers (you may want more than one, though, in case a server is down)."
    );

    public static final Key<Integer> METADATA_MAX_AGE = Key.of(
            "metadata.max.age.ms",
            new IntegerParser().defaultValue(30000)
    ).description(
            "The period of time in milliseconds after which we force a refresh of metadata even if we haven't seen any "
            + "partition leadership changes to proactively discover any new brokers or partitions."
    );

    public static final Key<Integer> SEND_BUFFER = Key.of(
            "send.buffer.bytes",
            new IntegerParser().defaultValue(128 * 1024)
    ).description(
            "The size of the TCP send buffer (SO_SNDBUF) to use when sending data. If the value is -1, the OS default "
            + "will be used."
    );

    public static final Key<String> TOPIC = Key.of(
            "topic",
            new StringParser()
    ).description(
            "The name of topic to consume from"
    ).markAsRequired();

    public static final Key<List<Integer>> PARTITIONS = Key.of(
            "partitions",
            new IntegerListParser(",")
    ).description(
            "The partitions of topic to consume"
    ).markAsRequired();

    public static final Collection<Key<?>> CONSUMER_CONFIG_KEYS;
    static {
        lockDictionary(ConsumerConfigKeyDictionary.class);
        CONSUMER_CONFIG_KEYS = getDeclaredKeys(ConsumerConfigKeyDictionary.class, CommonKeyFilters.ANY);
    }

}

```

Just a personal opinion: it provides better cohesion and clarity in the way of defining config keys than what's in Kafka 
source code.  

Include a link to ConsumerConfig for quick reference and comparison.
[ConsumerConfig.java](https://github.com/a0x8o/kafka/blob/master/clients/src/main/java/org/apache/kafka/clients/consumer/ConsumerConfig.java)

Next is a unit test as a demonstration of how to read raw config in form of Map<String, String> and use the config object.

```java
package io.aftersound.weave.example;

import io.aftersound.weave.config.ConfigException;
import io.aftersound.weave.config.ConfigUtils;
import io.aftersound.weave.config.Settings;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static io.aftersound.weave.example.ConsumerConfigKeyDictionary.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConsumerConfigTest {

    @Test
    public void testConsumerConfig() {
        // good config
        Map<String, String> configSource = MapBuilder.hashMap()
                .kv("group.id", "my.kakfa.consumer")
                .kv("group.instance.id", "my.kafka.consumer.instance")
                .kv("max.poll.records", "1000")
                .kv("max.poll.interval.ms", "45000")
                .kv("session.timeout.ms", "20000")
                .kv("bootstrap.servers", "192.168.0.1:2181,192.168.0.10:2181")
                .kv("topic", "twitter")
                .kv("partitions", "1,3,5,7")
                .build();
        
        // read and parse config
        Settings config = Settings.from(
                ConfigUtils.extractConfig(
                        configSource,
                        CONSUMER_CONFIG_KEYS
                )
        );
        
        // access config values in strong types
        assertEquals("my.kakfa.consumer", config.v(GROUP_ID));
        assertEquals("my.kafka.consumer.instance", config.v(GROUP_INSTANCE_ID));
        assertEquals(1000, config.v(MAX_POLL_RECORDS).intValue());
        assertEquals(45000L, config.v(MAX_POLL_INTERVAL).longValue());
        assertEquals(20000L, config.v(SESSION_TIMEOUT).longValue());
        assertEquals("192.168.0.1:2181,192.168.0.10:2181", config.v(BOOTSTRAP_SERVERS));
        assertEquals(30000, config.v(METADATA_MAX_AGE).intValue());
        assertEquals(128 * 1024, config.v(SEND_BUFFER).intValue());
        List<Integer> partitions = config.v(PARTITIONS);
        assertEquals(4, partitions.size());
        assertEquals(1, partitions.get(0).intValue());
        assertEquals(3, partitions.get(1).intValue());
        assertEquals(5, partitions.get(2).intValue());
        assertEquals(7, partitions.get(3).intValue());

        // config missing required
        configSource = MapBuilder.hashMap()
                .kv("max.poll.records", "1000")
                .kv("max.poll.interval.ms", "45000")
                .kv("session.timeout.ms", "20000")
                .kv("bootstrap.servers", "192.168.0.1:2181,192.168.0.10:2181")
                .build();

        ConfigException ce = null;
        try {
            Settings.from(
                    ConfigUtils.extractConfig(
                            configSource,
                            CONSUMER_CONFIG_KEYS
                    )
            );
        } catch (ConfigException e) {
            ce = e;
        }
        assertNotNull(ce);
    }

}

```

To support customized config value type, simply implement ValueParser interface or extend from base value parser.  

```java
package io.aftersound.weave.example;

import io.aftersound.weave.common.parser.FirstRawKeyValueParser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Customized parser for list of integer values in form of delimited string
 */
public class IntegerListParser extends FirstRawKeyValueParser<List<Integer>> {

    private final Pattern pattern;

    public IntegerListParser(String delimiter) {
        this.pattern = Pattern.compile(delimiter);
    }

    @Override
    protected List<Integer> _parse(String rawValue) {
        String[] values = pattern.split(rawValue);
        List<Integer> ints = new ArrayList<>();
        for (String value : values) {
            ints.add(Integer.parseInt(value));
        }
        return ints;
    }

}

```

You could find complete source code of this example at 
[ConsumerConfigTest](https://github.com/aftersound/weave-examples/blob/master/config-example/src/test/java/io/aftersound/weave/example/ConsumerConfigTest.java)

Hope the example explains itself.



 
 
