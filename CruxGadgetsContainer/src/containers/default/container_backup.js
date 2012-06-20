{
	"gadgets.container" : ["default", "accel"],
	"gadgets.parent" : null,
	"gadgets.lockedDomainRequired" : false,
	"gadgets.lockedDomainSuffix" : "-a.ssghml.sysmap.com.br:8888",
	"gadgets.parentOrigins" : ["*"],
	"gadgets.iframeBaseUri" : "/gadgets/ifr",
	"gadgets.uri.iframe.basePath" : "/gadgets/ifr",
	"gadgets.jsUriTemplate" : "http://%host%/gadgets/js/%js%",
	"gadgets.uri.iframe.lockedDomainSuffix" :  "-a.ssghml.sysmap.com.br:8888",
	"gadgets.uri.iframe.unlockedDomain" : "ssghml.sysmap.com.br:8888",
	"gadgets.uri.js.host" : "http://ssghml.sysmap.com.br:8888/",
	"gadgets.uri.js.path" : "/gadgets/js",
	"gadgets.uri.oauth.callbackTemplate" : "//%host%/gadgets/oauthcallback",
	"gadgets.securityTokenType" : "insecure",
	"gadgets.osDataUri" : "http://%host%/rpc",
	"gadgets.uri.concat.host" : "${Cur['defaultShindigProxyConcatAuthority']}",
	"gadgets.uri.concat.path" : "/gadgets/concat",
	"gadgets.uri.concat.js.splitToken" : "false",
	"gadgets.uri.proxy.host" : "${Cur['defaultShindigProxyConcatAuthority']}",
	"gadgets.uri.proxy.path" : "/gadgets/proxy",
	"gadgets.features" : {
	  "core.io" : {
	    "proxyUrl" : "//%host%/gadgets/proxy?container=default&refresh=%refresh%&url=%url%%rewriteMime%",
	    "jsonProxyUrl" : "//%host%/gadgets/makeRequest"
	  },
	  "views" : {
	    "profile" : {
	      "isOnlyVisible" : false,
	      "urlTemplate" : "http://%host%/gadgets/profile?{var}",
	      "aliases": ["DASHBOARD", "default"]
	    },
	    "canvas" : {
	      "isOnlyVisible" : true,
	      "urlTemplate" : "http://%host%/gadgets/canvas?{var}",
	      "aliases" : ["FULL_PAGE"]
	    }
	  },
	  "tabs": {
	    "css" : [
	      ".tablib_table {",
	      "width: 100%;",
	      "border-collapse: separate;",
	      "border-spacing: 0px;",
	      "empty-cells: show;",
	      "font-size: 11px;",
	      "text-align: center;",
	    "}",
	    ".tablib_emptyTab {",
	      "border-bottom: 1px solid #676767;",
	      "padding: 0px 1px;",
	    "}",
	    ".tablib_spacerTab {",
	      "border-bottom: 1px solid #676767;",
	      "padding: 0px 1px;",
	      "width: 1px;",
	    "}",
	    ".tablib_selected {",
	      "padding: 2px;",
	      "background-color: #ffffff;",
	      "border: 1px solid #676767;",
	      "border-bottom-width: 0px;",
	      "color: #3366cc;",
	      "font-weight: bold;",
	      "width: 80px;",
	      "cursor: default;",
	    "}",
	    ".tablib_unselected {",
	      "padding: 2px;",
	      "background-color: #dddddd;",
	      "border: 1px solid #aaaaaa;",
	      "border-bottom-color: #676767;",
	      "color: #000000;",
	      "width: 80px;",
	      "cursor: pointer;",
	    "}",
	    ".tablib_navContainer {",
	      "width: 10px;",
	      "vertical-align: middle;",
	    "}",
	    ".tablib_navContainer a:link, ",
	    ".tablib_navContainer a:visited, ",
	    ".tablib_navContainer a:hover {",
	      "color: #3366aa;",
	      "text-decoration: none;",
	    "}"
	    ]
	  },
	  "minimessage": {
	      "css": [
	        ".mmlib_table {",
	        "width: 100%;",
	        "font: bold 9px arial,sans-serif;",
	        "background-color: #fff4c2;",
	        "border-collapse: separate;",
	        "border-spacing: 0px;",
	        "padding: 1px 0px;",
	      "}",
	      ".mmlib_xlink {",
	        "font: normal 1.1em arial,sans-serif;",
	        "font-weight: bold;",
	        "color: #0000cc;",
	        "cursor: pointer;",
	      "}"
	     ]
	  },
	  "rpc" : {
	    "parentRelayUrl" : "/container/rpc_relay.html",
	    "useLegacyProtocol" : false
	  },
	  "skins" : {
	    "properties" : {
	      "BG_COLOR": "",
	      "BG_IMAGE": "",
	      "BG_POSITION": "",
	      "BG_REPEAT": "",
	      "FONT_COLOR": "",
	      "ANCHOR_COLOR": ""
	    }
	  },
	  "opensocial" : {
	    "path" : "http://%host%/rpc",
	    "invalidatePath" : "http://%host%/rpc",
	    "domain" : "shindig",
	    "enableCaja" : false,
	    "supportedFields" : {
	       "person" : ["id", {"name" : ["familyName", "givenName", "unstructured"]}, "thumbnailUrl", "profileUrl"],
	       "activity" : ["appId", "body", "bodyId", "externalId", "id", "mediaItems", "postedTime", "priority", 
	                     "streamFaviconUrl", "streamSourceUrl", "streamTitle", "streamUrl", "templateParams", "title",
	                     "url", "userId"],
	       "album" : ["id", "thumbnailUrl", "title", "description", "location", "ownerId"],
	       "mediaItem" : ["album_id", "created", "description", "duration", "file_size", "id", "language", "last_updated",
	                      "location", "mime_type", "num_comments", "num_views", "num_votes", "rating", "start_time",
	                      "tagged_people", "tags", "thumbnail_url", "title", "type", "url"]
	    }
	  },
	  "osapi.services" : {
	    "gadgets.rpc" : ["container.listMethods"]
	  },
	  "osapi" : {
	    "endPoints" : [ "http://%host%/rpc" ]
	  },
	  "osml": {
	    "library": "config/OSML_library.xml"
	  }
	}
}
