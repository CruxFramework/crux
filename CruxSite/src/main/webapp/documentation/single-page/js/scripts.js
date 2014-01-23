$(function(){
	initStuff();
});

function initStuff(){
	relocateMenus();
	relocateSections();
	addHeaderLinks();
}

function relocateMenus(){
	$('body').append('<div class="sidemenu"></div>');
	$('.sidemenu').append($('.book > .toc, .book > .list-of-figures, .book > .list-of-tables, .book > .list-of-examples'));
}

function relocateSections(){
	$('body').append('<div class="sectionsContainer"></div>');
	$('.sectionsContainer').append($('section.chapter, section.glossary, section.appendix'));
}

function growMenu(){
	$('.sidemenu').height($(document).height());
}

function addHeaderLinks(){
	$('.book > .titlepage h1').append('<a href="http://www.cruxframework.org/" class="right">Go to Crux Site</a><a href="http://showcase.cruxframework.org/" class=" right">Visit the Showcase</a>');
}