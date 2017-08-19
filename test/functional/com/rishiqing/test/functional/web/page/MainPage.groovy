package com.rishiqing.test.functional.web.page

import geb.Page

/**
 * 登录之后的主页面
 * Created by  on 2017/8/16.Wallace
 */
class MainPage extends BasePage {
    static url = "/app/todo"
    static at = {
        title == "日事清"
        $('body#mainBody')
    }
    static content = {
        navBar(wait: true, required: true) { $('#navigator') }
        mainContent(wait: true, required: true) { $('#mainContent') }
        userSettingButton(wait: true, required: true) { $('.user-profile .dropdown-toggle') }
        logoutButton(wait: true, required: true) { $('.userSettings li:last-child a.dropdown-item') }
    }

    def logout(){
        userSettingButton.click()
        logoutButton.click()
    }
}
