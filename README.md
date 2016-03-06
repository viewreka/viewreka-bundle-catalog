# viewreka-bundle-catalog

Viewreka bundles (aka vbundles) are software components that add new features (such as new types of data sources, charts or parameters) to the [Viewreka](https://github.com/viewreka/viewreka) application. In order to choose the vbundles they want to install, Viewreka users must know which vbundles are available. Furthermore, Viewreka has to know how to install the desired vbundles. This information is provided by vbundle catalogs.       
 
**viewreka-bundle-catalog** is a web application that provides a vbundle catalog and allows vbundle creators to register and manage their vbundles. One instance of this application runs at [http://viewreka-bundles.beryx.org/](http://viewreka-bundles.beryx.org/) and provides the default catalog used by Viewreka.

The application is written in Java and Groovy and uses Spring Boot and Vaadin.

User authentication is performed using OAuth with GitHub, LinkedIn, Google or Microsoft.
If you want to run your own instance of viewreka-bundle-catalog, you have to create 
an OAuth application for at least one of the above providers. After that, you must configure the corresponding authentication file using the gradle task *configureXXXAuth*, where XXX is the provider name (e.g., `configureGitHubAuth`).
 