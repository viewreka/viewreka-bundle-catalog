package org.beryx.viewreka.bundle.catalog
import com.vaadin.data.Item
import com.vaadin.data.util.converter.DateToLongConverter
import com.vaadin.data.util.filter.And
import com.vaadin.data.util.sqlcontainer.SQLContainer
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool
import com.vaadin.data.util.sqlcontainer.query.TableQuery
import com.vaadin.navigator.Navigator
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui.*
import com.vaadin.ui.renderers.DateRenderer
import org.beryx.viewreka.bundle.oauth.BaseManagerView
import org.beryx.viewreka.bundle.oauth.OAuthButton.User
import org.beryx.viewreka.bundle.repo.BundleInfo
import org.beryx.viewreka.bundle.repo.BundleReader
import org.vaadin.dialogs.ConfirmDialog
import org.vaadin.resetbuttonfortextfield.ResetButtonForTextField

import java.sql.SQLException
import java.sql.SQLIntegrityConstraintViolationException
import java.text.SimpleDateFormat

import static com.vaadin.data.util.filter.Compare.Equal

class ManagerView extends BaseManagerView {
    static HEADER_CAPTIONS = [
            ID: "ROWID",
            BUNDLEID: "Bundle ID",
            BUNDLEVERSIONMAJOR: "Version Major",
            BUNDLEVERSIONMINOR: "Version Minor",
            BUNDLEVERSIONPATCH: "Version Patch",
            BUNDLEVERSIONLABEL: "Version Label",
            BUNDLEVERSIONRELEASEBUILD: "Release Build",
            BUNDLENAME: "Bundle Name",
            BUNDLEDESCRIPTION: "Description",
            BUNDLEURL: "Bundle URL",
            BUNDLECLASS: "Bundle Class",
            VIEWREKAVERSIONMAJOR: "Viewreka Version Major",
            VIEWREKAVERSIONMINOR: "Viewreka Version Minor",
            VIEWREKAVERSIONPATCH: "Viewreka Version Patch",
            CATEGORIES: "Categories",
            HOMEPAGE: "Home Page",
            OWNERID: "Owner ID",
            OWNERSCREENNAME: "Owner Screen Name",
            OWNERSERVICE: "Authentication",
            OWNERPROFILEURL: "Owner Profile",
            CREATETIME: "Created",
            LASTUPDATETIME: "Last Update",
    ]

