// Installs the script by immediately appending a script tag to the body head
// with the src set, to get the script contents. The script contents are then
// installed into a script tag which is added to the install location (because
// the script contents will be wrapped in a call to onScriptDownloaded()).
function installScript(filename) {
  // Get a URL that is cached for a year (31536000 seconds).
  filename = $wnd.gadgets.io.getProxyUrl(filename, {refreshInterval:31536000});

  // Provides the getInstallLocation() and getInstallLocationDoc() functions
  __INSTALL_LOCATION__

  // Provides the setupWaitForBodyLoad() function
  __WAIT_FOR_BODY_LOADED__

  function installCode(code) {
    var docbody = getInstallLocation();
    var script = getInstallLocationDoc().createElement('script');
    script.language='javascript';
    script.text = code;
    docbody.appendChild(script);

    // Unless we're in pretty mode, remove the tags to shrink the DOM a little.
    // It should have installed its code immediately after being added.
    __START_OBFUSCATED_ONLY__
    docbody.removeChild(script);
    __END_OBFUSCATED_ONLY__
  }
  
  // Set up a script tag to start downloading immediately, as well as a
  // callback to install the code once it is downloaded and the body is loaded.
  __MODULE_FUNC__.onScriptDownloaded = function(code) {
    setupWaitForBodyLoad(function() {
      installCode(code);
    });
  };
  sendStats('moduleStartup', 'moduleRequested');
  var script = $doc.createElement('script');
  script.src = filename;
  $doc.getElementsByTagName('head')[0].appendChild(script);
}
