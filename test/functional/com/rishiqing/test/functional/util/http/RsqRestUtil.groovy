package com.rishiqing.test.functional.util.http

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.HttpRequest
import com.mashape.unirest.request.HttpRequestWithBody
import org.apache.http.HttpHost
import org.apache.http.client.CookieStore
import org.apache.http.cookie.Cookie
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.client.LaxRedirectStrategy
import org.apache.http.impl.cookie.BasicClientCookie

/**
 * Created by  on 2017/8/15.Wallace
 */
class RsqRestUtil {

    static CookieStore cookieStore

    static {
        config()
//        config([proxy: ['127.0.0.1': 5555]])
    }

    static void config(Map settings = [:]) {
        cookieStore = new BasicCookieStore()
        HttpClientBuilder builder = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultCookieStore(cookieStore)

        if(settings.proxy){
            Map.Entry entry = settings.proxy.find()
            builder.setProxy(new HttpHost(String.valueOf(entry.key), (int)entry.value))
        }

        Unirest.setHttpClient(builder.build())
    }

    static private HttpRequest handleHeader(HttpRequest request, RsqRestRequest delegateRequest){
        delegateRequest.headerMap.each {key, value ->
            request = request.header(String.valueOf(key), String.valueOf(value))
        }
        return request
    }

    static private HttpRequestWithBody handleBody(HttpRequestWithBody request, RsqRestRequest delegateRequest){
        request.fields(delegateRequest.body)
        return request
    }

    static private void handleCookie(RsqRestRequest delegateRequest){
        delegateRequest.cookies.each {it ->
            if(it instanceof Cookie){
                addCookie(it)
            }else if(it instanceof Map) {
                Cookie cookie = new BasicClientCookie("$it.name", "$it.value")
                cookie.domain = it.domain
                cookie.path = it.path
                addCookie(cookie)
            }else {
                throw new RuntimeException("cookie type not supported")
            }
        }
    }


    static RsqRestResponse post(String url, @DelegatesTo(RsqRestRequest) Closure closure){
        RsqRestRequest delegateRequest = new RsqRestRequest()
        def code = closure.rehydrate(delegateRequest, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()

        //  use Unirest to send request
        HttpRequestWithBody unirestReq = Unirest.post(url)

        handleHeader(unirestReq, delegateRequest)
        handleBody(unirestReq, delegateRequest)
        handleCookie(delegateRequest)

        HttpResponse<String> response = unirestReq.asString()

        RsqRestResponse resp = new RsqRestResponse(response)

        return resp
    }

    /**
     * 使用cookieStore来管理cookie，转发cookie相关的方法到cookieStore
     * @param cookie
     */
    static void addCookie(Cookie cookie) {
        cookieStore.addCookie(cookie)
    }

    /**
     * 使用cookieStore来管理cookie，转发cookie相关的方法到cookieStore
     * @return
     */
    static List<Cookie> getCookies() {
        return cookieStore.getCookies()
    }

    /**
     * 根据名称获取cookie
     * @param name
     * @return
     */
    static Cookie getCookieByName(String name) {
        return cookieStore.getCookies()?.find {it ->
            return it.name == name
        }
    }

    /**
     * 使用cookieStore来管理cookie，转发cookie相关的方法到cookieStore
     * @param date
     * @return
     */
    static boolean clearExpiredCookies(Date date) {
        return cookieStore.clearExpired(date)
    }

    /**
     * 使用cookieStore来管理cookie，转发cookie相关的方法到cookieStore
     */
    static void clearCookies() {
        cookieStore.clear()
    }


    public static void main(String[] args) {
        def cl = { String... param ->
            "${param[0]}------"
        }
        def showName = {
            println "----$name----"
        }


        def a = [1,2,3]

        def str = "----${->a}"

        println str

        a.add(4)

        a = [5]

        println str

//        showName.delegate = new Inner(name: 'Wallace')
//        println cl.call("ffff","dddd","oooo")
    }
}
