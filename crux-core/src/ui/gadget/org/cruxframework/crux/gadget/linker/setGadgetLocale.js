  /**
   * Gadget iframe URLs are generated with the locale in the URL as a
   *  lang/country parameter pair (e.g. lang=en&country=US) in lieu of the
   *  single locale parameter.
   * ($wnd.__gwt_Locale is read by the property provider in I18N.gwt.xml)
   */
  function setGadgetLocale() {
    var args = $wnd.location.search;

      function extractFromQueryStr(args, argName) {
        var start = args.indexOf(argName + "=");
        if (start < 0) {
          return null;
        }
        var value = args.substring(start);
        var valueBegin = value.indexOf("=") + 1;
        var valueEnd = value.indexOf("&");
        if (valueEnd == -1) {
            valueEnd = value.length;
        }
        return value.substring(valueBegin, valueEnd);
      }

    var lang = extractFromQueryStr(args, "lang");
    if (lang != null) {
      var idx = lang.indexOf("-");
      var langCountry = null;
      if (idx>0)
      {
      	  langCountry = lang.substring(idx+1, lang.length);
      	  lang = lang.substring(0, idx);
      }
      idx = lang.indexOf("_");
      if (idx>0)
      {
      	  langCountry = lang.substring(idx+1, lang.length);
      	  lang = lang.substring(0, idx); 
      }
    
      country = extractFromQueryStr(args, "country");
      if (country != null) {
	    $wnd.__gwt_Locale = lang + "_" + country;
      } else if (langCountry != null) {
	    $wnd.__gwt_Locale = lang + "_" + langCountry;
      } else {
        $wnd.__gwt_Locale = lang;
      }
    }
  }

