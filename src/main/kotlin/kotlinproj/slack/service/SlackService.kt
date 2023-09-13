package kotlinproj.slack.service

import com.slack.api.Slack
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.kotlin_extension.block.withBlocks
import com.slack.api.util.json.GsonFactory
import com.slack.api.webhook.Payload
import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.exception.constants.ErrorCode
import kotlinproj.Util.log.Logger
import kotlinproj.slack.constant.EventType
import kotlinproj.slack.dto.BlockPayload
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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


    // slack bot의 message 전송
    fun sendMessageByWebhook(eventMap: Map<String, String>){
        val slackInst = Slack.getInstance();
        val payload = getPayloadByType(eventMap);

        runCatching {
            slackInst.send(webhookUrl, payload);
        }
            .onFailure {
                err -> Logger.log.error(err.message);
                throw BusinessException(ErrorCode.SLACK_MESSAGE_DONT_SEND);
            }
    }

    // Event type에 따라 Slack 메시지 Payload 설정
    fun getPayloadByType(eventMap: Map<String, String>) : Payload{
        val eventType = eventMap["type"];
        var layoutBlocks:List<LayoutBlock> = listOf()

        when (eventType) {
            EventType.APP_MENTION.type -> {
                layoutBlocks = customizeBlocks(eventMap)
            };
        }
        return Payload.builder()
            .blocks(layoutBlocks)
            .build();
    }

    // app_mention일 때 조건에 따라서 다른 메세지 전송
    fun customizeBlocks(eventValue: Map<String, String>): List<LayoutBlock> {
        val text = requireNotNull(eventValue["text"]) {
            throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND)
        };
        val userId = requireNotNull(eventValue["user"]) {
            throw BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND)
        }

        if (isGreetingCondition(text)) {
            val userDisName = getSlackDisplayName(userId)
            return getGreetingLayoutBlock(userDisName)
        } else if( isWeatherAskingCondition(text) ) {
            return getAskingWeatherLayoutBlock()
        } else {
            return withBlocks {
                section {
                    markdownText("")
                }
            }
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
        val greetings = listOf("안녕", "하이", "헬로", "반갑", "hello", "Hello");
        val split = text.split(" ").filter { it.isNotEmpty() };

        if (split.size == 1
            || greetings.any { greeting -> text.contains(greeting, ignoreCase = true) }) {
            return true;
        }
        else {
            return false;
        }
    }

    fun isWeatherAskingCondition(text: String): Boolean {
        val split = text.split(" ").filter { it.isNotEmpty() };
        val weatherWord = "날씨"

        if (split.any{ it.contains(weatherWord)}) {
            return true;
        }
        return false;
    }


    //=== Slack Block Kit  ===//
    fun getGreetingLayoutBlock(userDisName:String): List<LayoutBlock>{
        return withBlocks {
            section {
                markdownText("안녕하세요 :wave:, 저는 :sloth: *나무늘봇* 이에요. \n 날씨를 찾아보기 귀찮으신 *$userDisName* 님 대신 날씨를 찾아보고 알려드릴게요!\n" +
                        "제가.. 궁금하실 분들을 위해 사용법을 알려드릴게요!:sunglasses:")
            }
            section {
                markdownText("*`@`로 저를 언급하면서 `날씨`, `날씨 어때?`와 같이 날씨에 대해 물어봐주세요*. 날짜와 시간을 선택할 수 있는 화면을 띄워드릴게요. 원하는 날짜/시간을 선택해주세요. 남은 하루 동안의 날씨에 대해 알려드릴게요.\n _(현재 시간으로부터 최대 3일까지 조회가 가능해요)_")
            }
            section {
                markdownText(":heavy_plus_sign:날씨 정보는 하루에 8번의 업데이트가 진행돼요. \n 새벽 2시(02시)부터 3시간 간격으로 날씨 데이터가 갱신되는 점 참고해주세요. \n _(02:00, 05:00, 08:00, 11:00, 14:00, 17:00, 20:00, 23:00)_")
            }
        }
    }
    fun getAskingWeatherLayoutBlock() : List<LayoutBlock>{
        val curDate:String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val curTime:String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

        return withBlocks {
            section {
                markdownText(":sloth: *나무늘봇* 에게 날씨를 물어보세요! \n 날씨를 알고 싶은 *날짜* 와 *시간* 을 골라주세요! 날씨 정보는 한 시간 단위로 나눠서 알려드려요. :slightly_smiling_face:")
            }
            actions {
                elements {
                    datePicker {
                        initialDate(curDate)
                        placeholder("Select a date :date:", true)
                    }
                    timePicker {
                        initialTime(curTime)
                        placeholder("Select time :stopwatch:", true)
                    }
                    button {
                        actionId("get-weather")
                        text("Click me", true)
                    }
                }
            }
        }
    }

    /**
     * Slack Block Kit의 DateTimePicker를 통해 날짜/시간 정보 선택 시 날짜 정보를 Pair 형식으로 추출
     */
    fun getDateTimeFromBlockKit(payload: String): Pair<String, String> {
        val json: BlockPayload = GsonFactory.createSnakeCase()
            .fromJson(payload, BlockPayload::class.java)

        val blockKit = json.state.values.values.toList()[0].values.toList();
        var date = ""
        var time = ""
        for (valueData in blockKit) {
            when(valueData.type) {
                "datepicker" -> {
                    date = valueData.selected_date
                }
                "timepicker" -> {
                    time = valueData.selected_time
                }
            }
        }
        return Pair(date, time)
    }

}