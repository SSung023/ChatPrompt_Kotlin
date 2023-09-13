package kotlinproj.slack.service

import com.slack.api.model.block.LayoutBlock
import kotlinproj.Util.exception.BusinessException
import kotlinproj.Util.log.Logger
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
        assertThat(payload).isNotEqualTo("")
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
    @DisplayName("slack event 내용에 인삿말이 있으면 ()님 안녕하세요!가 담긴 LayoutBlock을 반환해야 한다.")
    fun should_SayHello_when_IncludeHello() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "안녕");

        //when
        val blockList:List<LayoutBlock> = slackService.customizeBlocks(eventValue);

        //then
        Logger.log.info { blockList[0].type }
    }

    @Test
    @DisplayName("slack event 내용에 멘션만 있으면 ()님 안녕하세요!를 출력해야 한다.")
    fun should_SayHello_when_OnlyMention() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "");

        //when
        slackService.customizeBlocks(eventValue);

        //then
    }

    @Test
    @DisplayName("slack event 내용에 인삿말 외에 다른 내용이 있으면 null 반환해야 한다.")
    fun should_not_greeting_when_NoHello() {
        //given
        val eventValue = getEventValue(EventType.APP_MENTION.type, "다른 말");

        //when
        slackService.customizeBlocks(eventValue);

        //then
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

    @Test
    @DisplayName("date/time picker 상호작용을 했을 때 date/time 정보를 추출할 수 있다.")
    fun canGet_weatherInfo() {
        //given

        //when

        //then
    }

    /**
     * ApiService에서 WeatherService, DateInfoService를 의존하고 있음
     * ApiService는 데이터를 저장하는데에 Weather, DateInfoService를 사용하는 것
     * Slack을 통해서 요청을 받기 때문에 총괄하는 서비스는 SlackService로 두고,
     * SlackService에서 필요할 때에 다른 Service들을 호출하는 것
     */
    @Test
    @DisplayName("현재보다 이후의 날씨 데이터가 없을 때 api를 호출하여 날씨 데이터를 받아온다.")
    fun getWeatherInfo_when_DataDontExist() {
        //given
        val payload = "{\"type\":\"block_actions\",\"user\":{\"id\":\"U05MVCYDKJL\",\"username\":\"adrians7206\",\"name\":\"adrians7206\",\"team_id\":\"T05M85AHW68\"},\"api_app_id\":\"A05MPS568QL\",\"token\":\"stG1dx0Mux3RzPSH2kAPXftX\",\"container\":{\"type\":\"message\",\"message_ts\":\"1694410721.729709\",\"channel_id\":\"C05N03Y8XL1\",\"is_ephemeral\":false},\"trigger_id\":\"5868638938550.5722180608212.db86ed658e60fda4f28197b9efe0e845\",\"team\":{\"id\":\"T05M85AHW68\",\"domain\":\"heys-workspace\"},\"enterprise\":null,\"is_enterprise_install\":false,\"channel\":{\"id\":\"C05N03Y8XL1\",\"name\":\"\\uc624\\ub298-\\ub0a0\\uc528-\\uc5b4\\ub54c\"},\"message\":{\"type\":\"message\",\"subtype\":\"bot_message\",\"text\":\":sloth: *\\ub098\\ubb34\\ub298\\ubd07* \\uc5d0\\uac8c \\ub0a0\\uc528\\ub97c \\ubb3c\\uc5b4\\ubcf4\\uc138\\uc694! \\n \\ub0a0\\uc528\\ub97c \\uc54c\\uace0 \\uc2f6\\uc740 *\\ub0a0\\uc9dc* \\uc640 *\\uc2dc\\uac04* \\uc744 \\uace8\\ub77c\\uc8fc\\uc138\\uc694! \\ub0a0\\uc528 \\uc815\\ubcf4\\ub294 \\ud55c \\uc2dc\\uac04 \\ub2e8\\uc704\\ub85c \\ub098\\ub220\\uc11c \\uc54c\\ub824\\ub4dc\\ub824\\uc694. :slightly_smiling_face: Click me button, with interactive elements\",\"ts\":\"1694410721.729709\",\"bot_id\":\"B05N4B0D1GD\",\"blocks\":[{\"type\":\"section\",\"block_id\":\"HFSUR\",\"text\":{\"type\":\"mrkdwn\",\"text\":\":sloth: *\\ub098\\ubb34\\ub298\\ubd07* \\uc5d0\\uac8c \\ub0a0\\uc528\\ub97c \\ubb3c\\uc5b4\\ubcf4\\uc138\\uc694! \\n \\ub0a0\\uc528\\ub97c \\uc54c\\uace0 \\uc2f6\\uc740 *\\ub0a0\\uc9dc* \\uc640 *\\uc2dc\\uac04* \\uc744 \\uace8\\ub77c\\uc8fc\\uc138\\uc694! \\ub0a0\\uc528 \\uc815\\ubcf4\\ub294 \\ud55c \\uc2dc\\uac04 \\ub2e8\\uc704\\ub85c \\ub098\\ub220\\uc11c \\uc54c\\ub824\\ub4dc\\ub824\\uc694. :slightly_smiling_face:\",\"verbatim\":false}},{\"type\":\"actions\",\"block_id\":\"Blpk\",\"elements\":[{\"type\":\"datepicker\",\"action_id\":\"q=klh\",\"initial_date\":\"2023-09-11\",\"placeholder\":{\"type\":\"plain_text\",\"text\":\"Select a date :date:\",\"emoji\":true}},{\"type\":\"timepicker\",\"action_id\":\"1MoXn\",\"initial_time\":\"14:38\",\"placeholder\":{\"type\":\"plain_text\",\"text\":\"Select time :stopwatch:\",\"emoji\":true}},{\"type\":\"button\",\"action_id\":\"get-weather\",\"text\":{\"type\":\"plain_text\",\"text\":\"Click me\",\"emoji\":true}}]}]},\"state\":{\"values\":{\"Blpk\":{\"q=klh\":{\"type\":\"datepicker\",\"selected_date\":\"2023-09-11\"},\"1MoXn\":{\"type\":\"timepicker\",\"selected_time\":\"14:38\"}}}},\"response_url\":\"https:\\/\\/hooks.slack.com\\/actions\\/T05M85AHW68\\/5898270396256\\/0VzpKn6RjzugxArAJtBM3Gv0\",\"actions\":[{\"action_id\":\"get-weather\",\"block_id\":\"Blpk\",\"text\":{\"type\":\"plain_text\",\"text\":\"Click me\",\"emoji\":true},\"type\":\"button\",\"action_ts\":\"1694413091.207996\"}]}\n";

        //when
        val (date, time) = slackService.getDateTimeFromBlockKit(payload)

        //then
        assertThat(date).isEqualTo("2023-09-11")
        assertThat(time).isEqualTo("14:38")
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
