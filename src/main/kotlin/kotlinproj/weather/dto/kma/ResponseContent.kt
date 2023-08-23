package kotlinproj.weather.dto.kma

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseContent(
    @JsonProperty("header")
    val header: Header,
    @JsonProperty("body")
    val body: Body
)
