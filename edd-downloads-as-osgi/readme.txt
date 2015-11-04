=== EDD Downloads As Services ===
Contributors: C Gallen OpenNMS / Entimoss
Donate link: 
Tags: easy digital downloads, digital downloads, e-downloads, edd, services, e-commerce, ecommerce, cgallen, osgi
Requires at least: 3.3
Tested up to: 4.1
Stable tag: 0.9.0
License: GPLv2 or later
License URI: http://www.gnu.org/licenses/gpl-2.0.html

Allow OSGi Licence Bundles to be sold by Easy Digital Downloads

== Description ==

This plugin requires [Easy Digital Downloads](http://wordpress.org/extend/plugins/easy-digital-downloads/ "Easy Digital Downloads") v1.9 or greater. 

(Tested to Easy Digital Downloads version Version 2.3.3)

It also requires an OSGi licence manager to be running in a separate karaf instance

The plugin creates a new download and post type for OSGi licences and links purchases to licences.

Settings for this plugin are provided in the Easy Digital Downloads Settings Extensions page
This allows you to specify the URL, username and password for the OSGi licence generator
A Debug option is provided to help identifying problems with this plugin communicating with the OSGi licence generator

To view the licences owned by a user, you should create a My Licences page and include the
\[osgi_licence_list\] shortcode

To make a download generate a licence page you should click 'This download is an OSGi licenced module'
and enter the OSGi Product Id for the download in the field

To see the product metadata for a product you should add the following shortcode to your download page
\[osgi_product_description\]

This will upload the product metadata once. If you want to refresh the metadata every time the page is loaded use
\[osgi_product_description retrieve == "always"\]

In the wordpress settings>Permalink Settings you must choose 'Default http://localhost:8080/wordpress/?p=123'

Suggestions welcome for future features. 

(Thanks to Andrew Munro, Sumobi for the downloads as a service plugin which acted as a template for this work)

**Easy Digital Downloads theme**
This plugin has been tested with the following Shop Front theme from Andrew Munro, Sumobi

[http://wordpress.org/themes/shop-front/](http://wordpress.org/themes/shop-front/ "Shop Front")

**Stay up to date**
TBD

== Installation ==

1. Unpack the entire contents of this plugin zip file into your `wp-content/plugins/` folder locally
1. Upload to your site
1. Navigate to `wp-admin/plugins.php` on your site (your WP Admin plugin page)
1. Activate this plugin

(this plugin has not yet been contributed to word press so cannot be installed using the 'add new' plugin feature)

== Screenshots ==
TBD

== Upgrade Notice ==

= 0.9.0 =
Initial Release

== Changelog ==

= 0.9.0 =
* Initial Release

= 0.9 =
* Initial release