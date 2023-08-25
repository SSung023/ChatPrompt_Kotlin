package kotlinproj.slack.service

import kotlinproj.Util.exception.BusinessException
import kotlinproj.slack.constant.EventType
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class SlackServiceTest {
    @Autowired lateinit var slackService: SlackService;


    @Test
    @DisplayName("webhook 통해 slack 메세지 전송에 성공했을 때에는 에러가 발생하지 않아야 한다.")
    fun shouldNotThrowException_when_sendNormally() {
        //given
        val eventMap = getEventValue(EventType.APP_MENTION.type, "");

        //when
        slackService.sendMessageByWebhook(eventMap);

        //then
        Assertions.assertThatNoException();
    }

    @Test
    @DisplayName("event의 종류가 app_mention일 때 실행되어야 한다.")
    fun shouldRun_when_eventType_app_mention() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "");

        //when
        val payload = slackService.getPayloadByType(eventValue);

        //then
        assertThat(payload).isEqualTo("{\"text\":\"HEY님 안녕하세요!\"}");
    }
    
    @Test
    @DisplayName("api를 통해 user의 정보를 받아올 수 있다.")
    fun canExtract_userInfo_ByAPI() {
        //given
        val userId = "U05MVCYDKJL" // ex) U05MVCYDKJL
        
        //when
        val username:String = slackService.getSlackDisplayName(userId);
        
        //then
        assertThat(username).isEqualTo("HEY");
    }

    @Test
    @DisplayName("user의 정보가 없을 때에는 예외가 발생해야 한다.")
    fun should_throwException_when_NoUserInfo() {
        //given
        val userId = "FakeUserId";

        //when, then
        Assertions.assertThatThrownBy{
            slackService.getSlackDisplayName(userId)
        }.isInstanceOf(BusinessException::class.java);
    }

    @Test
    @DisplayName("멘션만 있거나 인삿말이 포함되어 있는 경우에는 true를 반환해야 한다.")
    fun shouldReturn_True() {
        //given
        val text1 = "<@U05MMBQ2AKD>";
        val text2 = "<@U05MMBQ2AKD> 안녕";

        //when
        val res1 = slackService.isGreetingCondition(text1);
        val res2 = slackService.isGreetingCondition(text2);

        //then
        assertThat(res1).isTrue();
        assertThat(res2).isTrue();
    }

    @Test
    @DisplayName("slack event 내용에 인삿말이 있으면 ()님 안녕하세요!를 출력해야 한다.")
    fun should_SayHello_when_IncludeHello() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "안녕");

        //when
        val greeting:String? = slackService.customizeMsgByCondition(eventValue);

        //then
        greeting?.let {
            assertThat(greeting).isEqualTo("HEY님 안녕하세요!");
        }
    }

    @Test
    @DisplayName("slack event 내용에 멘션만 있으면 ()님 안녕하세요!를 출력해야 한다.")
    fun should_SayHello_when_OnlyMention() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "");

        //when
        val greeting:String? = slackService.customizeMsgByCondition(eventValue);

        //then
        greeting?.let {
            assertThat(greeting).isEqualTo("HEY님 안녕하세요!");
        }
    }

    @Test
    @DisplayName("slack event 내용에 인삿말 외에 다른 내용이 있으면 null 반환해야 한다.")
    fun should_not_greeting_when_NoHello() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "다른 말");

        //when
        val greeting:String? = slackService.customizeMsgByCondition(eventValue);

        //then
        assertThat(greeting).isNotEqualTo("HEY님 안녕하세요!");
    }

    @Test
    @DisplayName("멘션만 했거나 인삿말이 있을 때엔 true를 반환해야 한다.")
    fun shouldReturnTrue_when_OnlyMention_Or_IncludeGreeting() {
        //given
        val eventValue1 = getEventValue(EventType.APP_MENTION.type, "");
        val eventValue2 = getEventValue(EventType.APP_MENTION.type, "뭐하니");
        val text1 = eventValue1["text"]!!;
        val text2 = eventValue2["text"]!!;
        
        //when
        val isGreeting1:Boolean = slackService.isGreetingCondition(text1);
        val isGreeting2:Boolean = slackService.isGreetingCondition(text2);
        
        //then
        assertThat(isGreeting1).isTrue();
        assertThat(isGreeting2).isFalse();
    }

    @Test
    @DisplayName("event의 유형이 app_mention일 때, 그에 맞는 payload를 담아야 한다.")
    fun shouldContainBody_when_app_mention() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "안녕");

        //when
        val payload = slackService.getPayloadByType(eventValue);

        //then
        assertThat(payload).isEqualTo("{\"text\":\"HEY님 안녕하세요!\"}")
    }

    @Test
    @DisplayName("메세지에 날씨라는 단어가 들어있으면 날씨에 대한 정보를 전달한다")
    fun should() {
        //given
        val eventValue1 = getEventValue(EventType.APP_MENTION.type, "날씨")
        val eventValue2 = getEventValue(EventType.APP_MENTION.type, "뭐해")
        val text1 = eventValue1["text"]!!;
        val text2 = eventValue2["text"]!!;

        //when
        val isAskingWeather1:Boolean = slackService.isWeatherAskingCondition(text1);
        val isAskingWeather2:Boolean = slackService.isWeatherAskingCondition(text2);

        //then
        assertThat(isAskingWeather1).isTrue();
        assertThat(isAskingWeather2).isFalse();
    }
    
    
    





    fun getEventValue(type:String, text:String) : LinkedHashMap<String, String>{
        val eventValue = LinkedHashMap<String, String>();
        eventValue["client_msg_id"] = "12341234";
        eventValue["type"] = type;
        eventValue["user"] = "U05MVCYDKJL";
        eventValue["text"] = "<@U05MMBQ2AKD> $text";

        return eventValue;
    }
}