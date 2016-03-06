package org.beryx.viewreka.bundle.catalog
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool
import groovy.json.JsonOutput
import groovy.sql.Sql
import org.beryx.viewreka.core.Version
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import java.sql.Connection

@RestController
@SpringBootApplication
public class CatalogApplication {
    static final JDBCConnectionPool jdbcConnectionPool
    static {
        String dbPath = System.getProperty("bundle.db") ?: System.getProperty("user.dir") + "/db"
        String url = "jdbc:derby:$dbPath"
        jdbcConnectionPool = new SimpleJDBCConnectionPool("org.apache.derby.jdbc.EmbeddedDriver", url, "", "", 5, 100)
    }

    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args)
    }

    @RequestMapping(path = "/json", produces = "application/json; charset=UTF-8")
    String json() {
        def catalogEntries = []
        Connection connection
        Sql sql
        try {
            connection = jdbcConnectionPool.reserveConnection()
            sql = new Sql(connection)
            sql.eachRow('select * from ta_catalog') { row ->
                BundleInfoImpl entry = new BundleInfoImpl()
                catalogEntries << entry
                entry.bundleClass = row.BUNDLECLASS
                entry.viewrekaVersionMinor = row.VIEWREKAVERSIONMAJOR as int
                entry.viewrekaVersionMinor = row.VIEWREKAVERSIONMINOR as int
                entry.viewrekaVersionPatch = row.VIEWREKAVERSIONPATCH as int
                entry.categories = row.CATEGORIES.split('\\s*,\\s*')
                entry.id = row.BUNDLEID
                entry.name = row.BUNDLENAME
                entry.version = new Version(row.BUNDLEVERSIONMAJOR, row.BUNDLEVERSIONMINOR, row.BUNDLEVERSIONPATCH, row.BUNDLEVERSIONLABEL, row.BUNDLEVERSIONRELEASEBUILD)
                entry.description = row.BUNDLEDESCRIPTION
                entry.url = row.BUNDLEURL
                entry.homePage = row.HOMEPAGE

                entry.ownerId = row.OWNERID
                entry.ownerScreenName = row.OWNERSCREENNAME
                entry.ownerService = row.OWNERSERVICE
                entry.ownerProfileUrl = row.OWNERPROFILEURL ?: ""
                entry.createTime = row.CREATETIME as long
                entry.lastUpdateTime = row.LASTUPDATETIME as long
            }
        } finally {
            sql?.close()
            jdbcConnectionPool.releaseConnection(connection)
        }
        JsonOutput.prettyPrint(JsonOutput.toJson([entries: catalogEntries]))
    }

}
