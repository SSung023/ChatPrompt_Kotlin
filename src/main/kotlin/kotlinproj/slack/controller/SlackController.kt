package kotlinproj.slack.controller

import kotlinproj.slack.dto.ValidDto
import kotlinproj.slack.service.SlackService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class SlackController(private val slackService: SlackService) {

    // Slack Request URL 검증용
    @PostMapping()
    fun test(@RequestBody req: ValidDto) : String{
        return req.challenge
    }

    @GetMapping("/slack")
    fun slackTest() {
        slackService.sendSlackMessage();
    }
}