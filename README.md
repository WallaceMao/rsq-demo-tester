# grails(2.5.x) test demo
 
## 1 grails unit test（单元测试）
grails单元测试是针对编写的单个类方法进行测试，其所依赖的其他domain/service等需要使用Mock来模拟
grails单元测试一般由开发人员编写，对单个业务逻辑复杂的类进行

### 单元测试代码编写
参照unit包中 [com.rishiqing.demo.dao.TodoDaoServiceSpec.groovy](https://github.com/WallaceMao/rsq-demo-tester/blob/master/test/unit/com/rishiqing/demo/dao/TodoDaoServiceSpec.groovy)

### 执行单元测试
#### IDEA环境下
- 可以直接使用IDEA集成的jUnit runner来运行unit test的测试用例
- 可以直接在Run/Debug Configuration中配置jUnit的Runner，或者使用`Ctrl + Shift + F10`快捷键运行测试用例

#### grails环境下
命令行运行

    grails test-app --echoOut unit:

## 2 grails integration test（集成测试）
grails集成测试是模拟grails的context环境，使用spock进行测试，测试用例所依赖的domain/service等均可以正常使用，不再需要mock
grails集成测试一般由开发人员编写，对于依赖较多的controller和service，使用单元测试比较麻烦，可以直接进行集成测试
grails集成测试默认情况下对每个测试类开启transactional，提交的数据并不会保存到数据库，而是放在一个事务中，每次测试结束回滚事务

### 基本配置
#### DataSource.groovy

    test {
        dataSource {
            dbCreate = "update"
            driverClassName = "com.mysql.jdbc.Driver"
            url = "jdbc:mysql://localhost:3306/temp?autoReconnect=true&characterEncoding=utf-8&useSSL=false"
            username = "root"
            password = "root"
        }
    }

#### 集成测试代码编写
参照integration中[com.rishiqing.demo.TodoServiceIntegrationSpec.groovy](https://github.com/WallaceMao/rsq-demo-tester/blob/master/test/unit/com/rishiqing/demo/TodoServiceIntegrationSpec.groovy)

#### 执行集成测试
#### IDEA环境下（无效）
- 由于集成测试需要使用grails application context，**所以不能直接在IDEA中执行**

#### grails环境下
命令行运行

    grails test-app --echoOut integration:


## 3 grails functional test（功能测试）
grails功能测试是在grails的环境下启动functional test。使用Geb-spock的集成测试框架进行。

### 3.1 依赖
    dependencies {
        test "org.gebish:geb-spock:1.0"
        //  连接数据库的话需要使用MySQL驱动
        runtime "mysql:mysql-connector-java:5.1.39"
    }
    plugins {
        test 'org.grails.plugins:geb:1.0'
        //  由于要启动grails application，所以需要加上grails appliaction依赖的插件
        build ":tomcat:7.0.70"
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.8'
        compile ":asset-pipeline:2.5.7"
        runtime ":hibernate4:4.3.10"
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"
    }

### 3.2 基本配置
#### DataSource.groovy
在未启动grails application的情况下，functional test会自动启动grails application，启动时的数据源环境为test；
在已手动启动grails application的情况下，functional test只运行测试，数据源环境与手动启动的grails application环境有关

#### FunctionalTestConfig.groovy
用于定义环境变量的配置文件，示例：

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

可以通过`ConfigUtil`读取

#### GebConfig.groovy
Geb基本配置，用于配置Geb的option，示例：

    import org.openqa.selenium.chrome.ChromeDriver
    
    def path = "${new File('.').getAbsolutePath()}${File.separator}test${File.separator}functional${File.separator}chromedriver.exe"
    
    driver = {
        System.setProperty("webdriver.chrome.driver",
                path)
        new ChromeDriver()
    }
    
    //  自动清除cookie
    autoClearCookies = true
    
    baseNavigatorWaiting = true
    atCheckWaiting = true
    
    baseUrl = "${ConfigUtil.config.baseUrl}${ConfigUtil.config.path}"

### 3.3 api test（api测试）
使用grails的functional测试，执行api test
api测试不调用web driver，直接通过封装过的[Unirest库](http://unirest.io/java.html)调用url，并对返回值进行验证

#### 新增依赖：

    dependencies {
        //  Unirest库，用于发送rest请求
        test "com.mashape.unirest:unirest-java:1.4.9"
    }

#### api测试代码编写
参照[com.rishiqing.demo.api.TodoFunctionalSpec.groovy](https://github.com/WallaceMao/rsq-demo-tester/blob/master/test/unit/com/rishiqing/demo/api/TodoFunctionalSpec.groovy)

#### 执行api测试
运行测试的方式有两种：

##### IDEA环境下
- 由于功能测试可以不依赖grails application context，那么就可以直接使用IDEA集成的jUnit runner来运行api test的测试用例
- 可以直接在Run/Debug Configuration中配置jUnit的Runner，或者使用`Ctrl + Shift + F10`快捷键运行测试用例
- 如果需要配置不同环境，可以在Run/Debug Configuration的VM options中配置环境参数，例如

      -Dgeb.env=local

##### grails环境下
命令行运行

    grails test-app -Dgeb.env=local --echoOut functional: com.rishiqing.demo.api.**.*

进行api功能测试，参数说明：

- `--echoOut`：输出测试用例中的打印结果
- `-Dgev.env`：指定运行测试时的环境参数，默认为local，需要与FunctionalTestConfig.groovy中的环境参数一致
- `functional:`：运行功能测试
- `com.rishiqing.demo.api.**.*`：运行`com.rishiqing.demo.api`包及其子包中的所有测试用例

### 3.4 web functional test（web端功能测试）
调用web driver操作浏览器，执行浏览器环境下的功能测试

#### 新增依赖：

    dependencies {
        //  selenium 相应的web driver，用于驱动浏览器
        test "org.seleniumhq.selenium:selenium-chrome-driver:2.53.1"
        test "org.seleniumhq.selenium:selenium-support:2.53.1"
    }
    
#### web功能测试代码编写
参照[com.rishiqing.demo.web.TodoWebFunctionalSpec.groovy](https://github.com/WallaceMao/rsq-demo-tester/blob/master/test/unit/com/rishiqing/demo/web/TodoWebFunctionalSpec.groovy)

#### 执行dweb功能测试代码
运行测试的方式有两种：

##### IDEA环境下
- 由于功能测试可以不依赖grails application context，那么就可以直接使用IDEA集成的jUnit runner来运行web functional test的测试用例
- 可以直接在Run/Debug Configuration中配置jUnit的Runner，或者使用`Ctrl + Shift + F10`快捷键运行测试用例
- 如果需要配置不同环境，可以在Run/Debug Configuration的VM options中配置环境参数，geb.env默认为local例如

      -Dgeb.env=local

##### grails环境下
命令行运行

    grails test-app -Dgeb.env=local --echoOut functional: com.rishiqing.demo.web.**.*

进行api功能测试，参数说明：

- `--echoOut`：输出测试用例中的打印结果
- `-Dgev.env`：指定运行测试时的环境参数，默认为local，需要与FunctionalTestConfig.groovy中的环境参数一致
- `functional:`：运行功能测试
- `com.rishiqing.demo.web.**.*`：运行`com.rishiqing.demo.web`包及其子包中的所有测试用例


## 4 更多例子

1. 用户名密码验证：functional: [com.rishiqing.test.functional.rest.spec.account.ValidateUserSpec.groovy](https://github.com/WallaceMao/rsq-demo-tester/blob/master/test/functional/com/rishiqing/test/functional/rest/spec/account/ValidateUserSpec.groovy)
2. 微信登录：web functional: [com.rishiqing.test.functional.web.spec.WeixinLoginSpec.groovy](https://github.com/WallaceMao/rsq-demo-tester/blob/master/test/functional/com/rishiqing/test/functional/web/spec/WeixinLoginSpec.groovy)