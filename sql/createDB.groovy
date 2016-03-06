@Grab('org.apache.derby:derby:10.12.1.1')
@GrabConfig(systemClassLoader=true)

import groovy.sql.Sql

def defaultJsonUrl = 'http://viewreka-bundles.beryx.org/json'

def cli = new CliBuilder(usage: "${getClass().name}.groovy [options] <dbName> [<jsonUrl>]")
cli.with {
    h longOpt: 'help', 'Show usage information'
    d longOpt: 'directory', args: 1, argName: 'dir', 'The directory where the database should be stored'
    o longOpt: 'overwrite', 'Overwrite existing database with the same name'
    u longOpt: 'url', "Link to the json catalog used to populate the DB\n(default: $defaultJsonUrl)"
}


def options = cli.parse(args)

if (!options) {
    return
}

if (!options.arguments() || options.h) {
    cli.usage()
    return
}


def dbName = options.arguments()[0]
def dbFile = options.d ? new File(options.d, dbName) : new File(dbName)
def dbPath = dbFile.path.replaceAll('\\\\', '/')

if(dbFile.exists()) {
    if(!options.o) {
        def answer = System.console().readLine "$dbPath already exist. Delete? [y/n] "
        if(answer.toLowerCase() != 'y') return
    }
    def deleted = dbFile.isDirectory() ? dbFile.deleteDir() : dbFile.delete()
    if(!deleted) {
        println "Failed to delete $dbFile"
        return
    }
}


// System.properties['derby.stream.error.field'] = 'java.lang.System.err'

def driver = 'org.apache.derby.jdbc.EmbeddedDriver'
def createUrl = "jdbc:derby:$dbPath;create=true"

println "Connection string: $createUrl"

def user = ''
def password = ''
def sql = Sql.newInstance(createUrl, user, password, driver)

def scriptName = 'create_schema.sql'
def scriptStream = getClass().getResourceAsStream('create_schema.sql')
if(!scriptStream) {
    println "Cannot find $scriptName"
    return
}
// SQL statements should be separated by lines containing only the / character
def stmts = scriptStream.text.normalize().split('\\n/\\s*\\n')
stmts.each { stmt ->
    stmt = stmt.trim()
    if(stmt.endsWith(';')) stmt = stmt.substring(0, stmt.length() - 1)
    println "Executing: $stmt"
    sql.execute(stmt)
}

def jsonUrl = (options.arguments().size() >= 2) ? options.arguments()[1] : defaultJsonUrl
Map catalog = new groovy.json.JsonSlurper().parse(new URL(jsonUrl))
catalog.entries.each { Map entry ->
    println "Inserting ${entry.id} ${entry.version}..."
    sql.executeInsert """
        insert into TA_CATALOG (
            bundleId,
            bundleVersionMajor,
            bundleVersionMinor,
            bundleVersionPatch,
            bundleVersionLabel,
            bundleVersionReleaseBuild,
            bundleName,
            bundleDescription,
            bundleUrl,
            bundleClass,
            viewrekaVersionMajor,
            viewrekaVersionMinor,
            viewrekaVersionPatch,
            categories,
            homePage,
            ownerId,
            ownerScreenName,
            ownerService,
            ownerProfileUrl,
            createTime,
            lastUpdateTime
        ) values (
            ${entry.id},
            ${entry.version.major},
            ${entry.version.minor},
            ${entry.version.patch},
            ${entry.version.label},
            ${entry.version.releaseBuild},
            ${entry.name},
            ${entry.description},
            ${entry.url},
            ${entry.bundleClass},
            ${entry.viewrekaVersionMajor},
            ${entry.viewrekaVersionMinor},
            ${entry.viewrekaVersionPatch},
            ${entry.categories.join(',')},
            ${entry.homePage},
            ${entry.ownerId},
            ${entry.ownerScreenName},
            ${entry.ownerService},
            ${entry.ownerProfileUrl},
            ${entry.createTime},
            ${entry.lastUpdateTime}
        )
        """
}

sql.close()
println "Done"
