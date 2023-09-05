package kotlinproj.slack.controller

import com.slack.api.util.json.GsonFactory
import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.Util.log.Logger
import kotlinproj.slack.dto.BlockPayload
import kotlinproj.slack.dto.ValidDto
import kotlinproj.slack.dto.ValueData
import kotlinproj.slack.service.SlackService
import kotlinproj.weather.service.ApiService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.time.LocalTime

@RestController
@RequestMapping("/api/slack")
class SlackController(private val slackService: SlackService,
    private val apiService: ApiService) {

    // Slack Request URL 검증용
    @PostMapping("/")
    fun validateURL(@RequestBody req: ValidDto) : String{
        return req.challenge;
    }

    // Bot event 발생 시 실행
    @PostMapping("/event")
    fun event(@RequestBody req: Map<String, Any>) {
        val eventValue = requireNotNull(req["event"]) {
            throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND)
        }
        slackService.sendMessageByWebhook(eventValue as Map<String, String>);
    }

    @GetMapping("/weather")
    fun getWeather() {
        apiService.saveWeatherList(
            apiService.requestWeatherAPI(LocalTime.now(), 10000)
                .response.body.items.item);
    }

    //
    @PostMapping(
        value = arrayOf("/action"),
        consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    )
    fun action(@RequestParam payload: String) {
        val fromJson: BlockPayload = GsonFactory.createSnakeCase()
            .fromJson(payload, BlockPayload::class.java)

        val values: List<Map<String, ValueData>> = fromJson.state.values.values.toList()
        val date = values[0]["+kP"]?.selected_date
        val time = values[0]["=DLz"]?.selected_time


    }
}

