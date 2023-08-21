package kotlinproj.Util.filter

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class CorsFilter: Filter {

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig?) {
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val req: HttpServletRequest = request as HttpServletRequest;
        val res: HttpServletResponse = response as HttpServletResponse;


        //여기에 내가 허용하고자 하는 클라이언트의 url을 입력해 줍니다.
        //주의사항 "https://myurl.com/" 처럼 마지막에 '/'를 붙이면 CORS에러가 그대로 발생하게 됩니다.
        res.setHeader("Access-Control-Allow-Origin", "*") //이렇게 해서 모든 요청에 대해서 허용할 수도 있습니다.
        //res.setHeader("Access-Control-Allow-Origin", "https://accounts.kakao.com");

        res.setHeader("Access-Control-Allow-Credentials", "true")
        res.setHeader("Access-Control-Allow-Methods", "*")
        res.setHeader("Access-Control-Max-Age", "3600")
        res.setHeader(
            "Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization, Set-Cookie"
        )

        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
        } else {
            chain!!.doFilter(req, res)
        }
    }

}