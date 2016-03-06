package org.beryx.viewreka.bundle.catalog

import groovy.transform.ToString

@ToString
class CatalogEntry extends BundleInfoImpl {
    String ownerId
    String ownerScreenName
    String ownerService
    String ownerProfileUrl
    long createTime
    long lastUpdateTime
}
