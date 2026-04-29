package com.hikari.anime.extension.api.model

sealed class Filter(val name: String) {
    class Header(name: String) : Filter(name)
    class Separator : Filter("")
    class CheckBox(name: String, var state: Boolean = false) : Filter(name)
    class Select<T>(name: String, val values: List<T>, var selected: T) : Filter(name)
}

class FilterList(filters: List<Filter> = emptyList()) : List<Filter> by filters
