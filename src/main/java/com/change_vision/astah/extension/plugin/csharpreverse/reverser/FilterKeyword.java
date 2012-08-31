package com.change_vision.astah.extension.plugin.csharpreverse.reverser;

import java.util.Set;

public class FilterKeyword {
    public FilterKeyword(Set<String> keywords, String toType) {
        super();
        this.keywords = keywords;
        this.toType = toType;
    }

    public Set<String> keywords;
    public String toType;
}
