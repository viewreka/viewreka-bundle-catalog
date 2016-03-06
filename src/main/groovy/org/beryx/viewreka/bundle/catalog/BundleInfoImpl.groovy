package org.beryx.viewreka.bundle.catalog

import groovy.transform.ToString
import org.beryx.viewreka.bundle.repo.BundleInfo
import org.beryx.viewreka.core.Version

@ToString
class BundleInfoImpl implements BundleInfo {
    String bundleClass
    int viewrekaVersionMajor
    int viewrekaVersionMinor
    int viewrekaVersionPatch
    List<String> categories
    String id
    String name
    Version version
    String description
    String url
    String homePage = ""

    String ownerId
    String ownerScreenName
    String ownerService
    String ownerProfileUrl = ""
    long createTime
    long lastUpdateTime
}
