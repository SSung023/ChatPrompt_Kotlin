package kotlinproj.weather.dto.kma

import com.fasterxml.jackson.annotation.JsonProperty

data class Items(
    @JsonProperty("item")
    val item: List<Item>
)

