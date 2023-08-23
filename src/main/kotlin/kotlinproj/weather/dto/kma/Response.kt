package kotlinproj.weather.dto.kma

import com.fasterxml.jackson.annotation.JsonProperty

data class Response(
    @JsonProperty("response")
    val response: ResponseContent
)

