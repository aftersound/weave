package io.aftersound.util.map;

import java.util.ArrayList;
import java.util.List;

class ListQuery {
    private final Filter filter;
    private final Return returnDirective;

    public ListQuery(Filter filter, Return returnDirective) {
        this.filter = filter;
        this.returnDirective = returnDirective;
    }

    public Object on(List list) {
        List matched;

        // wildcard ('*') means all should be matched
        // no need to loop through list and apply the filter
        if (filter.isWildcard()) {
            matched = list;
        } else {
            matched = new ArrayList();
            for (Object e : list) {
                if (filter.apply(e)) {
                    matched.add(e);
                }
            }
        }

        if (returnDirective == null) {
            return matched;
        }

        switch (returnDirective.getType()) {
            case FirstMatched:
                return matched.size() > 0 ? matched.get(0) : null;
            case LastMatched:
                return matched.size() > 0 ? matched.get(matched.size() - 1) : null;
            case AtIndex: {
                int index = returnDirective.getIndex();
                return (index >= 0 && index < matched.size()) ? matched.get(index) : null;
            }
            case InRange: {
                if (returnDirective.getStartIndex() >= matched.size()) {
                    return new ArrayList<>();
                }
                int effectiveEndIndex = Math.min(returnDirective.getEndIndex(), matched.size() - 1);
                return matched.subList(returnDirective.getStartIndex(), effectiveEndIndex + 1);
            }
            default:
                return matched;
        }
    }
}
