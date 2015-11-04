<?php
// Exit if accessed directly
if (! defined ( 'ABSPATH' ))
	exit ();

/**
 * class-edd-osgilicences.php
 * Class for manipulating OSGI licences and metadata.
 * uses httpful.phar library
 */
class EddOsgiLicences {
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
		// return self::$instance;
		if (! self::$instance) {
			self::$instance = new self ();
		}
		return self::$instance;
	}
	
	/**
	 * Constructor
	 *
	 * @since 1.0
	 *       
	 * @return void
	 */
	public function __construct() {
		
	}
	
	/**
	 * Constructs a table of inputs for licenceMetadata
	 *
	 * @param SimpleXMLElement $_licenceMetadata        	
	 * @param SimpleXMLElement $_licenceMetadataSpec        	
	 * @param boolean $noinput
	 *        	if true all inputs are readonly
	 * @return string
	 */
	function licenceMetadataForm($_licenceMetadata, $_licenceMetadataSpec, $noinput) {
		// check if OSGi licence generator debugging is enabled
		$osgipub_osgi_debug = edd_get_option ( 'osgipub_osgi_debug' );
	
		$MetadataFormStr = "";
		$MetadataFormStr .= "<table id=\"edd-osgi-licenceMetadata\" style=\"width: 100%;border: 3px solid;\" >\n";
		$metadataspec = ( array ) $_licenceMetadataSpec->children ();
		$licenceMetadataSpec_xpath = new DOMXPath ( dom_import_simplexml ( $_licenceMetadataSpec )->ownerDocument );
		foreach ( $_licenceMetadata->children () as $key => $value ) {
			if (($key != "options") && ($key != "systemIds")) {
				$readOnly = ($noinput || ('' != ( string ) $metadataspec [$key]));
				$MetadataFormStr .= "    <tr>\n";
				if ($readOnly) {
					$MetadataFormStr .= "    <td>" . $key . "</td>\n";
				} else {
					$MetadataFormStr .= "    <td>" . $key . " <bold>**</bold></td>\n"; // ** indicates editable field
				}
				$MetadataFormStr .= "        <td>\n";
				$MetadataFormStr .= "            <input type=\"text\" id=\"" . $key . "\"  name=\"" . $key . "\" value=\"" . $value . "\" ";
				if ($readOnly) {
					$MetadataFormStr .= " readonly";
				}
				$MetadataFormStr .= ">\n";
				$MetadataFormStr .= "        </td>\n";
				$MetadataFormStr .= "    </tr>\n";
			}
		}
		// **************
		// systemIds fields
		// **************
		$MetadataFormStr .= "    <tr>\n";
		$MetadataFormStr .= "        <td><u><bold>systemIds</bold></u></td>\n";
		$MetadataFormStr .= "    </tr>\n";
		$MetadataFormStr .= "    <tr>\n";
		$MetadataFormStr .= "        <td>\n";
		$MetadataFormStr .= "        </td>\n";
		$MetadataFormStr .= "        <td>\n";
		// creates an option list in which all systemIds are listed
		$MetadataFormStr .= "           <select id=\"systemIdsList\" size=\"10\" ";
			// do not allow user to change field if no editing allowed
			if ($noinput) {
				$MetadataFormStr .= " disabled";
			} else {
				// javascript to update systemIdInput
				$MetadataFormStr .= " onchange=\"document.getElementById('systemIdInput').value=document.getElementById('systemIdsList').value\" ";
		    }
		$MetadataFormStr .= " >\n";
		foreach ( $_licenceMetadata->xpath ( '//systemIds/systemId' ) as $item ) {
			$systemIdStr = ( string ) $item;
			$MetadataFormStr .= "              <option value=\"" . $systemIdStr . "\">" . $systemIdStr . "</option>\n";
		}
		$MetadataFormStr .= "           </select>\n";
		$MetadataFormStr .= "        </td>\n";
		$MetadataFormStr .= "        </tr>\n";
			// do not allow user to change field if no editing allowed
		if (! $noinput) {
		    // embedded scripts to add and remove systemId's from list
			$MetadataFormStr .= "<script>\n";
			$MetadataFormStr .= "function removeSelectedSystemId(){\n";
			$MetadataFormStr .= "	var systemIdsList=document.getElementById('systemIdsList');\n";
			$MetadataFormStr .= "	var systemIdInput=document.getElementById('systemIdInput');\n";
			$MetadataFormStr .= "	document.getElementById('errorField1').innerHTML='';\n";
			$MetadataFormStr .= "   if (systemIdInput.value != null && systemIdInput.value !='' ) {\n";
			$MetadataFormStr .= "     for (var i=0; i<systemIdsList.length; i++){\n";
			$MetadataFormStr .= "        if (systemIdsList.options[i].value==systemIdInput.value ) {\n";
			$MetadataFormStr .= "           systemIdsList.remove(i);\n";
			$MetadataFormStr .= "        }\n";
			$MetadataFormStr .= "     }\n";
			$MetadataFormStr .= "     systemIdInput.value='';\n";
			$MetadataFormStr .= "   }\n";
			$MetadataFormStr .= "   updateAllSystemIds()\n";
			$MetadataFormStr .= "}\n";
			$MetadataFormStr .= "\n";
			$MetadataFormStr .= "function addNewSystemId(){\n";
			$MetadataFormStr .= "	var systemIdsList=document.getElementById('systemIdsList');\n";
			$MetadataFormStr .= "	var systemIdInput=document.getElementById('systemIdInput');\n";
			$MetadataFormStr .= "	var maxSizeSystemIdsStr = document.getElementById('maxSizeSystemIds').value;\n";
			$MetadataFormStr .= "	var maxSizeSystemIds = parseInt(maxSizeSystemIdsStr);\n";
			$MetadataFormStr .= "   document.getElementById('errorField1').innerHTML='';\n";
			$MetadataFormStr .= "	if(maxSizeSystemIds==0) {;\n";
			$MetadataFormStr .= "      document.getElementById('errorField1').innerHTML=' you cannot add systemId if maxSizeSystemIds==0 ';\n";
			$MetadataFormStr .= "   }else if(systemIdsList.length>=maxSizeSystemIds) {\n";
			$MetadataFormStr .= "      document.getElementById('errorField1').innerHTML=' you cannot add more systemId entries than maxSizeSystemIds ('+maxSizeSystemIds+')';\n";
			$MetadataFormStr .= "   } else if ((systemIdInput.value != null) && (systemIdInput.value !='') && (systemIdInput.value.indexOf(',') == -1)) {\n";
			$MetadataFormStr .= "	   var alreadyInlist=false;\n";
			$MetadataFormStr .= "      for (var i=0; i<systemIdsList.length; i++){\n";
			$MetadataFormStr .= "         if (systemIdsList.options[i].value==systemIdInput.value ) {\n";
			$MetadataFormStr .= "            alreadyInlist=true;\n";
			$MetadataFormStr .= "         }\n";
			$MetadataFormStr .= "      }\n";
			$MetadataFormStr .= "      if (! alreadyInlist ) {\n";
			$MetadataFormStr .= "         var opt = document.createElement('option');\n";
			$MetadataFormStr .= "         var systemIdInput=document.getElementById('systemIdInput');\n";
			$MetadataFormStr .= "         opt.value = systemIdInput.value;\n";
			$MetadataFormStr .= "         opt.innerHTML = systemIdInput.value;\n";
			$MetadataFormStr .= "         systemIdsList.appendChild(opt);\n";
			$MetadataFormStr .= "         document.getElementById('systemIdInput').value='';\n";
			$MetadataFormStr .= "      }  else document.getElementById('errorField1').innerHTML=' value is already in list';\n";
			$MetadataFormStr .= "   }\n";
			$MetadataFormStr .= "   updateAllSystemIds()\n";
			$MetadataFormStr .= "}\n";
			$MetadataFormStr .= "function updateAllSystemIds(){\n";
			$MetadataFormStr .= "	var systemIdsList=document.getElementById('systemIdsList');\n";
			$MetadataFormStr .= "	var allSystemIdsStr='';\n";
			$MetadataFormStr .= "   for (var i=0; i<systemIdsList.length; i++){\n";
			$MetadataFormStr .= "      if (systemIdsList.options[i].value !='') { // don't add empty strings \n";
			$MetadataFormStr .= "         allSystemIdsStr= allSystemIdsStr +systemIdsList.options[i].value;\n";
			$MetadataFormStr .= "         if ((i+1)<systemIdsList.length) allSystemIdsStr = allSystemIdsStr+',';\n";
			$MetadataFormStr .= "      }\n";
			$MetadataFormStr .= "   }\n";
			$MetadataFormStr .= "   document.getElementById('allSystemIds').value=allSystemIdsStr;\n";
			$MetadataFormStr .= "}\n";
			$MetadataFormStr .= "</script>\n";
		    $MetadataFormStr .= "    <tr>\n";
			$MetadataFormStr .= "        <td>\n";
			$MetadataFormStr .= "        </td>\n";
			$MetadataFormStr .= "        <td>\n";
			$MetadataFormStr .= "           <div style=\"display:inline-block;\">\n";
		    $MetadataFormStr .= "              <input type=\"hidden\" id=\"allSystemIds\" name=\"allSystemIds\" value=\"\" > <!--hidden value used in POST to send systemIds -->\n";
			$MetadataFormStr .= "              <input type=\"text\" id=\"systemIdInput\" value=\"\" placeholder=\"New or Selected systemId\"> <!--no name defined so value is not sent in post -->\n";
			$MetadataFormStr .= "              <button type=\"button\" onclick=\"removeSelectedSystemId()\">Remove selected systemId</button>\n";
			$MetadataFormStr .= "              <button type=\"button\" onclick=\"addNewSystemId()\">Add new systemId</button>\n";
			$MetadataFormStr .= "              <div  style=\"color: red;\" id=\"errorField1\"></div>\n";
			$MetadataFormStr .= "           </div>\n";
			$MetadataFormStr .= "        </td>\n";
			$MetadataFormStr .= "    </tr>\n";
		}
		// **************
		// options fields
		// **************
		$MetadataFormStr .= "    <tr>\n";
		$MetadataFormStr .= "        <td><u><bold>Options</bold></u></td>\n";
		$MetadataFormStr .= "    </tr>\n";
		foreach ( $_licenceMetadata->xpath ( '//options' ) as $item ) {
			// check if field can be edited
			// do not allow user to change field if populated in metadata spec
			$name = ( string ) $item->option->name;
			$keyNodesMs = $licenceMetadataSpec_xpath->evaluate ( "//option[name='" . $name . "']/value" );
			$keyNodeMs = $keyNodesMs->item ( 0 );
			$editfield = true;
			if ('' != ( string ) $keyNodeMs->nodeValue) {
				$editfield = false;
			}
			
			$MetadataFormStr .= "    <tr>\n";
			if ($noinput || ! $editfield) {
				$MetadataFormStr .= "        <td>" . $item->option->name . "</td>\n";
			} else {
				$MetadataFormStr .= "    <td>" . $item->option->name . " <bold>**</bold></td>\n"; // ** indicates editable field
			}
			$MetadataFormStr .= "        <td>\n";
			$MetadataFormStr .= "            <input type=\"text\" name=\"" . $item->option->name . "\" value=\"" . $item->option->value . "\" ";
			
			// do not allow user to change field if populated in metadata spec
			if ($noinput || ! $editfield) {
				$MetadataFormStr .= " readonly";
			}
			$MetadataFormStr .= ">\n";
			$MetadataFormStr .= "        </td>\n";
			$MetadataFormStr .= "        <td>" . $item->option->description . "</td>\n";
			$MetadataFormStr .= "    </tr>\n";
		}
		$MetadataFormStr .= "</table>\n";
		return $MetadataFormStr;
	}
	
	/**
	 * Updates LicenceMetadata based upon POST message contents and licenceMetadataSpec.
	 * If licence metadata spec is already populated, then update not allowed 
	 * @param unknown $_licenceMetadataSpec
	 * @param unknown $_licenceMetadata
	 * @param unknown $POST_
	 */
	function modifyLicenceMetadataFromPost($_licenceMetadataSpec, $_licenceMetadata,  $POST_){
		// check if OSGi licence generator debugging is enabled
		$osgipub_osgi_debug = edd_get_option ( 'osgipub_osgi_debug' );
		
		// debug code to print out POST_
		if ($osgipub_osgi_debug) {
		    echo  "DEBUG received update matadata POST_ \n ";
		    while( list( $field, $value ) = each( $POST_ )) {
              // display values
              if( is_array( $value )) {
                 // if checkbox (or other multiple value fields)
                 while( list( $arrayField, $arrayValue ) = each( $value )) {
                   echo $arrayField ."=" . $arrayValue . "\n";
                 }
              } else {
                 echo $field . "=" . $value . "\n";
              }
            }
            echo  "\n " ;
		}
		
		$metadataspec = ( array ) $_licenceMetadataSpec->children ();
		$metadata = ( array ) $_licenceMetadata->children ();
		// populate fields of licenceMetadata with data from the form
		foreach ( $_licenceMetadata->children () as $key => $value ) {
			if ($key != "options") {
				// this prevents accepting post of fields already populated in licence metadata spec
				if (isset ( $POST_ [$key] )) {
					if ('' == ( string ) $metadataspec [$key]) {
						// this is complex because xpath appears to be only way to set value in SimpleXMLElement
						$keyNodes = $_licenceMetadata->xpath ( '//' . $key );
						$keyNode = $keyNodes [0];
						$keyNode->{0} = htmlspecialchars ( $POST_ [$key] );
					}
				}
			}
		}
		
		// populate options fields of licenceMetadata with data from the form
		// this is complex because xpath appears to be only way to set value in SimpleXMLElement
		// and DOMXPath implements xpath more correctly than simpleXMLElement
		$licenceMetadata_xpath = new DOMXPath ( dom_import_simplexml ( $_licenceMetadata )->ownerDocument );
		$licenceMetadataSpec_xpath = new DOMXPath ( dom_import_simplexml ( $_licenceMetadataSpec )->ownerDocument );
		$metadataOptionNames = $licenceMetadata_xpath->evaluate ( "//option/name" );
		foreach ( $metadataOptionNames as $metOptionName ) {
			$name = ( string ) $metOptionName->nodeValue;
			// check if post contains an update for the options
			if (isset ( $POST_ [$name] )) {
				// this prevents accepting post of fields already populated in licence metadata spec
				$keyNodesMs = $licenceMetadataSpec_xpath->evaluate ( "//option[name='" . $name . "']/value" );
				$keyNodeMs = $keyNodesMs->item ( 0 );
				if ('' == ( string ) $keyNodeMs->nodeValue) {
					// update value if not set in metadata spec
					$keyNodes = $licenceMetadata_xpath->evaluate ( "//option[name='" . $name . "']/value" );
					$keyNode = $keyNodes->item ( 0 );
					$keyNode->nodeValue = htmlspecialchars ( $POST_ [$name] );
				}
			}
		}
		

		// populate systemIds fields of licenceMetadata with data from the hidden allSystemIds value in form
		// the systemId elements in allSystemIds are separated by ','
		if (isset ( $POST_ ['allSystemIds'] )) {
		   $allSystemIdsStr= htmlspecialchars ( $POST_ ['allSystemIds'] );

		   // remove existing systemIds values before re-populating
		   $count = count($_licenceMetadata->systemIds->systemId);
		   
           for ($i = 0; $i < $count; $i++) {
               unset($_licenceMetadata->systemIds->systemId[0]);
           }
		   
           // split string at ',' character and add node for each systemId
           // check and never add more values than maximum value
           $maxSizeSystemIds=intval($_licenceMetadataSpec->maxSizeSystemIds);

		   $systemIdArray = explode(',', $allSystemIdsStr);
		   $systemIdArraySize = count($systemIdArray);
		   if ($systemIdArraySize > $maxSizeSystemIds) {
		      $systemIdArraySize=$maxSizeSystemIds;
		   }
		   
		   $systemIdsNode = $_licenceMetadata->systemIds[0];
		   for ($i = 0; $i < $systemIdArraySize; $i++) {
		   	   $systemidStr = (string) $systemIdArray[$i];
               $systemIdsNode->addChild("systemId", $systemidStr );
           }
           
           if ($osgipub_osgi_debug) {
		      echo "DEBUG systemIds count ". $count . "\n";
		      echo "DEBUG allSystemIdsStr ". $allSystemIdsStr . "\n";
		      echo "DEBUG maxSizeSystemIds ". $maxSizeSystemIds . "\n";
		      echo "DEBUG systemIdArraySize ". $systemIdArraySize . "\n";
		   }
		   
		}
		
		return $_licenceMetadata;
	}
	
	
	
	
	/**
	 *
	 * @param SimpleXMLElement $_licenceMetadata        	
	 * @param string $osgiLicenceGeneratorUrl        	
	 * @param string $osgi_username        	
	 * @param string $osgi_password        	
	 * @param string $osgipub_osgi_debug        	
	 * @param int $post_id        	
	 * @param  $POST_ used to pass the calling pages $_POST object
	 * @return string licence string 
	 */
	function generateLicence($_licenceMetadataSpec, $_licenceMetadata, $post_id, $POST_) {
		// check if OSGi licence generator debugging is enabled
		$osgipub_osgi_debug = edd_get_option ( 'osgipub_osgi_debug' );
		
		$_licenceMetadata =  $this->modifyLicenceMetadataFromPost($_licenceMetadataSpec, $_licenceMetadata,  $POST_);
		
		// get the base url URL of the OSGi licence generator service from settings
		$osgiLicenceGeneratorUrl = edd_get_option ( 'osgipub_osgi_licence_pub_url' );
		if (! isset ( $osgiLicenceGeneratorUrl ) || '' == $osgiLicenceGeneratorUrl) {
			throw new Exception ( 'edd-osgi-class: You must set the OSGi Licence Publisher URL in the plugin settings' );
		}
		
		// get the username to access the OSGi licence generator service from settings
		$osgi_username = edd_get_option ( 'osgipub_osgi_username' );
		if (! isset ( $osgi_username )) {
			throw new Exception ( 'edd-osgi-class: You must set the OSGi User Name in the plugin settings' );
		}
		
		// get the password to access of the OSGi licence generator service from settings
		$osgi_password = edd_get_option ( 'osgipub_osgi_password' );
		if (! isset ( $osgi_password )) {
			throw new Exception ( 'edd-osgi-class: You must set the OSGi Password in the plugin settings' );
		}

		$uri = $osgiLicenceGeneratorUrl . '/licencemgr/rest/licence-pub/createlicence';
		
		$payload = ( string ) $_licenceMetadata->asXML ();
		
		// save updated licence metadata
		update_post_meta ( $post_id, 'edd_osgiLicenceMetadataStr', $payload );
		
		if ($osgipub_osgi_debug) {
		   echo "DEBUG _licenceMetadata payload saved to postMetadata edd_osgiLicenceMetadataStr=" . $payload . "\n";
		   $TEMPedd_osgiLicenceMetadataStr = get_post_meta ( $post_id, 'edd_osgiLicenceMetadataStr', true );
		   echo "DEBUG _licenceMetadata payload retrieved from postMetadata edd_osgiLicenceMetadataStr=" . $TEMPedd_osgiLicenceMetadataStr . "\n\n";
		}
		
		if ($osgipub_osgi_debug)
			echo "Post request to licence publisher: Basic Authentication username='" . $osgi_username . "' password='" . $osgi_password . "'\n     uri='" . $uri . "'\n" . "     payload='" . $payload . "\n";
		
		$response = \Httpful\Request::post ( $uri, $payload, 'application/xml' )->authenticateWith ( $osgi_username, $osgi_password )->expectsXml ()->send ();
		
		if ($osgipub_osgi_debug)
			echo "response code='" . $response->code . "' reply payload='" . var_dump ( $response->body ) . "\n";
			
			// if we cant talk to the licence generator error and leave page
		if ($response->code != 200) {
			$msg = 'null';
			$devmsg = 'null';
			$code = $response->code;
			if (isset ( $response->errorMessage )) {
				$devmsg = ( string ) $response->errorMessage->developerMessage;
				$msg = ( string ) $response->errorMessage->message;
			}
			throw new Exception ( "edd-osgi-class: http error code='" . $code . "\n" . "     Cannot generate licence from url=' . $uri . '\n" . "     Reason=' . $msg . '\n" . "     Developer Message='" . $devmsg . "'\n" );
		}
		
		$licenceStr = $response->body->licence;
		
		return ( string ) $licenceStr;
	}
	
	/**
	 * Gets the licence metadata specification from the OSGI licence-pub service
	 * 
	 * @param string $edd_osgiProductIdStr        	
	 * @throws Exception
	 * @return string edd_osgiLicenceMetadataSpecStr spec string as xml
	 */
	function getLicenceMetadataSpec($edd_osgiProductIdStr) {
			// check if OSGi licence generator debugging is enabled
		$osgipub_osgi_debug = edd_get_option ( 'osgipub_osgi_debug' );
		
		// get the base url URL of the OSGi licence generator service from settings
		$osgiLicenceGeneratorUrl = edd_get_option ( 'osgipub_osgi_licence_pub_url' );
		if (! isset ( $osgiLicenceGeneratorUrl ) || '' == $osgiLicenceGeneratorUrl) {
			throw new Exception ( 'edd-osgi-class: You must set the OSGi Licence Publisher URL in the plugin settings' );
		}
		
		// get the username to access the OSGi licence generator service from settings
		$osgi_username = edd_get_option ( 'osgipub_osgi_username' );
		if (! isset ( $osgi_username )) {
			throw new Exception ( 'edd-osgi-class: You must set the OSGi User Name in the plugin settings' );
		}
		
		// get the password to access of the OSGi licence generator service from settings
		$osgi_password = edd_get_option ( 'osgipub_osgi_password' );
		if (! isset ( $osgi_password )) {
			throw new Exception ( 'edd-osgi-class: You must set the OSGi Password in the plugin settings' );
		}
		
		try {
			
			$uri = $osgiLicenceGeneratorUrl . '/licencemgr/rest/licence-pub/getlicencemetadataspec?productId=' . $edd_osgiProductIdStr;
			
			if ($osgipub_osgi_debug)
				echo "Get Licence Metadata Spec request to licence publisher: Basic Authentication\n" . "     username='" . $osgi_username . "' password='" . $osgi_password . "'\n" . "     uri='" . $uri . "\n";
			
			$response = \Httpful\Request::get ( $uri )->authenticateWith ( $osgi_username, $osgi_password )->expectsXml ()->send ();
			
			if ($osgipub_osgi_debug) {
				// var_dump($response);
				echo "\nResponse from licence publisher: Http response code='" . $response->code . "' response body='" . $response->body->asXML () . "'\n";
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
				throw new Exception ( "edd-osgi-class: Http error code='" . $code . "\n" . "     Cannot retrieve licence spec from OSGi licence publisher url=' . $uri . '\n" . "     Reason=' . $msg . '\n" . "     Developer Message='" . $devmsg . "'\n" );
			}
			
			$edd_osgiLicenceMetadataSpecStr = $response->body->licenceMetadataSpec->asXML ();
			
			return ( string ) $edd_osgiLicenceMetadataSpecStr;
		} catch ( Exception $e ) {
			throw new Exception ( 'Problem loading LicenceMetadataSpec: Exception: ' . $e->getMessage () . "\n" );
		}
	}
}

?>
