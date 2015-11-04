<?php
/*
 * Plugin Name: Easy Digital Downloads - Downloads As OSGi licenced bundles
 * Plugin URI:
 * Description: Define downloads as "OSGi licenced bundles". Licenced bundles create a licence page on checkout whch allow a user to generate a licence for their OSGi application. A new licence post is created and linked to from the purchase confirmation page, and in the purchase receipt email
 * Version: 1.0.4
 * Author: Craig Gallen based upon edd-downloads-as-service v 1.0.4 by Andrew Munro, Sumobi
 * Author URI: http://craiggallen.com
 * License: GPL-2.0+
 * License URI: http://www.opensource.org/licenses/gpl-license.php
 */

// Exit if accessed directly
if (! defined ( 'ABSPATH' ))
	exit ();
	
	// load httpful.phar library
require_once ('lib/httpful.phar');

if (! class_exists ( 'EDD_Downloads_As_Osgi' )) {
	class EDD_Downloads_As_Osgi {
		
		// check if OSGi licence generator debugging is enabled
		private $osgipub_osgi_debug = false;
		private static $instance;
		
		/**
		 * Main Instance
		 *
		 * Ensures that only one instance exists in memory at any one
		 * time. Also prevents needing to define globals all over the place.
		 *
		 * @since 1.0
		 *       
		 */
		public static function instance() {
			// if (! isset ( self::$instance )) {
			// self::$instance = new self ();
			// }
			
			// return self::$instance;
			if (! self::$instance) {
				self::$instance = new EDD_Downloads_As_Osgi ();
			}
			return self::$instance;
		}
		
		/**
		 * Start your engines
		 *
		 * @since 1.0
		 *       
		 * @return void
		 */
		public function __construct() {
			// check if OSGi licence generator debugging is enabled
			$this->osgipub_osgi_debug = edd_get_option ( 'osgipub_osgi_debug' );
			
			$this->setup_globals ();
			$this->setup_actions ();
			$this->load_textdomain ();
		}
		
		/**
		 * Globals
		 *
		 * @since 1.0
		 *       
		 * @return void
		 */
		private function setup_globals() {
			// paths
			$this->file = __FILE__;
			$this->basename = apply_filters ( 'edd_osgi_plugin_basenname', plugin_basename ( $this->file ) );
			$this->plugin_dir = apply_filters ( 'edd_osgi_plugin_dir_path', plugin_dir_path ( $this->file ) );
			$this->plugin_url = apply_filters ( 'edd_osgi_plugin_dir_url', plugin_dir_url ( $this->file ) );
		}
		
		/**
		 * see https://wordpress.org/support/topic/custom-post-type-posts-not-displayed
		 *
		 * @param unknown $query        	
		 * @return unknown
		 */
		// function my_get_posts( $query ) {
		
		// if ( ( is_home() && $query->is_main_query() ) || is_feed() )
		// $query->set( 'post_type', array( 'post', 'page', 'osgi_licence_post' ) );
		
		// return $query;
		// }
		
		/**
		 * provides shortcodes for listing set of licences
		 * shortcodes [osgi_licence_list] lists only licences for current logged in user
		 * shortcodes [osgi_licence_list user_filter="all_users"] lists licences for all users
		 *
		 * @param unknown $atts        	
		 */
		public function osgi_licence_list_shortcode($atts) {
			$content = "";
			
			if(! is_user_logged_in ()){
				$content = "<p>You need to be logged in to see your licence list.</p>";
				return $content;
			}
			
			// default return only values for current user
			$args = array (
					'post_type' => 'osgi_licence_post',
					'meta_query' => array (
							array (
									'key' => 'edd_payment_user_id',
									'value' => get_current_user_id (),
									'compare' => '=' 
							) 
					) 
			);
			
			if ($this->osgipub_osgi_debug) {
				echo "debug: osgi_licence_list_shortcode var_dump (atts )</p>\n" . var_dump ( $atts );
			}
			
			// return all users licences if set to 'all_users'
			if (isset ( $atts ['user_filter'] ) && ($atts ['user_filter'] == "all_users")) {
				$args = array (
						'post_type' => 'osgi_licence_post' 
				);
			}
			
			// The Query
			$the_query = new WP_Query ( $args );
			
			// The Loop
			
			if ($the_query->have_posts ()) {
				$content .= "<TABLE>\n";
				$content .= "<TR>\n";
				$content .= "<TH>Licence</TH>\n";
				$content .= "<TH>Product ID</TH>\n";
				$content .= "<TH>Licencee</TH>\n";
				$content .= "</TR>\n";
				while ( $the_query->have_posts () ) {
					$the_query->the_post ();
					$edd_osgiLicencee = get_post_meta ( $the_query->post->ID, 'edd_osgiLicencee', true );
					$edd_osgiProductIdStr = get_post_meta ( $the_query->post->ID, 'edd_osgiProductIdStr', true );
					$content .= "<TR>\n";
					$content .= "<td><a href=\"" . get_permalink () . "\" >" . get_the_title () . "</a></td>\n";
					$content .= "<td>" . $edd_osgiProductIdStr . "</td>\n";
					$content .= "<td>" . $edd_osgiLicencee . "</td>\n";
					$content .= "</TR>\n";
				}
				
				$content .= "</TABLE>\n";
			} else {
				$content .= "<p>No Licences found</p>";
			}
			/* Restore original Post Data */
			wp_reset_postdata ();
			
			return $content;
		}
		
		/**
		 * Provides shortcodes for retrieving licence metadata from licence generator for use in product definition
		 * shortcodes [osgi_licence_metadata] retrieving licence specification from licence generator every time page viewed
		 *
		 * @param unknown $atts        	
		 */
		public function osgi_licence_metadata_shortcode($atts) {
			// set time metadata uploaded
			$objDateTime = new DateTime ( 'NOW' );
			$content = "";
			$content .= "<div id=\"osgi_licence_metadata_shortcode\" class=\"osgi_metadata\">\n";
			
			try {
				$edd_osgiProductIdStr = get_post_meta ( get_the_ID (), '_edd_osgiProductIdStr', true );
				if (! isset ( $edd_osgiProductIdStr )) {
					throw new Exception ( 'edd-osgi: You must set productId on the download definition' );
				}
				
				$user_can_modify_metadata = (is_user_logged_in () && current_user_can ( 'view_shop_sensitive_data' ));
				
				if ($this->osgipub_osgi_debug) {
					$content .= "<p>debug: Running osgi_licence_metadata_shortcode shortcode </p>\n";
					if ($user_can_modify_metadata) {
						$content .= "<p>debug: osgi_licence_metadata_shortcode: user permitted to modify licence metadata</p>\n";
					} else
						$content .= "<p>debug: osgi_licence_metadata_shortcode: user NOT permitted to modify licence metadata</p>";
					$content .= "<p>debug: Attributes:";
					if ("" != $atts)
						foreach ( $atts as $key => $value ) {
							$content .= "   " . $key . "=" . $value . "<br>\n";
						}
					else
						$content .= " Not Set";
					$content .= "</p>\n";
				}
				
				// If user can modify shop data then they will see licence metadata and can edit it
				// load supporting class
				$eddOsgiLicences = new EddOsgiLicences ();
				
				if ($user_can_modify_metadata) {
					
					// load unmodified metadata spec from licence manager
					$licenceMetadataSpecStr = $eddOsgiLicences->getLicenceMetadataSpec ( $edd_osgiProductIdStr );
					$osgiLicenceMetadataSpec = new SimpleXMLElement ( $licenceMetadataSpecStr );
					
					// try loading modified LicenceMetadataSpecStr from this post
					$edd_modified_osgiLicenceMetadataSpecStr = get_post_meta ( get_the_ID (), '_edd_modified_osgiLicenceMetadataSpecStr', true );
					// if no modified LicenceMetadataSpecStr in post then create new one
					if (! isset ( $edd_modified_osgiLicenceMetadataSpecStr ) || '' == $edd_modified_osgiLicenceMetadataSpecStr) {
						$edd_modified_osgiLicenceMetadataSpecStr = $licenceMetadataSpecStr;
						// set the update time
						$edd_osgiLicenceMetadataStrUpdateTime = $objDateTime->format ( DateTime::COOKIE );
						update_post_meta ( get_the_ID (), '_edd_osgiLicenceMetadataStrUpdateTime', $edd_osgiLicenceMetadataStrUpdateTime );
					}
					$edd_modified_osgiLicenceMetadataSpec = new SimpleXMLElement ( $edd_modified_osgiLicenceMetadataSpecStr );
					
					// check if request asks to reset the modified Licence Metadata Spec
					if (isset ( $_POST ['_resetLicenceMetadataSpec'] ) && $_POST ['_resetLicenceMetadataSpec']=='true') {
						// if reset then load unmodified metadata spec from licence manager
						$licenceMetadataSpecStr = $eddOsgiLicences->getLicenceMetadataSpec ( $edd_osgiProductIdStr );
						$osgiLicenceMetadataSpec = new SimpleXMLElement ( $licenceMetadataSpecStr );
						$edd_modified_osgiLicenceMetadataSpec=$osgiLicenceMetadataSpec;
						$payload = ( string ) $edd_modified_osgiLicenceMetadataSpec->asXML ();
						update_post_meta ( get_the_ID (), '_edd_modified_osgiLicenceMetadataSpecStr', $payload );
						// set the update time
						$edd_osgiLicenceMetadataStrUpdateTime = $objDateTime->format ( DateTime::COOKIE );
						update_post_meta ( get_the_ID (), '_edd_osgiLicenceMetadataStrUpdateTime', $edd_osgiProductMetadataStrUpdateTime );
					} elseif (isset ( $_POST ['_modifyLicenceMetadataSpec'] ) && $_POST ['_modifyLicenceMetadataSpec']=='true') {
						// check if request asks to modify the Licence Metadata Spec
			            // parse post data into modified licene metadata spec an update the post
						$edd_modified_osgiLicenceMetadataSpec =  $eddOsgiLicences->modifyLicenceMetadataFromPost($osgiLicenceMetadataSpec, $edd_modified_osgiLicenceMetadataSpec,  $_POST);
						$payload = ( string ) $edd_modified_osgiLicenceMetadataSpec->asXML ();
						update_post_meta ( get_the_ID (), '_edd_modified_osgiLicenceMetadataSpecStr', $payload );
						// set the update time
						$edd_osgiLicenceMetadataStrUpdateTime = $objDateTime->format ( DateTime::COOKIE );
						update_post_meta ( get_the_ID (), '_edd_osgiLicenceMetadataStrUpdateTime', $edd_osgiLicenceMetadataStrUpdateTime );
					}

					$noinput = false;

					// update time string is time when matadata last updated
					$edd_osgiLicenceMetadataStrUpdateTime = get_post_meta ( get_the_ID (), '_edd_osgiLicenceMetadataStrUpdateTime', true );
					if(isset($edd_osgiLicenceMetadataStrUpdateTime)){
						$content .= "<div style=\" line-height: 80%;\">Licence Metadata (Last Updated: " . $edd_osgiLicenceMetadataStrUpdateTime . ")</div>\n";
					}
					$content .="<form method=\"post\" action=\"\" enctype=\"multipart/form-data\">\n";
					$content .= $eddOsgiLicences->licenceMetadataForm ( $edd_modified_osgiLicenceMetadataSpec, $osgiLicenceMetadataSpec, $noinput );
					// set up form to update licence metadata
					$content .="<p>As a privileged user you can modify the standard metadata spec for this product instance</p>\n";
					$content .="<p>(e.g. you can change the options or duration of the licence)</p>\n";
					$content .="<input type=\"hidden\" name=\"_modifyLicenceMetadataSpec\" value=\"true\">\n";
					$content .="<button type=\"submit\">Update Licence Metadata Spec</button>\n";
					$content .="</form>\n";
					$content .="<form method=\"post\" action=\"\" enctype=\"multipart/form-data\">\n";
					$content .="<input type=\"hidden\" name=\"_resetLicenceMetadataSpec\" value=\"true\">\n";
					$content .="<button type=\"submit\">Reset Licence Metadata Spec</button>\n";
					$content .="</form>\n";
					
				}
			} catch ( Exception $e ) {
				$content .= "<p>" . "Exception in osgi_licence_metadata shortcode: Exception: " . $e->getMessage () . "</p>\n";
			}
			$content .= "</div> <!-- id=\"osgi_licence_metadata_shortcode\" -->\n";
			
			return $content;
		}
		
		/**
		 * provides shortcodes for retrieving product description from licence generator
		 * shortcodes [osgi_product_description] retrieving product description from licence generator every time page viewed
		 * shortcodes [osgi_licence_list retrieve="if_new"] only retreives a new product description the first time page is viewed
		 *
		 * @param unknown $atts        	
		 */
		public function osgi_product_description_shortcode($atts) {
			$content = "";
			$content .= "<div id=\"osgi_product_description_shortcode\" class=\"osgi_metadata\">\n";
			
			if ($this->osgipub_osgi_debug) {
				$content .= "<p>debug: Running osgi_product_description shortcode </p>\n";
				$content .= "<p>debug: Attributes:";
				if ("" != $atts)
					foreach ( $atts as $key => $value ) {
						$content .= "   " . $key . "=" . $value . "<br>\n";
					}
				else
					$content .= " Not Set";
				$content .= "</p>\n";
			}
			
			try {
				
				$edd_osgi_enabled = get_post_meta ( get_the_ID (), '_edd_osgi_enabled', true );
				if (! isset ( $edd_osgi_enabled ) || $edd_osgi_enabled != "1") {
					throw new Exception ( "edd-osgi: You must tick 'This download is an OSGi module on the download definition'" );
				}
				
				$edd_osgiProductIdStr = get_post_meta ( get_the_ID (), '_edd_osgiProductIdStr', true );
				if (! isset ( $edd_osgiProductIdStr )) {
					throw new Exception ( 'edd-osgi: You must set productId on the download definition' );
				}
				
				// get the base url URL of the OSGi licence generator service from settings
				$osgiLicenceGeneratorUrl = edd_get_option ( 'osgipub_osgi_licence_pub_url' );
				if (! isset ( $osgiLicenceGeneratorUrl ) || '' == $osgiLicenceGeneratorUrl) {
					throw new Exception ( 'edd-osgi: You must set the OSGi Licence Publisher URL in the plugin settings' );
				}
				
				// get the username to access the OSGi licence generator service from settings
				$osgi_username = edd_get_option ( 'osgipub_osgi_username' );
				if (! isset ( $osgi_username )) {
					throw new Exception ( 'edd-osgi: You must set the OSGi User Name in the plugin settings' );
				}
				
				// get the password to access of the OSGi licence generator service from settings
				$osgi_password = edd_get_option ( 'osgipub_osgi_password' );
				if (! isset ( $osgi_password )) {
					throw new Exception ( 'edd-osgi: You must set the OSGi Password in the plugin settings' );
				}
				
				// product descriptor string contains previously retreived product description but may not be set
				$edd_osgiProductMetadataStr = get_post_meta ( get_the_ID (), '_edd_osgiProductMetadataStr', true );
				
				// update time string is time when matadata last updated
				$edd_osgiProductMetadataStrUpdateTime = get_post_meta ( get_the_ID (), '_edd_osgiProductMetadataStrUpdateTime', true );
				
				if ($this->osgipub_osgi_debug) {
					$content .= "<p>";
					$content .= "debug: edd_osgi_enabled=" . $edd_osgi_enabled . "<br>\n";
					$content .= "debug: osgiLicenceGeneratorUrl=" . $osgiLicenceGeneratorUrl . "<br>\n";
					$content .= "debug: osgi_username=" . $osgi_username . "<br>\n";
					$content .= "debug: osgi_password=" . $osgi_password . "<br>\n";
					$content .= "debug: edd_osgiProductIdStr=" . $edd_osgiProductIdStr . "<br>\n";
					$content .= "debug: edd_osgiProductMetadataStrUpdateTimer=" . $edd_osgiProductMetadataStrUpdateTime . "<br>\n";
					$content .= "debug: edd_osgiProductMetadataStr=" . $edd_osgiProductMetadataStr . "<br>\n";
					$content .= "</p>";
				}
				
				$retrieve = false;
				
				// retrieve matadata from OSGI publisher if edd_osgiProductMetadataStr not populated
				if (! isset ( $edd_osgiProductMetadataStr ) || "" == $edd_osgiProductMetadataStr) {
					$retrieve = true;
				}
				
				// retrieve matadata from OSGI publisher if retrieve set to if_new (default)
				if (isset ( $atts ['retrieve'] ) && ($atts ['retrieve'] == "if_new")) {
					// do nothing - already set to retrieve once
				}
				
				// retrieve matadata from OSGI publisher if retrieve set to always
				if (isset ( $atts ['retrieve'] ) && ($atts ['retrieve'] == "always")) {
					$retrieve = true;
				}
				
				// only update product metadata in this product description if $retreive is true
				if ($retrieve) {
					if ($this->osgipub_osgi_debug)
						$content .= "<p>debug: retrieving product matadata</p>";
					
					$uri = $osgiLicenceGeneratorUrl . '/licencemgr/rest/product-pub/getproductspec?productId=' . $edd_osgiProductIdStr;
					
					if ($this->osgipub_osgi_debug)
						$content .= "<p>debug: Get Product Spec request to licence publisher: Basic Authentication\n" . "     username='" . $osgi_username . "' password='" . $osgi_password . "'\n" . "     uri='" . $uri . "</p>\n";
					
					$response = \Httpful\Request::get ( $uri )->authenticateWith ( $osgi_username, $osgi_password )->expectsXml ()->send ();
					
					if ($this->osgipub_osgi_debug) {
						$content .= "<p>debug: Response from licence publisher: Http response code='" . $response->code . "' response body:</p>\n";
						$content .= "<textarea>" . $response->body->asXML () . "</textarea>\n";
					}
					
					// if we cant talk to the licence generator error and leave page
					if ($response->code != 200) {
						$msg = 'null';
						$devmsg = 'null';
						$code = $response->code;
						if (isset ( $response->errorMessage )) {
							$devmsg = ( string ) $response->errorMessage->developerMessage;
							$msg = ( string ) $response->errorMessage->message;
						}
						throw new Exception ( "edd-osgi: Http error code='" . $code . "\n" . "     Cannot retrieve product specification from OSGi licence publisher url=' . $uri . '\n" . "     Reason=' . $msg . '\n" . "     Developer Message='" . $devmsg . "'\n" );
					}
					
					$edd_osgiProductMetadataStr = $response->body->productMetadata->asXML ();
					update_post_meta ( get_the_ID (), '_edd_osgiProductMetadataStr', $edd_osgiProductMetadataStr );
					
					// set time metadata uploaded
					$objDateTime = new DateTime ( 'NOW' );
					$edd_osgiProductMetadataStrUpdateTime = $objDateTime->format ( DateTime::COOKIE );
					update_post_meta ( get_the_ID (), '_edd_osgiProductMetadataStrUpdateTime', $edd_osgiProductMetadataStrUpdateTime );
				}
				
				// parse the product matadata specification we have just saved as a string
				$osgiProductMetadataSpec = new SimpleXMLElement ( $edd_osgiProductMetadataStr );
				
				$content .= "<table id=\"edd-osgi-productMetadata\" style=\"width: 100%;border: 3px solid;\" >\n";
				$content .= "<caption>Product Metadata (Last Updated: " . $edd_osgiProductMetadataStrUpdateTime . ")</caption>\n";
				foreach ( $osgiProductMetadataSpec->children () as $key => $value ) {
					$content .= "    <tr>\n";
					$content .= "    <td>" . $key . "</td>\n";
					$content .= "        <td>" . $value . "</td>\n";
					$content .= "    </tr>\n";
				}
				$content .= "</table>\n";
			} catch ( Exception $e ) {
				$content .= "<p>" . "Exception in osgi_product_description shortcode: Exception: " . $e->getMessage () . "</p>\n";
			}
			
			$content .= "</div> <!-- id=\"osgi_product_description_shortcode\" -->\n";
			return $content;
		}
		
		/**
		 * register post type for osgi_licence_post
		 */
		public function reg_post_type() {
			$args = array (
					'public' => true,
					'label' => 'osgi_licence_post',
					'description' => 'osgi licence posts which generate purchased licences',
					'menu_position' => null,
 					'capability_type' => 'post',
 					'capabilities' => array(
 							'create_posts' => 'do_not_allow', // Removes support for the "Add New" function, including Super Admin's in dashboard
 							//'edit_posts' => 'do_not_allow'    // too drastic - prevents admin listing any post of this type 
 					),
 					'map_meta_cap' => true // Set to false, if users are not allowed to edit/delete existing posts
			);
			register_post_type ( 'osgi_licence_post', $args );
		}
		
		/**
		 * Setup the default hooks and actions
		 *
		 * @since 1.0
		 *       
		 * @return void
		 */
		private function setup_actions() {
			global $edd_options;
			
			// error_log ( "CGALLEN CHECKING setupactions\n", 3, "C:\Bitnami\wordpress-4.1-0\apps\wordpress\my-errors.log" );
			// error_log ( "CGALLEN CHECKING setupactions", 0 );
			// ;
			
			// shortcodes [osgi_licence_list]
			add_shortcode ( 'osgi_licence_list', array (
					$this,
					'osgi_licence_list_shortcode' 
			) );
			
			// shortcodes [osgi_product_description]
			add_shortcode ( 'osgi_product_description', array (
					$this,
					'osgi_product_description_shortcode' 
			) );
			
			// shortcodes [osgi_licence_metadata]
			add_shortcode ( 'osgi_licence_metadata', array (
					$this,
					'osgi_licence_metadata_shortcode' 
			) );
			
			add_action ( 'init', array (
					$this,
					'reg_post_type' 
			) );
			
			/**
			 * see
			 * http://wordpress.stackexchange.com/questions/17385/custom-post-type-templates-from-plugin-folder
			 *
			 * Filter the single_template with our custom function
			 */
			add_filter ( 'single_template', array (
					$this,
					'my_custom_template' 
			) );
			
			/**
			 * see https://wordpress.org/support/topic/custom-post-type-posts-not-displayed
			 * see http://justintadlock.com/archives/2010/02/02/showing-custom-post-types-on-your-home-blog-page
			 */
			// add_filter( 'pre_get_posts', array (
			// $this,
			// 'my_get_posts'
			// ) );
			
			// metabox ( see easy-digital-downloads\includes\admin\downloads\metabox.php
			add_action ( 'edd_meta_box_settings_fields', array (
					$this,
					'add_metabox' 
			) );
			add_action ( 'edd_metabox_fields_save', array (
					$this,
					'save_metabox' 
			) );
			
			// actions added to checkout template
			
			// these only change the test surrounding the checkout button
			// add actions before checkout
			// add_action( 'edd_purchase_form_before_submit', array( $this, 'save_metabox' ) );
			
			// add actions after checkout
			// add_action( 'edd_purchase_form_after_submit', array( $this, 'save_metabox' ) );
			
			// hopefully adds content to the success page
			// SEE \easy-digital-downloads\includes\checkout\template.php
			// add_filter( 'the_content', array($this, 'edd_osgi_filter_success_page_osgi') );
			
			// see \easy-digital-downloads\templates\shortcode-receipt.php
			add_action ( 'edd_payment_receipt_after_table', array (
					$this,
					'edd_osgi_action_payment_receipt_after_table' 
			) );
			
			// add_action( 'edd_payment_receipt_after', array( $this, 'edd_osgi_action_payment_receipt_after_table' ) );
			
			// settings
			add_filter ( 'edd_settings_extensions', array (
					$this,
					'settings' 
			) );
			
			// filter each download
			add_filter ( 'edd_receipt_show_download_files', array (
					$this,
					'receipt' 
			), 10, 2 );
			add_filter ( 'edd_email_receipt_download_title', array (
					$this,
					'email_receipt' 
			), 10, 3 );
			
			do_action ( 'edd_osgi_setup_actions' );
		}
		
		/**
		 * see http://wordpress.stackexchange.com/questions/17385/custom-post-type-templates-from-plugin-folder
		 * Single template function which will choose our template
		 */
		public function my_custom_template($single) {
			global $post;
			
			// error_log ( "CGALLEN CHECKING debug post_type == osgi_licence_post\n", 3, "C:\Bitnami\wordpress-4.1-0\apps\wordpress\my-errors.log" );
			// error_log ( "CGALLEN CHECKING debug post_type == osgi_licence_post", 0 );
			;
			/* Checks for single template by post type */
			if ($post->post_type == "osgi_licence_post") {
				// echo 'CGALLEN debug post_type == osgi_licence_post';
				// error_log ( "CGALLEN true debug post_type == osgi_licence_post\n", 3, "C:\Bitnami\wordpress-4.1-0\apps\wordpress\my-errors.log" );
				
				// if (file_exists ( dirname ( __FILE__ ) . '/edd-osgilicences-template.php' ))
				// error_log ( "CGALLEN file exists\n", 3, "C:\Bitnami\wordpress-4.1-0\apps\wordpress\my-errors.log" );
				
				return dirname ( __FILE__ ) . '/edd-osgilicences-template.php';
			}
			return $single;
		}
		
		/**
		 *
		 * @param unknown $payment        	
		 * @param string $edd_receipt_args        	
		 */
		public function edd_osgi_action_payment_receipt_after_table($payment, $edd_receipt_args = null) {
			if ($this->osgipub_osgi_debug)
				echo "<p>debug: this is the action after table</p>\n";
			
			if (isset ( $payment ) && edd_is_payment_complete ( $payment->ID )) {
				
				$meta = get_post_meta ( $payment->ID );
				
				if ($this->osgipub_osgi_debug) {
					echo "<p>debug: Payment vardump=";
					var_dump ( $payment );
					echo "</p>\n";
					echo "<p>debug: Payment metadata vardump=";
					var_dump ( $meta );
					echo "</p>\n";
				}
				
				// same $cart = edd_get_payment_meta_cart_details( $payment->ID, true );
				$downloads = edd_get_payment_meta_cart_details ( $payment->ID, true );
				
				$edd_payment_post_id = $payment->ID;
				
				$edd_payment_user_id = ( string ) $meta ['_edd_payment_user_id'] [0];
				
				$edd_payment_customer_id = ( string ) $meta ['_edd_payment_customer_id'] [0];
				
				//TODO REMOVED BECAUSE NOT BEING GENERATED $edd_payment_number = ( string ) $meta ['_edd_payment_number'] [0];
				$edd_payment_number=$edd_payment_post_id;
				
				$edd_payment_purchase_key = ( string ) $meta ['_edd_payment_purchase_key'] [0];
				
				// see easy-digital-downloads/templates/history-downloads.php
				// add_query_arg( 'payment_key', edd_get_payment_key( $post->ID ), edd_get_success_page_uri() )
				if ($downloads) {
					
					$display_licence_table = false;
					
					// check if any downloads are osgi licenced
					foreach ( $downloads as $download ) {
						if ($this->is_osgi_licenced ( $download ['id'] ))
							$display_licence_table = true;
					}
					
					if ($display_licence_table) {
						echo "<div id=\"osgi_licence_list_table\" class=\"osgi_licence_list\">\n";
						echo "<h3>OSGi Licences</h3>\n";
						echo "<p>One of more of your purchased downloads have associated OSGi Licences.<BR>To generate your licences select the links below.</p>\n";
						echo "<table>\n";
					}
					
					// used to set change licence name for multiple downloads
					$download_no = 0;
					foreach ( $downloads as $download ) {
						// Skip over Bundles. Products included with a bundle will be displayed individually
						if (edd_is_bundled_product ( $download ['id'] ))
							continue;
							
							// if not osgi licenced bundle skip
						if (! ($this->is_osgi_licenced ( $download ['id'] )))
							continue;
						
						$download_no ++;
						
						$price_id = edd_get_cart_item_price_id ( $download );
						$download_files = edd_get_download_files ( $download ['id'], $price_id );
						$name = get_the_title ( $download ['id'] );
						
						// quantity used to handle multiple licences per download
						for($quantity = 1; $quantity <= $download ['quantity']; $quantity ++) {
							
							if (isset ( $edd_payment_number )) {
								// start of table row
								echo "  <tr>\n";
								echo "    <td>\n";
								
								// product id string from download
								// contains maven unique id of product to which this licence applies
								$edd_osgiProductIdStr = get_post_meta ( $download ['id'], '_edd_osgiProductIdStr', true );
								
								// try loading modified LicenceMetadataSpecStr from this product efinition and apply to licence post
								$edd_modified_osgiLicenceMetadataSpecStr = get_post_meta ( $download ['id'], '_edd_modified_osgiLicenceMetadataSpecStr', true );
								
								// Retrieve and append the price option name
								if (! empty ( $price_id )) {
									$name .= ' - ' . edd_get_price_option_name ( $download ['id'], $price_id, $payment->ID );
								}
								
								// product name - payment number - download number
								$licence_post_title = $name . ' - ' . $edd_payment_number . '-' . $download_no . '-' . $quantity;
								
								// remove whitepsace
								$licence_post_name = preg_replace ( '/\s+/', '', $licence_post_title );
								
								if ($this->osgipub_osgi_debug) {
									echo "<p>debug: download [id]=" . $download ['id'] . '</p>\n';
									if (! isset ( $edd_osgiProductIdStr )) {
										echo "<p>debug: from download edd_osgiProductIdStr not set for download [id]=" . $download ['id'] . '</p>\n';
									} else {
										echo "<p>debug: from download edd_osgiProductIdStr=";
										echo $edd_osgiProductIdStr;
										echo "</p>\n";
									}
									echo "<p>debug: payment name=";
									echo $name;
									echo "</p>\n";
									echo "<p>debug: licence_post_title=";
									echo $licence_post_title;
									echo "</p>\n";
									echo "<p>debug: licence_post_name=";
									echo $licence_post_name;
									echo "</p>\n";
									echo "<p>debug: edd_payment_number=";
									echo $edd_payment_number;
									echo "</p>\n";
								}
								
								$found_post = null;
								
								if ($posts = get_posts ( array (
										'name' => $licence_post_name,
										'post_type' => 'osgi_licence_post',
										'post_status' => 'publish',
										'posts_per_page' => 1 
								) )) {
									$found_post = $posts [0];
								}
								
								// Now, we can do something with $found_post
								if (! is_null ( $found_post )) {
									if ($this->osgipub_osgi_debug) {
										echo "<p>debug: we found the licence post=";
										echo $found_post->ID;
										echo "</p>\n";
									}
									echo '<a href="' . get_post_permalink ( $found_post->ID ) . '" >Link to Licence: ' . $licence_post_title . '</a>';
									echo "\n";
								} else {
									// get post with payment number metadata OR create post with metadata
									
									$post = array (
											// 'ID' => [ <post id> ] // Are you updating an existing post?
											'post_content' => '<p>DO NOT EDIT: You can only view or change this licence post by using View Post.</p>', // The full text of the post.
											'post_name' => $licence_post_name, // The name (slug) for your post
											'post_title' => $licence_post_title, // The title of your post.
											                                     // 'post_status' => [ 'draft' | 'publish' | 'pending'| 'future' | 'private' | custom registered status ] // Default 'draft'.
											'post_status' => 'publish', // Default 'draft'.
											'post_type' => 'osgi_licence_post',
											// 'post_type' => [ 'post' | 'page' | 'link' | 'nav_menu_item' | custom post type ] // Default 'post'.
											// 'post_author' => [ <user ID> ] // The user ID number of the author. Default is the current user ID.
											'ping_status' => 'closed', // Pingbacks or trackbacks allowed. Default is the option 'default_ping_status'.
											                           // 'post_parent' => [ <post ID> ] // Sets the parent of the new post, if any. Default 0.
											                           // 'menu_order' => [ <order> ] // If new post is a page, sets the order in which it should appear in supported menus. Default 0.
											                           // 'to_ping' => // Space or carriage return-separated list of URLs to ping. Default empty string.
											                           // 'pinged' => // Space or carriage return-separated list of URLs that have been pinged. Default empty string.
											                           // 'post_password' => [ <string> ] // Password for post, if any. Default empty string.
											                           // 'guid' => // Skip this and let Wordpress handle it, usually.
											                           // /'post_content_filtered' => // Skip this and let Wordpress handle it, usually.
											                           // /'post_excerpt' => [ <string> ] // For all your post excerpt needs.
											                           // 'post_date' => [ Y-m-d H:i:s ] // The time post was made.
											                           // 'post_date_gmt' => [ Y-m-d H:i:s ] // The time post was made, in GMT.
											'comment_status' => 'closed' 
									);
									// Default is the option 'default_comment_status', or 'closed'.
									// 'post_category' => [ array(<category id>, ...) ] // Default empty.
									// 'tags_input' => [ '<tag>, <tag>, ...' | array ] // Default empty.
									// 'tax_input' => [ array( <taxonomy> => <array | string> ) ] // For custom taxonomies. Default empty.
									// 'page_template' => '../edd-downloads-as-osgi.php' // Requires name of template file, eg template.php. Default empty.
									
									$newpost_id = wp_insert_post ( $post );
									
									// setting product id for licence
									// update_post_meta ( $newpost_id, 'edd_osgiProductIdStr', 'org.opennms.co.uk/org.opennms.co.uk.newfeature/0.0.1-SNAPSHOT' );
									update_post_meta ( $newpost_id, 'edd_osgiProductIdStr', $edd_osgiProductIdStr );
									
                                    // apply modified metadata to this licence post
									update_post_meta ( $newpost_id, '_edd_modified_osgiLicenceMetadataSpecStr', $edd_modified_osgiLicenceMetadataSpecStr );
									
									// setting customer metadata - not yet used in the template
									update_post_meta ( $newpost_id, 'edd_payment_customer_id', $edd_payment_customer_id );
									update_post_meta ( $newpost_id, 'edd_payment_user_id', $edd_payment_user_id );
									
									// setting edd_osgiLicencee information
									$f_name = ( string ) get_user_meta ( $edd_payment_user_id, 'first_name', true );
									$first_name = (isset ( $f_name ) ? $f_name : "");
									
									$l_name = ( string ) get_user_meta ( $edd_payment_user_id, 'last_name', true );
									$last_name = (isset ( $l_name ) ? $l_name : "");
									
									$address = "";
									$addr = edd_get_customer_address ( $edd_payment_user_id );
									if (isset ( $addr )) {
										$address = implode ( ", ", $addr );
									}
									
									$edd_osgiLicencee = $first_name . ", " . $last_name . ", " . $address;
									update_post_meta ( $newpost_id, 'edd_osgiLicencee', $edd_osgiLicencee );
									
									// for reverse lookup of post id of the associated payment
									update_post_meta ( $newpost_id, 'edd_payment_post_id', $edd_payment_post_id );
									
									if ($this->osgipub_osgi_debug) {
										echo "<p>debug: we created a new licence post=";
										echo $newpost_id;
										echo "</p>\n";
									}
									echo '<a href="' . get_post_permalink ( $newpost_id ) . '" >Link to Licence: ' . $licence_post_title . '</a>';
									echo "\n";
								}
								echo "    </td>\n";
								echo "  </tr>\n";
							}
						}
					}
					
					if ($display_licence_table) {
						echo "</table>\n";
						echo "</div> <!-- div id=osgi_licence_list_table -->";
					}
					;
				}
			} else
				echo '<p>payment not set</p>\n';
			
			if ($this->osgipub_osgi_debug) {
				if (isset ( $edd_receipt_args )) {
					echo "<p>debug: edd_receipt_args vardump=";
					var_dump ( $edd_receipt_args );
					echo "</p>\n";
				} else
					echo '<p>debug: edd_receipt_args not set</p>\n';
			}
		}
		
		/**
		 * TODO REMOVE
		 */
		// public function edd_osgi_filter_success_page_osgi($content) {
		// global $edd_options, $edd_receipt_args;
		
		// if (isset ( $edd_options ['success_page'] )) {
		// $content = $content . "<p>CRAIG TEST success_page</p>";
		// }
		// if (isset ( $_GET ['payment-confirmation'] )) {
		// $content = $content . "<p>CRAIG TEST payment-confirmation</p>";
		// }
		// if (is_page ( $edd_options ['success_page'] )) {
		// $content = $content . "<p>CRAIG TEST is_page success_page</p>";
		// }
		
		// if (isset ( $edd_options ['success_page'] ) && isset ( $_GET ['payment-confirmation'] ) && is_page ( $edd_options ['success_page'] )) {
		// // if ( has_filter( 'edd_payment_confirm_' . $_GET['payment-confirmation'] ) ) {
		// // $content = apply_filters( 'edd_payment_confirm_' . $_GET['payment-confirmation'], $content );
		// // }
		// $content = $content . "<p>CRAIG TEST ADDTIONAL CONTENT CHECKOUT</p>";
		// }
		
		// $anID = $edd_receipt_args ['id'];
		// $content = $content . "<p>CRAIG TEST edd_receipt_args[id]={$anID}</p>";
		
		// $the_payment = get_post ( $edd_receipt_args ['id'] );
		// $id = $the_payment->ID;
		// $content = $content . "<p>CRAIG TEST the_payment->ID={$the_payment->ID}</p>";
		
		// $my_value = get_post_meta ( $id, '_edd_payment_number', 1 );
		
		// $content = $content . "<p>CRAIG TEST my_value->$my_value</p>";
		
		// $sw_args = array (
		// 'meta_query' => array (
		// array (
		// 'key' => 'edd_osgi_licence',
		// 'value' => $my_value,
		// 'compare' => 'LIKE'
		// )
		// )
		// );
		// $query = new WP_Query ( $sw_args );
		// // if ( $the_query->have_posts() ) {
		// // echo '<h2>Films By Star Wards Directors</h2>';
		// // echo '<ul>';
		// // while ( $the_query->have_posts() ) {
		// // $the_query->the_post();
		// // echo '<li>' . get_the_title() . '</li>';
		// // }
		// // echo '</ul>';
		// // }
		// // /* Restore original Post Data */
		// // wp_reset_postdata();
		
		// return $content;
		// }
		
		/**
		 * TODO ADD LANGUAGE FILES ETC
		 * Loads the plugin language files
		 *
		 * @access public
		 * @since 1.0
		 * @return void
		 */
		public function load_textdomain() {
			// Set filter for plugin's languages directory
			$lang_dir = dirname ( plugin_basename ( $this->file ) ) . '/languages/';
			$lang_dir = apply_filters ( 'edd_osgi_languages_directory', $lang_dir );
			
			// Traditional WordPress plugin locale filter
			$locale = apply_filters ( 'plugin_locale', get_locale (), 'edd-osgi' );
			$mofile = sprintf ( '%1$s-%2$s.mo', 'edd-osgi', $locale );
			
			// Setup paths to current locale file
			$mofile_local = $lang_dir . $mofile;
			$mofile_global = WP_LANG_DIR . '/edd-downloads-as-osgi/' . $mofile;
			
			if (file_exists ( $mofile_global )) {
				load_textdomain ( 'edd-osgi', $mofile_global );
			} elseif (file_exists ( $mofile_local )) {
				load_textdomain ( 'edd-osgi', $mofile_local );
			} else {
				// Load the default language files
				load_plugin_textdomain ( 'edd-osgi', false, $lang_dir );
			}
		}
		
		/**
		 * TODO MOVE METABOX ENTRIES TO MAIN PANEL - TOO SMALL
		 * Add Metabox
		 *
		 * @since 1.0
		 */
		public function add_metabox($post_id) {
			$is_osgi_enabled = ( boolean ) get_post_meta ( $post_id, '_edd_osgi_enabled', true );
			$is_osgi_licence = ( boolean ) get_post_meta ( $post_id, '_edd_osgi_islicenced', true );
			$edd_osgiProductIdStr = ( string ) get_post_meta ( $post_id, '_edd_osgiProductIdStr', true );
			?>
<p>
	<strong><?php apply_filters( 'edd_osgi_header', printf( __( '%s As OSGi Licence:', 'edd-osgi' ), edd_get_label_singular() ) ); ?></strong>
</p>
<p>
	<label for="edd_download_as_osgi_enabled"> <input type="checkbox"
		name="_edd_osgi_enabled" id="edd_download_as_osgi_enabled" value="1"
		<?php checked( true, $is_osgi_enabled ); ?> />
					<?php apply_filters( 'edd_osgi_header_label', printf( __( 'This %s is an OSGi module', 'edd-osgi' ), strtolower( edd_get_label_singular() ) ) ); ?>
				</label>
</p>
<p>
	<label for="edd_download_as_osgi_licence"> <input type="checkbox"
		name="_edd_osgi_islicenced" id="edd_download_as_osgi_licence"
		value="1" <?php checked( true, $is_osgi_licence ); ?> />
					<?php apply_filters( 'edd_osgi_header_label', printf( __( 'This %s is an OSGi licenced module', 'edd-osgi' ), strtolower( edd_get_label_singular() ) ) ); ?>
				</label>
</p>
<p>
	<strong>Enter OSGi Product Id</strong>
</p>
<p>
	<input type="text" name="_edd_osgiProductIdStr"
		id="_edd_osgiProductIdStr"
		value="<?php echo $edd_osgiProductIdStr; ?>" />
</p>
<?php
		}
		
		/**
		 * Add to save function
		 *
		 * @param $fields Array
		 *        	of fields
		 * @since 1.0
		 * @return array
		 */
		public function save_metabox($fields) {
			$fields [0] = '_edd_osgi_enabled';
			$fields [1] = '_edd_osgi_islicenced';
			$fields [2] = '_edd_osgiProductIdStr';
			return $fields;
		}
		
		/**
		 * Prevent receipt from listing download files
		 *
		 * @param $enabled default
		 *        	true
		 * @param int $item_id
		 *        	ID of download
		 * @since 1.0
		 * @return boolean
		 */
		public function receipt($enabled, $item_id) {
			if ($this->is_osgi_licenced ( $item_id )) {
				return false;
			}
			
			return true;
		}
		
		/**
		 * Modify email template to remove dash if the item is a service
		 *
		 * @since 1.0
		 */
		// TODO CHANGE
		public function email_receipt($title, $item_id, $price_id) {
			if ($this->is_osgi_licenced ( $item_id )) {
				$title = get_the_title ( $item_id );
				
				if ($price_id !== false) {
					$title .= "&nbsp;" . edd_get_price_option_name ( $item_id, $price_id );
				}
			}
			
			return $title;
		}
		
		/**
		 * Is OSGi
		 *
		 * @param int $item_id
		 *        	ID of download
		 * @return boolean true if osgi licenced module, false otherwise
		 * @return boolean
		 */
		public function is_osgi_licenced($item_id) {
			global $edd_receipt_args, $edd_options;
			
			// get array of osgi categories
			// $osgi_categories = isset ( $edd_options ['edd_osgi_osgi_categories'] ) ? $edd_options ['edd_osgi_osgi_categories'] : '';
			
			// $term_ids = array ();
			
			// if ($osgi_categories) {
			// foreach ( $osgi_categories as $term_id => $term_name ) {
			// $term_ids [] = $term_id;
			// }
			// }
			
			$is_osgi_licenced = get_post_meta ( $item_id, '_edd_osgi_islicenced', true );
			
			// get payment
			// $payment = get_post ( $edd_receipt_args ['id'] );
			// $meta = isset ( $payment ) ? edd_get_payment_meta ( $payment->ID ) : '';
			// $cart = isset ( $payment ) ? edd_get_payment_meta_cart_details ( $payment->ID, true ) : '';
			
			// if ($cart) {
			// foreach ( $cart as $key => $item ) {
			// $price_id = edd_get_cart_item_price_id ( $item );
			
			// $download_files = edd_get_download_files ( $item_id, $price_id );
			
			// // if the service has a file attached, we still want to show it
			// if ($download_files)
			// return;
			// }
			// }
			
			// check if download has meta key or has a service term assigned to it
			// if ($is_osgi_licenced || has_term ( $term_ids, 'download_category', $item_id )) {
			// return true;
			// }
			
			if ($is_osgi_licenced) {
				return true;
			}
			
			return false;
		}
		
		/**
		 * Get terms
		 *
		 * @return array
		 * @since 1.0
		 */
		// public function get_terms() {
		// $args = array (
		// 'hide_empty' => false,
		// 'hierarchical' => false
		// );
		
		// $terms = get_terms ( 'download_category', apply_filters ( 'edd_osgi_get_terms', $args ) );
		
		// $terms_array = array ();
		
		// foreach ( $terms as $term ) {
		// $term_id = $term->term_id;
		// $term_name = $term->name;
		
		// $terms_array [$term_id] = $term_name;
		// }
		
		// if ($terms)
		// return $terms_array;
		
		// return false;
		// }
		
		/**
		 * Settings
		 *
		 * @since 1.0
		 */
		public function settings($settings) {
			$new_settings = array (
					array (
							'id' => 'edd_osgi_header',
							'name' => '<strong>' . __ ( 'Downloads As OSGi Licenced Bundles', 'edd-osgi' ) . '</strong>',
							'type' => 'header' 
					),
					// array (
					// 'id' => 'edd_osgi_service_categories',
					// 'name' => __ ( 'Select OSGi licence Categories', 'edd-osgi' ),
					// 'desc' => __ ( 'Select the categories that contain "OSGi Licences"', 'edd-osgi' ),
					// 'type' => 'multicheck',
					// 'options' => $this->get_terms ()
					// ),
					array (
							'id' => 'osgipub_osgi_licence_pub_url',
							'name' => __ ( 'Licence Publisher URL', 'edd' ),
							'desc' => __ ( 'Set the base URL of the OSGi licence publisher service. (do not append backslash / at end)', 'edd-osgi' ),
							'type' => 'text',
							'size' => 'regular',
							'std' => 'http://localhost:8181' 
					),
					array (
							'id' => 'osgipub_osgi_username',
							'name' => __ ( 'Licence Publisher Username', 'edd' ),
							'desc' => __ ( 'Set the username for the OSGi licence publisher service', 'edd-osgi' ),
							'type' => 'text',
							'size' => 'regular',
							'std' => 'admin' 
					),
					array (
							'id' => 'osgipub_osgi_password',
							'name' => __ ( 'Licence Publisher Password', 'edd' ),
							'desc' => __ ( 'Set the password for the OSGi licence publisher service', 'edd-osgi' ),
							'type' => 'text',
							'size' => 'regular',
							'std' => 'admin' 
					),
					array (
							'id' => 'osgipub_osgi_debug',
							'name' => __ ( 'Debug OSGI Plugin', 'edd' ),
							'desc' => __ ( 'Check this box to enable debugging messages for OSGi plugin.', 'edd-osgi' ),
							'type' => 'checkbox',
							'std' => '0' 
					) 
			);
			
			return array_merge ( $settings, $new_settings );
		}
	}
}

/**
 * Get everything running
 *
 * @since 1.0
 *       
 * @access private
 * @return void
 */
function edd_downloads_as_osgi() {
	$edd_downloads_as_osgi = new EDD_Downloads_As_Osgi ();
	
	// Load plugin class files
	require_once ('class-edd-osgilicences.php');
	$eddOsgiLicences = new EddOsgiLicences ();
}
add_action ( 'plugins_loaded', 'edd_downloads_as_osgi' );