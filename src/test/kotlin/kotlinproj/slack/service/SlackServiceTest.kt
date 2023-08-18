package kotlinproj.slack.service

import kotlinproj.Util.exception.BusinessException
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
        val eventMap = getEventValue("app_mention", "");

        //when
        slackService.sendMessageByWebhook(eventMap);

        //then
        Assertions.assertThatNoException();
    }
    
    @Test
    @DisplayName("api를 통해 user의 정보를 받아올 수 있다.")
    fun canExtract_userInfo_ByAPI() {
        //given
        val eventInfo = getEventValue("app_mention", "") as Map<String, String>;
        val user = eventInfo["user"]!!; // ex) U05MVCYDKJL
        
        //when
        val username:String = slackService.getSlackDisplayName(user);
        
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
    @DisplayName("slack event 내용에 인삿말이 있으면 ()님 안녕하세요!를 출력해야 한다.")
    fun should_SayHello_when_IncludeHello() {
        //given
        val eventValue = getEventValue("app_mention", "안녕");

        //when
        val greeting:String? = slackService.customizeMentionRes(eventValue);

        //then
        greeting?.let {
            assertThat(greeting).isEqualTo("HEY님 안녕하세요!");
        }
    }

    @Test
    @DisplayName("slack event 내용에 멘션만 있거나 ()님 안녕하세요!를 출력해야 한다.")
    fun should_SayHello_when_OnlyMention() {
        //given
        val eventValue = getEventValue("app_mention", "");

        //when
        val greeting:String? = slackService.customizeMentionRes(eventValue);

        //then
        greeting?.let {
            assertThat(greeting).isEqualTo("HEY님 안녕하세요!");
        }
    }

    @Test
    @DisplayName("slack event 내용에 인삿말 외에 다른 내용이 있으면 null 반환해야 한다.")
    fun should_not_greeting_when_NoHello() {
        //given
        val eventValue = getEventValue("app_mention", "다른 말");

        //when
        val greeting:String? = slackService.customizeMentionRes(eventValue);

        //then
        assertThat(greeting).isNotEqualTo("HEY님 안녕하세요!");
    }

    @Test
    @DisplayName("멘션만 했거나 인삿말이 있을 때엔 true를 반환해야 한다.")
    fun shouldReturnTrue_when_OnlyMention_Or_IncludeGreeting() {
        //given
        val eventValue1 = getEventValue("app_mention", "");
        val eventValue2 = getEventValue("app_mention", "뭐하니");
        val text1 = eventValue1["text"]!!;
        val text2 = eventValue2["text"]!!;
        
        //when
        val isGreeting1:Boolean = slackService.isGreetingCondition(text1);
        val isGreeting2:Boolean = slackService.isGreetingCondition(text2);
        
        //then
        assertThat(isGreeting1).isTrue();
        assertThat(isGreeting2).isFalse();
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