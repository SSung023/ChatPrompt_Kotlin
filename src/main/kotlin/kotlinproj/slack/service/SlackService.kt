package kotlinproj.slack.service

import com.slack.api.Slack
import com.slack.api.methods.request.users.UsersInfoRequest
import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
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
    lateinit var botToken:String;
    @Value("\${slack.webhook-url}")
    lateinit var webhookUrl:String;
    val greetings = listOf("안녕", "하이", "헬로", "반갑", "hello", "Hello");


    // slack bot의 message 전송
    fun sendMessageByWebhook(eventMap: Map<String, String>){
        val payload = getPayLoadByType(eventMap);

        val slackInst = Slack.getInstance();

        runCatching {
            slackInst.send(webhookUrl, payload);
        }
            .onFailure {
                err -> Logger.log.error(err.message);
                throw BusinessException(ErrorCode.SLACK_MESSAGE_DONT_SEND);
            }
    }

    // Event type에 따라 Slack 메시지 Payload 설정
    fun getPayLoadByType(eventMap: Map<String, String>) : String{
        val eventType = eventMap["type"];
        var payload = "";

        when (eventType) {
            EventType.APP_MENTION.type -> {
                payload = customizeMentionRes(eventMap)
            };
        }
        return "{\"text\":\"$payload\"}";
    }

    // app_mention일 때 조건에 따라서 다른 메세지 전송
    fun customizeMentionRes(eventValue: Map<String, String>): String {
        val text = requireNotNull(eventValue["text"]) {
            throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND)
        };
        val userId = requireNotNull(eventValue["user"]) {
            throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND)
        }
        val userDisName = getSlackDisplayName(userId);

        if (isGreetingCondition(text)) {
            return "$userDisName" + "님 안녕하세요!";
        } else {
            return "무슨 말인지 잘 모르겠어요😅";
        }
    }


    // slack api를 통해 요청한 사용자의 Profile display name를 받아옴
    fun getSlackDisplayName(userId: String): String {
        val userReq:UsersInfoRequest = UsersInfoRequest.builder()
            .user(userId)
            .token(botToken)
            .build();

        val result = runCatching {
            Slack.getInstance().methods().usersInfo(userReq);
        }
        return result.fold(
            onSuccess = { userInfo ->
                val info = userInfo.user;
                info?.let {
                    return info.profile.displayName;
                } ?: throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND);
            },
            onFailure = {
                throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND);
            }
        );
    }

    fun isGreetingCondition(text: String): Boolean {
        val split = text.split(" ").filter { it.isNotEmpty() };

        if (split.size == 1
            || greetings.any { greeting -> text.contains(greeting, ignoreCase = true) }) {
            return true;
        }
        else {
            return false;
        }
    }


}