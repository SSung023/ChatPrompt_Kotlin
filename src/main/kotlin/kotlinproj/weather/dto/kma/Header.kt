package kotlinproj.weather.dto.kma

import com.fasterxml.jackson.annotation.JsonProperty

data class Header(
    @JsonProperty("resultCode")
    val resultCode: String,
    @JsonProperty("resultMsg")
    val resultMsg: String
)
