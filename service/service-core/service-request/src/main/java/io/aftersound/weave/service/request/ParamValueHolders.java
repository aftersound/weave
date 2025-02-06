package io.aftersound.weave.service.request;

import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.Type;

import java.util.*;

/**
 * Wrapper of list of {@link ParamValueHolder}, which provides helper functions
 */
public final class ParamValueHolders {

    private static final Type DEFAULT_VALUE_TYPE = ProtoTypes.STRING.create();

    private final List<ParamValueHolder> all;

    // Stats and Views
    private final List<String> paramNames;
    private final Map<String, List<ParamValueHolder>> withScope;

    private ParamValueHolders(List<ParamValueHolder> paramValues) {
        List<ParamValueHolder> pvhList = paramValues != null ? paramValues : Collections.<ParamValueHolder>emptyList();
        this.all = Collections.unmodifiableList(pvhList);
        this.paramNames = getParamNames(pvhList);
        this.withScope = createByScopeView(pvhList);
    }

    private static List<String> getParamNames(List<ParamValueHolder> pvhList) {
        List<String> names = new ArrayList<>();
        for (ParamValueHolder pvh : pvhList) {
            names.add(pvh.getParamName());
        }
        return Collections.unmodifiableList(names);
    }

    private static Map<String, List<ParamValueHolder>> createByScopeView(List<ParamValueHolder> pvList) {
        Map<String, List<ParamValueHolder>> paramValueHoldersWithScope = new HashMap<>();
        for (ParamValueHolder pv : pvList) {
            // Group ParamValueHolder(s) with same scope
            String valueScope = pv.metadata().getScope();
            if (!paramValueHoldersWithScope.containsKey(valueScope)) {
                paramValueHoldersWithScope.put(valueScope, new ArrayList<>());
            }
            paramValueHoldersWithScope.get(valueScope).add(pv);
        }
        return Collections.unmodifiableMap(paramValueHoldersWithScope);
    }

    private static ParamValueHolder nullValueHolder(String paramName) {
        return ParamValueHolder.singleValued(paramName, DEFAULT_VALUE_TYPE, null);
    }

    public static ParamValueHolders from(List<ParamValueHolder> paramHolders) {
        return new ParamValueHolders(paramHolders);
    }

    public static ParamValueHolders emptyHolders() {
        return new ParamValueHolders(Collections.<ParamValueHolder>emptyList());
    }

    public List<ParamValueHolder> all() {
        return all;
    }

    public List<String> paramNames() {
        return paramNames;
    }

    public ParamValueHolders withScope(String paramScope) {
        return new ParamValueHolders(withScope.get(paramScope));
    }

    public ParamValueHolder firstWithName(String paramName) {
        if (paramName == null) {
            return nullValueHolder(null);
        }

        ParamValueHolder first = null;
        for (ParamValueHolder candidate : all) {
            if (paramName.equals(candidate.getParamName())) {
                first = candidate;
                break;
            }
        }
        return first != null ? first : nullValueHolder(null);
    }

    public ParamValueHolders allWithName(String paramName) {
        if (paramName == null) {
            return emptyHolders();
        }

        List<ParamValueHolder> pvhListWithSameName = new ArrayList<>();
        for (ParamValueHolder candidate : all) {
            if (paramName.equals(candidate.getParamName())) {
                pvhListWithSameName.add(candidate);
            }
        }
        return new ParamValueHolders(pvhListWithSameName);
    }

    public Map<String, ParamValueHolder> asByNameMap() {
        Map<String, ParamValueHolder> mapView = new LinkedHashMap<>();
        for (ParamValueHolder paramValueHolder : all) {
            mapView.put(paramValueHolder.getParamName(), paramValueHolder);
        }
        return Collections.unmodifiableMap(mapView);
    }

    public Map<String, Object> asModifiableMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        for (ParamValueHolder paramValueHolder : all) {
            m.put(paramValueHolder.getParamName(), paramValueHolder.getValue());
        }
        return m;
    }

    public Map<String, Object> asUnmodifiableMap() {
        return Collections.unmodifiableMap(asModifiableMap());
    }
}