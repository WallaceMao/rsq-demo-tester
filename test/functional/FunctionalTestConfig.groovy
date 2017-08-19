//  默认环境为local，需要在启动测试时通过-Dgeb.env=prod指定使用prod环境

environments {
    //  default env is local
    local {
        baseUrl = "http://beta.rishiqing.com/"
        path = "task/"
        validateUser {

        }
    }
    prod {
        baseUrl = "https://www.rishiqing.com/"
        path = "task/"
    }
}