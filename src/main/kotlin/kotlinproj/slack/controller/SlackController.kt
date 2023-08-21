package kotlinproj.slack.controller

import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.slack.dto.ValidDto
import kotlinproj.slack.service.SlackService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/slack")
class SlackController(private val slackService: SlackService) {

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
}