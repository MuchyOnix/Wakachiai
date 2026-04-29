package com.hikari.anime.extension.source.anigoto

object AniGoToConstants {
    const val POSTER = ".aniCard .unit, a.aniOntop, div.film-poster, div.item, div.last_episodes li"
    const val POSTER_LINK = "a"
    const val POSTER_TITLE = "h6.title, h5.title, h3.film-name, p.name, .title, a"
    const val POSTER_IMAGE = "img"
    const val NEXT_PAGE = "li.page-item.next:not(.disabled), ul.pagination li.active + li a"
    const val DETAILS_SYNOPSIS = ".desc, .description, .film-description, .synopsis"
    const val DETAILS_GENRE = ".genre a[href*=genre], .item-list a[href*=genre], a[href*=genre]"
    const val DETAILS_TITLE = ".aniDetail .title, h1, .anis-content h2, .aniData h1, .title"
    const val DETAILS_POSTER = ".aniDetail .poster img, .poster img, .anis-poster img, img[alt]"
    const val DETAILS_META = ".aniMeta, .detail, .meta, .info"
    const val MOVIE_ID = "input#movie_id"
    const val EPISODE_LAST = "ul#episode_page li a:last-child"
    const val EPISODE_LINK = "li > a"
    const val EPISODE_NAME = "div.name"
    const val PLAYER_IFRAME = "iframe#player-embed, iframe"
}
