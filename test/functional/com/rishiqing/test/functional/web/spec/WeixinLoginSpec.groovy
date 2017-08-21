package com.rishiqing.test.functional.web.spec

import com.rishiqing.test.functional.util.WebJs
import com.rishiqing.test.functional.web.page.IndexPage
import com.rishiqing.test.functional.web.page.LoginAndRegisterPage
import com.rishiqing.test.functional.web.page.MainPage
import geb.Browser
import geb.spock.GebSpec
import spock.lang.Ignore

/**
 * 微信登录
 * Created by  on 2017/8/16.Wallace
 */
@Ignore
class WeixinLoginSpec extends GebSpec {
    def setup(){}
    def cleanup(){}

    def "test weixin login in index page"() {
        when:
        to IndexPage
        String info = "-------请将鼠标移动到本页面的二维码处，并扫描--------"
        webJs.showTestInfo(info)
        webJs.addAlert('#qr-login')
        println info

        then:
        waitFor(20, 0.5){
            at MainPage
            logout()
            at IndexPage
        }
    }

    def "test weixin login in login page"() {
        when:
        to IndexPage
        loginButton.click()

        then:
        at LoginAndRegisterPage

        expect:
        withNewWindow({ weixinLoginButton.click() }){
            String info = "--------请用手机扫描页面的二维码--------"
            new WebJs(js).showTestInfo(info)
            println info
            waitFor(20, 0.5) {
                at MainPage
                logout()
                at IndexPage
            }
        }
    }
}
