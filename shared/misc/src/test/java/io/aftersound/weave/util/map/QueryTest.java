package io.aftersound.weave.util.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class QueryTest {

    @Test
    public void query() throws Exception {
        final Map<String, Object> m;
        try (InputStream is = QueryTest.class.getResourceAsStream("/MapAccessorTest.json")) {
            m = new ObjectMapper().readValue(is, Map.class);
        }

        Map<String, Object> address = Path.of("address").query().on(m);
        assertNotNull(address);

        Map<String, Object> cityAndPostalCode = Path.of("address.{city,postalCode}").query().on(m);
        assertNotNull(cityAndPostalCode);
        assertEquals(2, cityAndPostalCode.size());

        assertEquals("Nara", Path.of("address.city").query().on(m));
        assertEquals("630-0192", Path.of("address.postalCode").query().on(m));

        Object pTypes = Path.of("phoneNumbers.type").query().on(m);
        assertTrue(pTypes instanceof List);

        Object pNumbers = Path.of("phoneNumbers.number").query().on(m);
        assertTrue(pNumbers instanceof List);

        List<Map<String, Object>> phoneNumbers = Path.of("phoneNumbers.{number,type}").query().on(m);
        assertEquals(2, phoneNumbers.size());

        String phoneNumber = Path.of("phoneNumbers[0].number").query().on(m);
        assertEquals("0123-4567-8888", phoneNumber);

        Map<String, Object> phone = Path.of("phoneNumbers[0].{type,number}").query().on(m);
        assertEquals("0123-4567-8888", phone.get("number"));
        assertEquals("iPhone", phone.get("type"));

        String iPhoneNumber = Path.of("phoneNumbers[(type=='iPhone')|0].number").query().on(m);
        assertEquals("0123-4567-8888", iPhoneNumber);

        String job = Path.of("jobs[($=='Musician')|0]").query().on(m);
        assertEquals("Musician", job);

        job = Path.of("jobs[0]").query().on(m);
        assertEquals("Programmer", job);

        job = Path.of("jobs[1]").query().on(m);
        assertEquals("Musician", job);

        job = Path.of("jobs[2]").query().on(m);
        assertNull(job);

        List<String> jobs = Path.of("jobs[0:0]").query().on(m);
        assertEquals(1, jobs.size());
        assertEquals("Programmer", jobs.get(0));

        jobs = Path.of("jobs[0:]").query().on(m);
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = Path.of("jobs[0:1]").query().on(m);
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = Path.of("jobs[:1]").query().on(m);
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = Path.of("jobs[1:2]").query().on(m);
        assertEquals(1, jobs.size());
        assertEquals("Musician", jobs.get(0));

        jobs = Path.of("jobs[2:]").query().on(m);
        assertEquals(0, jobs.size());

        jobs = Path.of("jobs[(true)|0:1]").query().on(m);
        assertEquals(2, jobs.size());
        assertEquals("Programmer", jobs.get(0));
        assertEquals("Musician", jobs.get(1));

        jobs = Path.of("jobs[($=='Musician')|0:1]").query().on(m);
        assertEquals(1, jobs.size());
        assertEquals("Musician", jobs.get(0));

        jobs = Path.of("jobs[($=='Musician')|1:5]").query().on(m);
        assertEquals(0, jobs.size());

        jobs = Path.of("jobs[($=='Programmer' || $=='Pianist')]").query().on(m);
        assertEquals(1, jobs.size());
    }

}