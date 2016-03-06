package org.beryx.viewreka.bundle.catalog

import groovy.sql.Sql
import spock.lang.Specification

class DummySpec extends Specification{
	void "test something"() {

        def url = "jdbc:derby:${System.properties['user.dir']}/db"
        def driver = 'org.apache.derby.jdbc.EmbeddedDriver'
        Sql sql = Sql.newInstance(url, '', '', driver)
        sql.eachRow('select * from ta_catalog') { row ->
            // println row.bundleClass
            println row
        }
        sql.close()

        expect:
        1 == 1
	}
}