    ManagerView(final Navigator navigator, User user) {
        super(navigator, user)

        HorizontalLayout headerLayout = new HorizontalLayout()
        headerLayout.width = "100%"
        viewLayout.addComponent(headerLayout)

        Label lbTitle = new Label("Viewreka bundle manager")
        lbTitle.addStyleName("v-label-huge")
        headerLayout.addComponent(lbTitle)

        VerticalLayout accountLayout = new VerticalLayout()
        headerLayout.addComponent(accountLayout)

        Label lbUser = new Label(user.screenName)
        lbUser.setWidthUndefined()
        accountLayout.addComponent(lbUser)
        accountLayout.setComponentAlignment(lbUser, Alignment.TOP_RIGHT)

        Button butLogout = new Button("Logout", (Button.ClickListener){ event -> logout()})
        accountLayout.addComponent(butLogout)
        accountLayout.setComponentAlignment(butLogout, Alignment.BOTTOM_RIGHT)

        viewLayout.addComponent(new Label(" ", ContentMode.PREFORMATTED))

        SQLContainer container
        try {
            JDBCConnectionPool pool = CatalogApplication.jdbcConnectionPool
            TableQuery tq = new TableQuery("TA_CATALOG", pool, new DerbySQLGenerator())

            tq.versionColumn = "ID"
            container = new SQLContainer(tq)
            container.addContainerFilter(new And(
                    new Equal("OWNERID", user.id),
                    new Equal("OWNERSERVICE", user.service)))
        } catch (SQLException e) {
            throw new RuntimeException(e)
        }

        Panel gridPanel = new Panel("$user.screenName's bundles")
        viewLayout.addComponent(gridPanel)
        VerticalLayout gridPanelLayout = new VerticalLayout()
        gridPanelLayout.spacing = true
        gridPanel.content = gridPanelLayout

        final grid = new Grid()
        grid.containerDataSource = container
        grid.heightMode = HeightMode.ROW
        gridPanelLayout.addComponent(grid)
        grid.setSizeFull()
        grid.setSelectionMode(Grid.SelectionMode.SINGLE)

        "".inject("", {})

        grid.frozenColumnCount = 5
        grid.columnReorderingAllowed = true
        grid.columns.each { col -> col.hidable = true }
        ["ID", "OWNERID", "OWNERSCREENNAME", "OWNERSERVICE", "OWNERPROFILEURL"].each { colId -> grid.getColumn(colId).hidden = true}

        HEADER_CAPTIONS.each {col, caption -> grid.getColumn(col).setHeaderCaption(caption)}

        ["CREATETIME", "LASTUPDATETIME"].each {col ->
            grid.getColumn(col).setRenderer(new DateRenderer(new SimpleDateFormat("yyyy.MM.dd HH:mm")), new DateToLongConverter())
        }

        Label lbNoBundles = new Label("You have registered no bundles so far.")
        lbNoBundles.addStyleName("v-label-large")
        viewLayout.addComponent(lbNoBundles)

        refresh(gridPanel, grid, container, lbNoBundles)

        final Button butDelete = new Button("Delete", (Button.ClickListener){ event ->
            def selectedItemId = grid.selectedRow
            if(selectedItemId != null) {
                Item item = container.getItem(selectedItemId)
                println "item of type ${item.getClass().getName()}: $item"
                ConfirmDialog.show(UI.current, "Delete Viewreka Bundle", "Do you really want to delete the selected bundle?",
                        "Yes", "No", (ConfirmDialog.Listener){dialog ->
                    if(dialog.confirmed) {
                        container.removeItem(selectedItemId)
                        try {
                            container.commit()
                        } catch (SQLException e) {
                            Notification.show("Cannot commit changes", e.toString(), Notification.Type.ERROR_MESSAGE)
                            container.rollback()
                        } finally {
                            refresh(gridPanel, grid, container, lbNoBundles)
                        }
                    }
                })
            }
        })
        butDelete.enabled = false
        grid.addSelectionListener {event -> butDelete.enabled = event.selected}
        gridPanelLayout.addComponent(butDelete)

        viewLayout.addComponent(new Label(" ", ContentMode.PREFORMATTED))

        Panel newBundlePanel = new Panel("Add Bundle")
        viewLayout.addComponent(newBundlePanel)
        viewLayout.setExpandRatio(newBundlePanel, 1.0f)
        FormLayout newBundleLayout = new FormLayout()
        newBundleLayout.spacing = true
        newBundleLayout.margin = true
        newBundlePanel.content = newBundleLayout

        TextField txtUrl = new TextField("Bundle URL")
        txtUrl.description = "The URL from which the bundle can be downloaded"
        txtUrl.required = true
        txtUrl.columns = 40
        ResetButtonForTextField.extend(txtUrl)
        txtUrl.immediate = true
        newBundleLayout.addComponent(txtUrl)

        TextField txtHomePage = new TextField("Home page")
        txtHomePage.description = "The URL of this bundle's home page"
        txtHomePage.columns = 40
        ResetButtonForTextField.extend(txtHomePage)
        txtHomePage.immediate = true
        newBundleLayout.addComponent(txtHomePage)

        Button butAddBundle = new Button("Add")
        butAddBundle.enabled = false
        butAddBundle.setWidthUndefined()
        newBundleLayout.addComponent(butAddBundle)

        txtUrl.addTextChangeListener {event -> butAddBundle.enabled = event.text}

        butAddBundle.addClickListener {event ->
            if(txtUrl.value) {
                BundleInfo info
                try {
                    URL url = new URL(txtUrl.value)
                    def bundle
                    (bundle, info) = new BundleReader().loadBundle(url)
                } catch (Exception e) {
                    e.printStackTrace()
                    Notification.show("Cannot load bundle", e.toString(), Notification.Type.ERROR_MESSAGE)
                }
                if(!info) return;
                try {
                    def itemId = container.addItem();
                    Item item = container.getItemUnfiltered(itemId)

                    item.getItemProperty("BUNDLEID").setValue(info.id)
                    item.getItemProperty("BUNDLEVERSIONMAJOR").setValue(info.version.major)
                    item.getItemProperty("BUNDLEVERSIONMINOR").setValue(info.version.minor)
                    item.getItemProperty("BUNDLEVERSIONPATCH").setValue(info.version.patch)
                    item.getItemProperty("BUNDLEVERSIONLABEL").setValue(info.version.label ?: "")
                    item.getItemProperty("BUNDLEVERSIONRELEASEBUILD").setValue(info.version.releaseBuild)
                    item.getItemProperty("BUNDLENAME").setValue(info.name)
                    item.getItemProperty("BUNDLEDESCRIPTION").setValue(info.description ?: "")
                    item.getItemProperty("BUNDLEURL").setValue(info.url)
                    item.getItemProperty("BUNDLECLASS").setValue(info.bundleClass)
                    item.getItemProperty("VIEWREKAVERSIONMAJOR").setValue(info.viewrekaVersionMajor)
                    item.getItemProperty("VIEWREKAVERSIONMINOR").setValue(info.viewrekaVersionMinor)
                    item.getItemProperty("VIEWREKAVERSIONPATCH").setValue(info.viewrekaVersionPatch)
                    item.getItemProperty("CATEGORIES").setValue(info.categories.join(','))
                    item.getItemProperty("HOMEPAGE").setValue(txtHomePage.value ?: "")
                    item.getItemProperty("OWNERID").setValue(user.id)
                    item.getItemProperty("OWNERSCREENNAME").setValue(user.screenName ?: user.id)
                    item.getItemProperty("OWNERSERVICE").setValue(user.service)
                    item.getItemProperty("OWNERPROFILEURL").setValue(user.publicProfileUrl ?: "")
                    long time = new Date().time
                    item.getItemProperty("CREATETIME").setValue(time)
                    item.getItemProperty("LASTUPDATETIME").setValue(time)

                    container.commit()
                } catch (Exception e) {
                    e.printStackTrace()
                    def errMsg = (e instanceof SQLIntegrityConstraintViolationException) ? "Bundle $info.id $info.version already exists" : e.toString()
                    Notification.show("Cannot store bundle data", errMsg, Notification.Type.ERROR_MESSAGE)
                    container.rollback()
                } finally {
                    refresh(gridPanel, grid, container, lbNoBundles)
                }
            }
        }
    }

    static void refresh(Panel gridPanel, Grid grid, SQLContainer container, Label lbNoBundles) {
        gridPanel.visible = container.size() > 0
        lbNoBundles.visible = !gridPanel.visible
        grid.setHeightByRows Math.max(1, Math.min(5, container.size()))
    }
}
