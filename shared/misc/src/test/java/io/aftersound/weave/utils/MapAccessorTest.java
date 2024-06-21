package io.aftersound.weave.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MapAccessorTest {

    @Test
    public void test() throws Exception {
        final Map<String, Object> m;
        try (InputStream is = MapAccessorTest.class.getResourceAsStream("/MapAccessorTest.json")) {
            m = new ObjectMapper().readValue(is, Map.class);
        }
        final MapAccessor ma = new MapAccessor(m);

        Map<String, Object> address = ma.query("address");
        assertNotNull(address);

        Map<String, Object> cityAndPostalCode = ma.query("address.{city,postalCode}");
        assertNotNull(cityAndPostalCode);
        assertEquals(2, cityAndPostalCode.size());

        assertEquals("Nara", ma.query("address.city"));
        assertEquals("630-0192", ma.query("address.postalCode"));

        Object pTypes = ma.query("phoneNumbers.type");
        assertTrue(pTypes instanceof List);

        Object pNumbers = ma.query("phoneNumbers.number");
        assertTrue(pNumbers instanceof List);

        List<Map<String, Object>> phoneNumbers = ma.query("phoneNumbers.{number,type}");
        assertEquals(2, phoneNumbers.size());

        String phoneNumber = ma.query("phoneNumbers[0].number");
        assertEquals("0123-4567-8888", phoneNumber);

        Map<String, Object> phone = ma.query("phoneNumbers[0].{type,number}");
        assertEquals("0123-4567-8888", phone.get("number"));
        assertEquals("iPhone", phone.get("type"));

        String iPhoneNumber = ma.query("phoneNumbers[(type=='iPhone')|0].number");
        assertEquals("0123-4567-8888", iPhoneNumber);

        String job = ma.query("jobs[($=='Musician')|0]");
        assertEquals("Musician", job);

        job = ma.query("jobs[0]");
        assertEquals("Programmer", job);

        job = ma.query("jobs[1]");
        assertEquals("Musician", job);

        job = ma.query("jobs[2]");
        assertNull(job);

        List<String> jobs = ma.query("jobs[0:0]");
        assertEquals(1, jobs.size());
        assertEquals("Programmer", jobs.get(0));

        jobs = ma.query("jobs[0:]");
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = ma.query("jobs[0:1]");
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = ma.query("jobs[:1]");
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = ma.query("jobs[1:2]");
        assertEquals(1, jobs.size());
        assertEquals("Musician", jobs.get(0));

        jobs = ma.query("jobs[2:]");
        assertEquals(0, jobs.size());

        jobs = ma.query("jobs[(true)|0:1]");
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = ma.query("jobs[($=='Musician')|0:1]");
        assertEquals(1, jobs.size());
        assertEquals("Musician", jobs.get(0));

        jobs = ma.query("jobs[($=='Musician')|1:5]");
        assertEquals(0, jobs.size());

        jobs = ma.query("jobs[($=='Programmer' || $=='Pianist')]");
        assertEquals(1, jobs.size());
    }

}