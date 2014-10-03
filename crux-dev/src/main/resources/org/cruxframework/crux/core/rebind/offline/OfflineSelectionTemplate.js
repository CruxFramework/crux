function __MODULE_FUNC__() {
  var $wnd = window;

  var metaProps = {} ,values = [] ,providers = [] ,answers = [] ,softPermutationId = 0 ,onLoadErrorFunc, propertyErrorFunc; // end of global vars



  

  function unflattenKeylistIntoAnswers(propValArray, value) {
    var answer = answers;
    for (var i = 0, n = propValArray.length - 1; i < n; ++i) {
      // lazy initialize an empty object for the current key if needed
      answer = answer[propValArray[i]] || (answer[propValArray[i]] = []);
    }
    // set the final one to the value
    answer[propValArray[n]] = value;
  }

  // Computes the value of a given property.  propName must be a valid property
  // name. Used by the generated PERMUTATIONS code.
  //
  function computePropValue(propName) {
    var value = providers[propName](), allowedValuesMap = values[propName];
    if (value in allowedValuesMap) {
      return value;
    }
    var allowedValuesList = [];
    for (var k in allowedValuesMap) {
      allowedValuesList[allowedValuesMap[k]] = k;
    }
    if (propertyErrorFunc) {
      propertyErrorFunc(propName, allowedValuesList, value);
    }
    throw null;
  }
  
  // __PROCESS_METAS__
  
  
  
  function __gwt_getMetaProperty(name) {
    var value = metaProps[name];
    return (value == null) ? null : value;
  }
  
  function getCalculatedUserAgent()
  {
  	var calculatedUserAgent = 'default';
  	try 
  	{
  		if (typeof(computePropValue('user.agent')) != 'undefined')
      {
      	calculatedUserAgent = computePropValue('user.agent'); 
      }
  	} catch (e) 
  	{
  	}
  	return calculatedUserAgent;
  }
  
  function getCalculatedDeviceFeatures()
  {
  	var calculatedDeviceFeatures = 'default';
  	
  	try
  	{
			if (typeof(computePropValue('device.features')) != 'undefined')
	    {
	    	calculatedDeviceFeatures = computePropValue('device.features'); 
	    }
		} catch (e) 
  	{
  	}
  	return calculatedDeviceFeatures;;
  }
  
  
  
  // --------------- PROPERTY PROVIDERS --------------- 

// __PROPERTIES_BEGIN__
// __PROPERTIES_END__

  // --------------- STRAIGHT-LINE CODE ---------------
  var strongName;
  var initialHtml;

  // --------------- WINDOW ONLOAD HOOK ---------------
    try {
// __PERMUTATIONS_BEGIN__
      // Permutation logic
// __PERMUTATIONS_END__
      
      var groupId = getCalculatedUserAgent() + '_' + getCalculatedDeviceFeatures();
      initialHtml = groupId + ".cache.html";
    } catch (e) {
     // intentionally silent on property failure
      return;
    }

//	window.__Crux_Frame = true; // Mark this page as a valid crux frame

// __PAGE_LOADER_FUNCTION__

	loadPage('offlineLoader_'+initialHtml);
}

__MODULE_FUNC__();
