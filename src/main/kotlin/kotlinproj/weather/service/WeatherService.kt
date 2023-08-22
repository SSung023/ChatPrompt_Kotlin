package kotlinproj.weather.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author HeeYeon
 * @description
 * 기상청 단기예보 API 사용해서 기상청으로부터 날씨 정보를 받아옴
 */
@Service
@Transactional(readOnly = true)
class WeatherService {

    @Value("\${kma.callback-url}")
    lateinit var url:String;
    @Value("\${kma.service-key}")
    lateinit var serviceKey:String;


    /**
     * 기상청
     * url 변동 사항: base_date, base_time, nx, ny
     */
    fun searchWeather(): String {
        val baseDate = getBaseDate();
        val baseTime = getBaseTime(LocalTime.now());

        val urlBuilder = getCallbackURL(12, baseDate, baseTime, 60, 126);

        val url = URL(urlBuilder);
        val conn = url.openConnection() as HttpURLConnection;
        conn.requestMethod= "GET";
        conn.setRequestProperty("Content-type", "application/json");

        val rd = BufferedReader(InputStreamReader(conn.inputStream));
        val sb = java.lang.StringBuilder();
        var line: String?
        while (rd.readLine().also { line = it } != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        val result = sb.toString();

        return result;
    }




    /**
     * request url에 들어갈 base_date를 구함
     * -> 현재의 날짜를 yyyyMMdd 형식으로 바꾸는 코드
     */
    fun getBaseDate(): String {
        val curTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        return curTime.format(formatter);
    }

    /**
     * request url에 들어갈 base_time을 구함
     * -> 현재 시간에서 30분을 뺀 후, 범위에 맞는 base_time을 구한 후 반환
     */
    fun getBaseTime(curTime:LocalTime): String {
        val hours = curTime.hour;
        val minutes = curTime.minute;
        var convertedHour = "";

        if (minutes < 30) {
            convertedHour = (hours - 1).toString() + "00";
        }else{
            convertedHour = hours.toString() + "00";
        }

        return when(convertedHour){
            "200", "300", "400" -> "0200";
            "500", "600", "700" -> "0500";
            "800", "900", "1000" -> "0800";
            "1100", "1200", "1300" -> "1100";
            "1400", "1500", "1600", -> "1400";
            "1700", "1800", "1900" -> "1700";
            "2000", "2100", "2200"-> "2000";
            "2300", "2400", "000", "0000", "100"-> "2300";

            else -> {"0000"}
        }
    }

    // 기상청 단기예보 request url 생성
    fun getCallbackURL(numOfRaws: Int, baseDate: String, baseTime: String, nx: Int, ny: Int): String {
        val urlBuilder = StringBuilder(url).also {
            it.append("?serviceKey=$serviceKey")
                .append("&dataType=JSON")
                .append("&numOfRaws=$numOfRaws")
                .append("&pageNo=1")
                .append("&base_date=$baseDate&base_time=$baseTime")
                .append("&nx=$nx&ny=$ny")
        };
        return urlBuilder.toString();
    }




}