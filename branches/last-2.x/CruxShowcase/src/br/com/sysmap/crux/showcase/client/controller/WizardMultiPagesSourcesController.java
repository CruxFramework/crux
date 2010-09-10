package br.com.sysmap.crux.showcase.client.controller;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;

import br.com.sysmap.crux.core.client.controller.Controller;
import br.com.sysmap.crux.core.client.controller.Expose;

@Controller("wizardMultiPagesSourcesController")
public class WizardMultiPagesSourcesController extends SourcesController {
	
	private Map<Integer, Boolean> sourcesLoaded = new HashMap<Integer, Boolean>();
	
	@Expose
	public void onSelectTab(BeforeSelectionEvent<Integer> event) {
		Integer itemIndex = event.getItem();
		if(!sourcesLoaded.containsKey(itemIndex)){
			sourcesLoaded.put(itemIndex, true);
			if (itemIndex%2==0)
			{
				loadXMLFile(getPageFile(itemIndex), getWidget(itemIndex));
			}
			else
			{
				loadFile(getFile(itemIndex), getWidget(itemIndex));
			}
		}
	}
	
	String getFile(int index){
		switch (index){
        	case 1: return "client/controller/MultiWizardController.java";  
        	case 3: return "client/controller/WizardStep1Controller.java";  
        	case 5: return "client/controller/WizardStep2Controller.java";  
        	case 7: return "client/controller/WizardStep3Controller.java";  
        	case 9: return "client/controller/WizardStep4Controller.java";  
        }
		
		return null;
	}

	String getPageFile(int index){
		switch (index){
        	case 0: return "multiPagesWizard.crux.xml";  
        	case 2: return "wizardStep1.crux.xml";  
        	case 4: return "wizardStep2.crux.xml";  
        	case 6: return "wizardStep3.crux.xml";  
        	case 8: return "wizardStep4.crux.xml";  
        }
		
		return null;
	}

	String getWidget(int index){
		switch (index){
			case 0: return "pageSource";  
        	case 1: return "controllerSource";  
			case 2: return "pageStep1Source";  
        	case 3: return "controllerStep1Source";  
			case 4: return "pageStep2Source";  
        	case 5: return "controllerStep2Source";  
			case 6: return "pageStep3Source";  
        	case 7: return "controllerStep3Source";  
			case 8: return "pageStep4Source";  
        	case 9: return "controllerStep4Source";  
        }
		
		return null;
	}
}