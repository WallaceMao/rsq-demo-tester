package com.rishiqing.test.functional.web.page

import geb.Page

/**
 * 登录页面
 * Created by  on 2017/8/16.Wallace
 */
class LoginAndRegisterPage extends BasePage {
    static url = "/i"
    static at = {
        title == "日事清"
        $('div.mainContainer')
    }
    static content = {
        regPort { $('div#regPort') }
        regPhone { $('div#regPhone') }
        regEmail { $('div#regEmail') }
        loginView { $('div#loginView') }
        avatarLogin { $('div#avatarLogin') }
        invitePort { $('div#invitePort') }
        inviteLogin { $('div#inviteLogin') }
        inviteRegPhone { $('div#inviteRegPhone') }
        inviteRegEmail { $('div#inviteRegEmail') }
        resetPort { $('div#resetPort') }
        emailSend { $('div#emailSend') }
        emailReset { $('div#emailReset') }
        phoneReset { $('div#phoneReset') }
        resetSuccess { $('div#resetSuccess') }
        resetFail { $('div#resetFail') }
        emailSuccess { $('div#emailSuccess') }
        emailFail { $('div#emailFail') }
        canNotInGroup { $('div#canNotInGroup') }
        errorView { $('div#errorView') }

        weixinLoginButton { $('div#loginView i.iconWX') }
    }
}
