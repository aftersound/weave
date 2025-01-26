package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import io.aftersound.func.FuncFactory;

import java.io.Serializable;
import java.util.List;

public class Schema implements Serializable {

    /**
     * The name of this schema
     */
    private String name;

    /**
     * Optional type hint of this schema, which could
     * indicate this schema is Cassandra table schema
     * or MySQL table schema, etc.
     */
    private String kind;

    /**
     * The list of fields in this schema
     */
    private List<Field> fields;

    /**
     * schema level directives
     * Optional
     */
    private List<Directive> directives;

    /**
     * wrapper dictionary derived from this.fields
     */
    private transient Dictionary<Field> dictionary;

    /**
     * The function directive by label
     */
    private transient Dictionary<Directive> _directives;

    /**
     * create a {@link Schema} with given name and {@link Field}s
     *
     * @param name the schema name
     * @param fields the schema fields
     * @return the {@link Schema} with given name and {@link Field}s
     */
    public static Schema of(String name, List<Field> fields) {
        Schema schema = new Schema();
        schema.setName(name);
        schema.setFields(fields);
        return schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    /**
     * Expected to be called once and only once
     *
     * @param funcFactory - the {@link FuncFactory} used to initialize the directives
     * @return this
     */
    public Schema init(FuncFactory funcFactory) {
        this.dictionary = Util.createFieldictionary(name, fields);

        // initialize schema level directives
        this._directives = Util.initDirectivesAndCreateDictionary(directives, funcFactory);

        // initialize field level directives


        return this;
    }

    public Dictionary<Field> dictionary() {
        if (dictionary == null) {
            throw new IllegalStateException("This Schema is not ready for use yet. Please 'init' it before using it");
        }
        return dictionary;
    }
}
