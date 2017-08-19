package com.rishiqing.test.integration

import grails.test.spock.IntegrationSpec

class DemoUserIntegrationSpec extends IntegrationSpec {
    def grailsApplication

    def setup() {
    }

    def cleanup() {
    }

    void "test something true"() {
        expect:
        grailsApplication.config.rsq.test.functional.path == '/task'
    }
}
