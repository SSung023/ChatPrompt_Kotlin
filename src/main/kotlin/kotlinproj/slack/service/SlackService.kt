package kotlinproj.slack.service

import com.slack.api.Slack
import com.slack.api.webhook.WebhookResponse
import kotlinproj.Util.log.Logger
import kotlinproj.slack.constant.EventType
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
    lateinit var token:String;
    @Value("\${slack.webhook-url}")
    lateinit var webhookUrl:String;


    fun sendMessageByWebhook(eventValue: Any){
        val eventInfo = eventValue as Map<String, String>;
        val eventType = eventInfo["type"];

        when (eventType) {
            EventType.APP_MENTION.type -> Logger.log.info("test")
        }

        val res:WebhookResponse = Slack.getInstance()
            .send(webhookUrl, "{\"text\":\"안녕!!\"}");


    }
}