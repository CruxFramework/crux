/*
 * Copyright 2011 cruxframework.org.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.cruxframework.crux.gadgets.client.layout;

import org.cruxframework.crux.gadgets.client.container.GadgetContainer;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class GadgetShindigClassHandler
{
	private final LayoutManager manager;

	/**
	 * 
	 * @param manager
	 */
	public GadgetShindigClassHandler(LayoutManager manager)
    {
		this.manager = manager;
		createInheritsFunction();
    }
	
	/**
	 * Create the native javascript class used by shindig to render a gadget
	 */
	public void createNativeGadgetClass()
    {
		createGadgetClass();
		createGadgetClassGetIframeUrlFunction();
		createGadgetClassGetAditionalParamsFunction(GadgetContainer.get().isCajaEnabled(), GadgetContainer.get().isDebug());
		createGadgetClassIsProfileViewFunction();
		createGadgetClassGetTitleBarContentFunction();
		createGadgetClassChangeViewFunction(manager);
		createGadgetClassOpenMenuOptionsFunction(manager);
		createGadgetClassRefreshFunction();
		configureGadgetClass();
    }
	
	/**
	 * Create the gadget Layout Manager and associate it with gadget.container.layoutManager.
	 */
	public void createNativeLayoutManager()
	{
		createNativeLayoutManager(manager);
		configureLayoutManager();
	}
	
	/**
	 * Create a base function for native javascript classes inheritance.
	 */
	protected native void createInheritsFunction()/*-{
		if (!Function.prototype.inherits)
		{
			Function.prototype.inherits = function(parentCtor) {
				function tempCtor() {};
				tempCtor.prototype = parentCtor.prototype;
				this.superClass_ = parentCtor.prototype;
				this.prototype = new tempCtor();
				this.prototype.constructor = this;
			};
		} 
	}-*/;
	
	/**
	 * Create the gadget Layout Manager and associate it with gadget.container.layoutManager.
	 * @param manager
	 */
	protected native void createNativeLayoutManager(LayoutManager manager)/*-{
		$wnd.CruxLayoutManager = {};
		
		$wnd.CruxLayoutManager = function() {
		    $wnd.shindig.LayoutManager.call(this);
		};
		
		$wnd.CruxLayoutManager.inherits($wnd.shindig.LayoutManager);
		
		$wnd.CruxLayoutManager.prototype.getGadgetChrome = function(gadget) {
		    return manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::getGadgetChrome(I)(gadget.id);
		};
	}-*/;
	
	
	/**
	 * Create the javascript class used by shindig to render a gadget.
	 */
	protected native void createGadgetClass()/*-{
		$wnd.CruxGadgetClass = function(opt_params) {
    		$wnd.shindig.Gadget.call(this, opt_params);
			this.id = -1;
			this.isActive_ = true;
			if (!this.serverBase) {
				this.serverBase_ = '/gadgets/'; // default gadget server
			}
			if (!this.view) {
				this.view = $wnd.shindig.container.view_;
			}
			if (!this.width) {
				this.width = '100%';
			}
			var subClass = this.requiresPubSub2 ? $wnd.shindig.OAAIfrGadget : $wnd.shindig.IfrGadget;
			for (var name in subClass) {
			    if (subClass.hasOwnProperty(name) && name !== "getIframeUrl") {
				    this[name] = subClass[name];
				}
			}     		
  		};

		$wnd.CruxGadgetClass.inherits($wnd.shindig.BaseIfrGadget);
	}-*/;
	
	/**
	 * Create the getIframeUrl function for the gadget class. 
	 */
	protected native void createGadgetClassGetIframeUrlFunction()/*-{
  		$wnd.CruxGadgetClass.prototype.getIframeUrl = function() {
			return this.serverBase_ + 'ifr?' +
				'container=' + this.CONTAINER +
				'&mid=' + this.id +
				'&nocache=' + $wnd.shindig.container.nocache_ +
				'&country=' + $wnd.shindig.container.country_ +
				'&lang=' + $wnd.shindig.container.language_ +
				'&view=' + this.view +
				(this.specVersion ? '&v=' + this.specVersion : '') +
				((!this.requiresPubSub2 && $wnd.shindig.container.parentUrl_) ? '&parent=' + encodeURIComponent($wnd.shindig.container.parentUrl_) : '') +
				//(this.debug ? '&debug=1' : '') +
				this.getAdditionalParams() +
				this.getUserPrefsParams() +
				(this.secureToken ? '&st=' + this.secureToken : '') +
				'&url=' + encodeURIComponent(this.specUrl) +
				(!this.requiresPubSub2? '#rpctoken=' + this.rpcToken:'') +
				(this.viewParams ?
				'&view-params=' + encodeURIComponent($wnd.gadgets.json.stringify(this.viewParams)) : '') +
				(this.hashData ? (!this.requiresPubSub2?'&':'#') + this.hashData : '');
  		};
	}-*/;
	
	/**
	 * Create the getAdditionalParams function for the gadget class. 
	 * 
	 * @param useCaja
	 * @param isDebug
	 */
	protected native void createGadgetClassGetAditionalParamsFunction(boolean useCaja, boolean isDebug)/*-{
  		$wnd.CruxGadgetClass.prototype.getAdditionalParams = function() {
    		var params = '';

    		if (useCaja) {
      			params += "&caja=1&libs=caja";
    		}
    		if (isDebug) {
      			params += "&debug=1";
    		}
    		return params;
  		};
	}-*/;
	
	/**
	 * Create the isProfileView function for the gadget class. 
	 */
	protected native void createGadgetClassIsProfileViewFunction()/*-{
  		$wnd.CruxGadgetClass.prototype.isProfileView = function() {
  			return this.view === "home" || this.view === "profile" || this.view === "default";
  		};
  	}-*/;
  		
	/**
	 * Create the getTitleBarContent function for the gadget class. 
	 */
	protected void createGadgetClassGetTitleBarContentFunction()
	{
		createGadgetClassGetTitleBarContentFunction(true);
	}
	
	/**
	 * Create the getTitleBarContent function for the gadget class. 
	 */
	protected native void createGadgetClassGetTitleBarContentFunction(boolean showControlButtons)/*-{
		$wnd.CruxGadgetClass.prototype.getTitleBarContent = function(continuation) {

			var toogleButton = (this.isProfileView()?'<a href="#" id="gadgets_gadget_toogle_' + this.id + '" onclick="shindig.container.getGadget(' + this.id +
						 ').handleToggle();return false;" class="' + this.cssClassTitleButtonToogle +
						 '"></a>':'');

			var fullScreenButton = ((this.isProfileView() || showControlButtons)?'<a id="gadgets_gadget_full_screen_' + this.id + 
			             '"href="#" onclick="shindig.container.getGadget(' + this.id +
						 ').changeView();return false;" class="' + 
						 (this.isProfileView()?this.cssClassTitleButtonFullScreen:this.cssClassTitleButtonRestoreScreen) +
						 '"></a>':'');						
									
			var menuButton = ((this.isProfileView() || showControlButtons)?'<a href="#" id="gadgets_gadget_menu_' + this.id + 
						 '" onclick="shindig.container.getGadget(' + this.id +
						 ').openMenuOptions(this);return false;" class="' + this.cssClassTitleButtonMenu + '"></a>':'');						

			var cssTitleBar = this.cssClassTitleBar +(this.isProfileView()?'':'-canvas');
			continuation('<div id="' + this.cssClassTitleBar + '-' + this.id +
						 '" class="' + cssTitleBar + '"><span id="' +
						 this.getIframeId() + '_title" class="' +
						 this.cssClassTitle + '">' + (this.title ? this.title : 'Title') + '</span><div class="' +
						 this.cssClassTitleButtonBar + '">' + fullScreenButton + toogleButton + menuButton + 
						 '</div></div>');
		}; 

		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonMenu = 'gadgets-gadget-title-button-menu';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonToogle = 'gadgets-gadget-title-button-toogle';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonFullScreen = 'gadgets-gadget-title-button-full-screen';
		$wnd.CruxGadgetClass.prototype.cssClassTitleButtonRestoreScreen = 'gadgets-gadget-title-button-restore-screen';
		$wnd.CruxGadgetClass.prototype.cssClassTitleOptionsMenu = 'gadgets-gadget-title-options-menu';
	}-*/;
	//TODO: adicionar botao para permissoes opensocial
	//TODO: implementar UserPrefStore extendendo DefaultUserPrefStore e setar a $wnd.shindig.Container.prototype.userPrefStore
	/**
	 * Create the chanveView function for the gadget class. 
	 */
	protected native void createGadgetClassChangeViewFunction(LayoutManager manager)/*-{
  		$wnd.CruxGadgetClass.prototype.changeView = function() {
  			var view;
  			if (this.isProfileView())
  			{
  				view = @org.cruxframework.crux.gadgets.client.layout.LayoutManager::profile;
  			}
  			else
  			{
  				view = @org.cruxframework.crux.gadgets.client.layout.LayoutManager::canvas;
  			}
			manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::changeGadgetView(ILorg/cruxframework/crux/gadgets/client/container/ContainerView;)(this.id, view);  		
  		};
	}-*/;
	
	/**
	 * Create the openMenuOptions function for the gadget class. 
	 */
	protected native void createGadgetClassOpenMenuOptionsFunction(LayoutManager manager)/*-{
  		$wnd.CruxGadgetClass.prototype.openMenuOptions = function(menuOptionsButton) {
			manager.@org.cruxframework.crux.gadgets.client.layout.LayoutManager::openMenuOptions(Lorg/cruxframework/crux/gadgets/client/container/Gadget;Lcom/google/gwt/user/client/Element;)(this, menuOptionsButton);  		
  		};
	}-*/;
	
	/**
	 * Create the openMenuOptions function for the gadget class. 
	 */
	protected native void createGadgetClassRefreshFunction()/*-{
  		$wnd.CruxGadgetClass.prototype.refresh = function() {
			if (this.isActive_) {
				var iframeId = this.getIframeId();
				$doc.getElementById(iframeId).src = this.getIframeUrl();
			} 
  		};
	}-*/;
	
	/**
	 * Associate the native class created here with shindig.container.gadgetClass variable. 
	 */
	protected native void configureGadgetClass()/*-{
  		$wnd.shindig.container.gadgetClass = $wnd.CruxGadgetClass;
	}-*/;
	
	/**
	 * Associate a new object with type equals to the native class created here with 
	 * shindig.container.layoutManager variable. 
	 */
	protected native void configureLayoutManager()/*-{
		$wnd.shindig.container.layoutManager = new $wnd.CruxLayoutManager();
	}-*/;
}
