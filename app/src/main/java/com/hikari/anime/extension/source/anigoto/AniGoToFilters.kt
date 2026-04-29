package com.hikari.anime.extension.source.anigoto

import com.hikari.anime.extension.api.model.Filter
import com.hikari.anime.extension.api.model.FilterList

fun aniGoToFilters() = FilterList(
    listOf(
        Filter.Header("Genres"),
        Filter.CheckBox("Action"),
        Filter.CheckBox("Fantasy"),
        Filter.CheckBox("Romance"),
        Filter.CheckBox("Sci-Fi")
    )
)
