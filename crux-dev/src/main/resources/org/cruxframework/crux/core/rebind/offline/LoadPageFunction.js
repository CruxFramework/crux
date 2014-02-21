/**
 * Page Loader function.
 */     
function loadPage(page){    
	function replacePage() {
		setTimeout(function(){window.location.replace(page);}, 1);
		clearListeners();			
	}

	function addListeners(){
        window.applicationCache.addEventListener('noupdate', replacePage, false);
        window.applicationCache.addEventListener('updateready', replacePage, false);
        window.applicationCache.addEventListener('error', replacePage, false);
	}

	function clearListeners(){
        window.applicationCache.removeEventListener('noupdate', replacePage);
        window.applicationCache.removeEventListener('updateready', replacePage);
        window.applicationCache.removeEventListener('error', replacePage);
	}

	var checker = setInterval(function(){
		if (window.applicationCache.status !== 0) {
	        addListeners(); 
	        if (window.applicationCache.status === window.applicationCache.UPDATEREADY){
				clearListeners();			
				clearInterval(checker);
		        window.applicationCache.swapCache();
				window.location.reload();
	        }else if (window.applicationCache.status === window.applicationCache.IDLE){
		        window.applicationCache.update();
				clearInterval(checker);
		    }
		} 
	}, 10);
}
