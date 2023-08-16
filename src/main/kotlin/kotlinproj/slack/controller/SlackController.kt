package kotlinproj.slack.controller

import kotlinproj.Util.log.Logger
import kotlinproj.slack.SlackService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SlackController(private val slackService: SlackService) {

    @GetMapping("/slack")
    fun slackTest() {
        slackService.sendSlackMessage();
    }
}