package kotlinproj.weather.dto.kma

import com.fasterxml.jackson.annotation.JsonProperty

data class Item(
    @JsonProperty("baseDate")
    val baseDate: String,
    @JsonProperty("baseTime")
    val baseTime: String,
    @JsonProperty("category")
    val category: String,
    @JsonProperty("fcstDate")
    val fcstDate: String,
    @JsonProperty("fcstTime")
    val fcstTime: String,
    @JsonProperty("fcstValue")
    val fcstValue: String,
    @JsonProperty("nx")
    val nx: Int,
    @JsonProperty("ny")
    val ny: Int
)
