package com.hikari.anime.data.repository

import com.hikari.anime.extension.api.model.SAnime

object DemoCatalog {
    val anime = listOf(
        SAnime("/watch/eclipse-requiem", "Eclipse Requiem", "https://cdn.myanimelist.net/images/anime/10/47347.jpg", "Action, Fantasy, Adventure", "A shadow swordsman crosses a cursed kingdom to break the Black Oath.", SAnime.ONGOING, true),
        SAnime("/watch/winds-of-elysia", "Winds of Elysia", "https://cdn.myanimelist.net/images/anime/1223/96541.jpg", "Fantasy, Drama", "Skyborne pilots chase a storm said to grant impossible wishes.", SAnime.ONGOING, true),
        SAnime("/watch/skybound-chronicles", "Skybound Chronicles", "https://cdn.myanimelist.net/images/anime/13/17405.jpg", "Adventure, Sci-Fi", "A crew of aerial couriers uncovers an empire above the clouds.", SAnime.COMPLETED, true),
        SAnime("/watch/neon-requiem", "Neon Requiem", "https://cdn.myanimelist.net/images/anime/5/73199.jpg", "Sci-Fi, Action", "A city of holograms hides one last signal from a forgotten war.", SAnime.ONGOING, true),
        SAnime("/watch/fragments-of-tomorrow", "Fragments of Tomorrow", "https://cdn.myanimelist.net/images/anime/1792/95088.jpg", "Romance, Drama", "Two students trade memories across different timelines.", SAnime.COMPLETED, true),
        SAnime("/watch/blades-of-dawn", "Blades of Dawn", "https://cdn.myanimelist.net/images/anime/3/40451.jpg", "Action, Adventure", "Duelists gather at first light to decide the fate of a divided realm.", SAnime.ONGOING, true),
        SAnime("/watch/silent-parallel", "Silent Parallel", "https://cdn.myanimelist.net/images/anime/1015/138006.jpg", "Mystery, Supernatural", "A quiet transfer student can hear the echoes of alternate worlds.", SAnime.ONGOING, true),
        SAnime("/watch/crimson-signal", "Crimson Signal", "https://cdn.myanimelist.net/images/anime/12/76049.jpg", "Thriller, Sci-Fi", "Investigators follow an encrypted broadcast through a rainlit megacity.", SAnime.COMPLETED, true)
    )
}
