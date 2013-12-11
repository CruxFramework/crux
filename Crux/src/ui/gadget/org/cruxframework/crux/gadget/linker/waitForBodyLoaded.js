// Setup code which waits for the body to be loaded and then calls the
// callback function
function setupWaitForBodyLoad(callback) {
  // Provides the isBodyLoaded() function
  __IS_BODY_LOADED__

  var bodyDone = false;

  if (isBodyLoaded()) {
    bodyDone = true;
    callback();
  }

  // If the page is not already loaded, setup some listeners and timers to
  // detect when it is done.
  var onBodyDoneTimerId;
  function onBodyDone() {
    if (!bodyDone) {
      bodyDone = true;
      callback();
// __MODULE_STYLES_BEGIN__
     // Style resources are injected here to prevent operation aborted errors on ie
// __MODULE_STYLES_END__

// __MODULE_SCRIPTS_BEGIN__
  // Script resources are injected here
// __MODULE_SCRIPTS_END__

      if (onBodyDoneTimerId) {
        clearInterval(onBodyDoneTimerId);
      }
    }
  }

  $wnd.gadgets.util.registerOnLoadHandler(function() {
      onBodyDone();
  });

  // Fallback. If onBodyDone() gets fired twice, it's not a big deal.
  var onBodyDoneTimerId = setInterval(function() {
    if (isBodyLoaded()) {
      onBodyDone();
    }
  }, 50);
}
