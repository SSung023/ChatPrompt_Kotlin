package kotlinproj.slack

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import kotlinproj.Util.log.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author HeeYeon
 * @description
 */
@Service
@Transactional(readOnly = true)
class SlackService {
    @Value(value = "\${slack.bot-token}")
    lateinit var token:String

    fun sendSlackMessage() {
        val methods:MethodsClient = Slack.getInstance().methods(token)
        runCatching {
            methods.chatPostMessage(ChatPostMessageRequest.builder()
                .channel("#오늘-날씨-어때")
                .text("나무늘봇..... 메세지... 테스트....")
                .build()
            )
        }.fold(
            onSuccess = { Logger.log.info("slack bot send message test success")},
            onFailure = {e -> Logger.log.info(e.message)}
        )
    }
}