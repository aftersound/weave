package io.aftersound.weave.service.metadata.param;

import io.aftersound.weave.common.Constraint;

import java.util.*;

/**
 * Wrapper of list of {@link ParamField}, which provides helper functions
 */
public class ParamFields {

    private static final String UNNAMED = "_UNNAMED";

    private final List<ParamField> all;

    // derived
    private final ParamField unnamed;
    private final Map<String, ParamField> byName;
    private final Map<ParamType, List<ParamField>> byParamType;
    private final Map<Constraint.Type, List<ParamField>> byConstraintType;

    private ParamFields(List<ParamField> paramFields) {
        List<ParamField> pfList = paramFields != null ? paramFields : Collections.<ParamField>emptyList();
        this.all = pfList;

        Map<String, ParamField> byName = new LinkedHashMap<>();
        Map<ParamType, List<ParamField>> byParamType = new HashMap<>();
        Map<Constraint.Type, List<ParamField>> byConstraintType = new HashMap<>();
        populateDerived(pfList, byName, byParamType, byConstraintType);

        this.unnamed = byName.remove(UNNAMED);
        this.byName = Collections.unmodifiableMap(byName);
        this.byParamType = Collections.unmodifiableMap(byParamType);
        this.byConstraintType = Collections.unmodifiableMap(byConstraintType);
    }

    private static void populateDerived(
            List<ParamField> pfList,
            Map<String, ParamField> byName,
            Map<ParamType, List<ParamField>> byParamType,
            Map<Constraint.Type, List<ParamField>> byConstraintType) {

        if (pfList == null || pfList.isEmpty()) {
            return;
        }

        for (ParamField paramField : pfList) {
            // name related
            if (paramField.getName() != null) {
                byName.put(paramField.getName(), paramField);
            } else {
                byName.put(UNNAMED, paramField);
            }

            // group ParamField(s) with same ParamType
            if (!byParamType.containsKey(paramField.getParamType())) {
                byParamType.put(paramField.getParamType(), new ArrayList<ParamField>());
            }
            byParamType.get(paramField.getParamType()).add(paramField);

            // group ParamField(s) with same ConstraintType
            if (!byConstraintType.containsKey(paramField.getConstraint().getType())) {
                byConstraintType.put(paramField.getConstraint().getType(), new ArrayList<ParamField>());
            }
            byConstraintType.get(paramField.getConstraint().getType()).add(paramField);
        }
    }

    public static ParamFields from(List<ParamField> paramFields) {
        return new ParamFields(paramFields);
    }

    public List<ParamField> all() {
        return all;
    }

    public boolean contains(String candidate) {
        return byName.containsKey(candidate);
    }

    public Collection<String> paramNames() {
        return byName.keySet();
    }

    public ParamFields named() {
        return new ParamFields(new ArrayList<>(byName.values()));
    }

    public ParamField unnamed() {
        return unnamed;
    }

    public ParamField getParamField(String name) {
        return byName.get(name);
    }

    public ParamFields withParamType(ParamType paramType) {
        return new ParamFields(byParamType.get(paramType));
    }

    public ParamField firstIfExists(ParamType type) {
        List<ParamField> candidates = withParamType(type).all();
        if (!candidates.isEmpty()) {
            return candidates.get(0);
        } else {
            return null;
        }
    }

    public ParamFields withConstraintType(Constraint.Type constraintType) {
        return new ParamFields(byConstraintType.get(constraintType));
    }
}
