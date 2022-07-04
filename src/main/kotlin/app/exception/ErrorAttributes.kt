package app.exception

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest


@Component
class CustomErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(webRequest: WebRequest?, includeStackTrace: Boolean): MutableMap<String, Any> {
        val attributes = super.getErrorAttributes(webRequest, includeStackTrace)
        val error = getError(webRequest)
        if (error is CustomException) {  
            attributes["errorCode"] = error.error.errorCode
            attributes["status"] = error.error.httpCode
            webRequest!!.setAttribute("javax.servlet.error.status_code", error.error.httpCode, 0)
        }
        attributes.remove("error")
        return attributes
    }
}