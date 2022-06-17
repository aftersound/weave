package io.aftersound.weave.jackson;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BaseTypeDeserializerTest {

    @Test
    public void baseType() {
    }

    @Test
    public void deserializeJson() throws Exception {
        BaseTypeDeserializer<Animal> animalDeserializer = new BaseTypeDeserializer<>(
                Animal.class,
                "type",
                Arrays.asList(Dog.TYPE)
        );
        ObjectMapper mapper = ObjectMapperBuilder.forJson().with(animalDeserializer).build();

        Dog dog = new Dog();
        dog.setCanBark(true);
        String json = mapper.writeValueAsString(dog);
        Animal animal = mapper.readValue(json, Animal.class);
        assertSame(Dog.class, animal.getClass());
        Dog restored = (Dog)animal;
        assertTrue(restored.isCanBark());
    }

    @Test(expected = JsonMappingException.class)
    public void deserializeJsonFailed() throws Exception {
        BaseTypeDeserializer<Animal> animalDeserializer = new BaseTypeDeserializer<>(
                Animal.class,
                "type",
                Arrays.asList(Dog.TYPE)
        );
        ObjectMapper mapper = ObjectMapperBuilder.forJson().with(animalDeserializer).build();

        Cat cat = new Cat();
        cat.setCanMeow(true);
        String json = mapper.writeValueAsString(cat);
        Animal animal = mapper.readValue(json, Animal.class);
        assertSame(Cat.class, animal.getClass());
        Cat restored = (Cat)animal;
        assertTrue(restored.isCanMeow());
    }

    @Test
    public void deserializeYAML() throws Exception {
        BaseTypeDeserializer<Animal> animalDeserializer = new BaseTypeDeserializer<>(
                Animal.class,
                "type",
                Arrays.asList(Dog.TYPE)
        );
        ObjectMapper mapper = ObjectMapperBuilder.forYAML().with(animalDeserializer).build();

        Dog dog = new Dog();
        dog.setCanBark(true);
        String yaml = mapper.writeValueAsString(dog);
        Animal animal = mapper.readValue(yaml, Animal.class);
        assertSame(Dog.class, animal.getClass());
        Dog restored = (Dog)animal;
        assertTrue(restored.isCanBark());
    }

    @Test(expected = JsonMappingException.class)
    public void deserializeYAMLFailed() throws Exception {
        BaseTypeDeserializer<Animal> animalDeserializer = new BaseTypeDeserializer<>(
                Animal.class,
                "type",
                Arrays.asList(Dog.TYPE)
        );
        ObjectMapper mapper = ObjectMapperBuilder.forYAML().with(animalDeserializer).build();

        Cat cat = new Cat();
        cat.setCanMeow(true);
        String yaml = mapper.writeValueAsString(cat);
        Animal animal = mapper.readValue(yaml, Animal.class);
        assertSame(Cat.class, animal.getClass());
        Cat restored = (Cat)animal;
        assertTrue(restored.isCanMeow());
    }
}