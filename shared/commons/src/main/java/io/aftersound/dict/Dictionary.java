package io.aftersound.dict;

import java.util.List;
import java.util.function.Predicate;

/**
 * A conceptual dictionary of entries
 *
 * @param <E>, generic type of entries in the dictionary
 */
public interface Dictionary<E> {

    /**
     * @return the name of the dictionary
     */
    String getName();

    /**
     * @return all entries in the dictionary
     */
    List<E> all();

    /**
     * Filter in entries based on given {@link Predicate<E>}
     *
     * @param predicate - the predicate that will filter in entries
     * @return all entries which match the predicate
     */
    List<E> filter(Predicate<E> predicate);

    /**
     * Look up the target entry by given name
     *
     * @param name - the name of the target entry
     * @return the entry if it is in the dictionary
     */
    E byEntryName(String name);

    /**
     * Get the value of the target attribute of the target entry
     *
     * @param entryName     - the name of the target entry
     * @param attributeName -the name of the target attribute
     * @param <ATTR>,       the desired/expected type of the attribute in generic form
     * @return the value of the target attribute of the target entry
     */
    <ATTR> ATTR getAttribute(String entryName, String attributeName);
}
