package org.cruxframework.crux.widgets.rebind.slideshow;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.utils.EscapeUtils;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.core.rebind.AbstractProxyCreator.SourcePrinter;
import org.cruxframework.crux.core.rebind.CruxGeneratorException;
import org.cruxframework.crux.core.rebind.screen.widget.AttributeProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.ViewFactoryCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreator;
import org.cruxframework.crux.core.rebind.screen.widget.WidgetCreatorContext;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.HasPostProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.creator.children.WidgetChildProcessor;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.DeclarativeFactory;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttribute;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributeDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributes;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagAttributesDeclaration;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChild;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagChildren;
import org.cruxframework.crux.core.rebind.screen.widget.declarative.TagConstraints;
import org.cruxframework.crux.core.utils.JClassUtils;
import org.cruxframework.crux.widgets.client.slideshow.Slideshow;
import org.cruxframework.crux.widgets.client.slideshow.data.AlbumService.Callback;
import org.cruxframework.crux.widgets.client.slideshow.data.PhotoAlbum;

import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;

class SlideshowContext extends WidgetCreatorContext
{

	public String serviceVariable;
	public JClassType serviceClass;
	
}

/**
 * 
 * @author Thiago da Rosa de Bustamante
 *
 */
@DeclarativeFactory(id="slideshow", library="widgets", targetWidget=Slideshow.class)
@TagAttributes({
	@TagAttribute(value="layout", processor=SlideshowFactory.LayoutAttributeProcessor.class),
	@TagAttribute(value="preloadNextImages", type=Boolean.class, defaultValue="true"),
	@TagAttribute(value="transitionDelay", type=Integer.class, defaultValue="5000")
})
@TagAttributesDeclaration({
	@TagAttributeDeclaration(value="scaleImages", type=Boolean.class, defaultValue="true"),
	@TagAttributeDeclaration(value="autoPlay", type=Boolean.class, defaultValue="false"),
	@TagAttributeDeclaration(value="loadOnStartup", type=Boolean.class, defaultValue="false")
})
@TagChildren({
	@TagChild(SlideshowFactory.ServiceProcessor.class)
})
public class SlideshowFactory extends WidgetCreator<SlideshowContext>
{
	public static class LayoutAttributeProcessor extends AttributeProcessor<SlideshowContext>
	{
		public LayoutAttributeProcessor(WidgetCreator<?> widgetCreator)
        {
	        super(widgetCreator);
        }

		@Override
        public void processAttribute(SourcePrinter out, SlideshowContext context, String attributeValue)
        {
			String layoutClass = SlideshowConfig.getLayout(attributeValue);
			if (!StringUtils.isEmpty(layoutClass))
			{
				out.println(context.getWidget()+".setLayout(new "+layoutClass+"());");
			}
			else
			{
				throw new CruxGeneratorException("Can not found a slideshow layout associated with the key ["+attributeValue+"]");
			}
        }
	}

	@TagConstraints(minOccurs="0", maxOccurs="1", tagName="service")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="name", required=true)
	})
	@TagChildren({
		@TagChild(ServiceParameterProcessor.class)
	})
	public static class ServiceProcessor extends WidgetChildProcessor<SlideshowContext> implements HasPostProcessor<SlideshowContext>
	{
		@Override
		public void processChildren(SourcePrinter out, SlideshowContext context) throws CruxGeneratorException
		{
			context.serviceVariable = ViewFactoryCreator.createVariableName("service");
			String name = context.readChildProperty("name");
			if (StringUtils.isEmpty(name))
			{
				throw new CruxGeneratorException("Service name is required for slideshow service. WidgetID["+context.getWidgetId()+"]");
			}
			String serviceClass = SlideshowConfig.getService(name);
			if (StringUtils.isEmpty(serviceClass))
			{
				throw new CruxGeneratorException("Service ["+serviceClass+"] not found for slideshow. WidgetID["+context.getWidgetId()+"]");
			}
			context.serviceClass = getWidgetCreator().getContext().getTypeOracle().findType(serviceClass);
			if (context.serviceClass == null)
			{
	    		String message = "Service class ["+serviceClass+"], declared on view ["+getWidgetCreator().getView().getId()+"], refered by slideshow ["+context.getWidgetId()+"], could not be loaded. "
				   + "\n Possible causes:"
				   + "\n\t 1. Check if any type or subtype used by service refers to another module and if this module is inherited in the .gwt.xml file."
				   + "\n\t 2. Check if your service or its members belongs to a client package."
				   + "\n\t 3. Check the versions of all your modules.";
	    		throw new CruxGeneratorException(message);
			}
			out.println(serviceClass + " " + context.serviceVariable + " = new " + serviceClass + "();");
		}
		
		@Override
        public void postProcessChildren(SourcePrinter out, SlideshowContext context) throws CruxGeneratorException
        {
			out.println(context.getWidget()+".setAlbumService("+context.serviceVariable+");");
			
			String loadOnStartup = context.readWidgetProperty("loadOnStartup");
			String autoPlay = context.readWidgetProperty("autoPlay");
			
			if (!StringUtils.isEmpty(loadOnStartup) && Boolean.parseBoolean(loadOnStartup))
			{
				out.println(context.getWidget()+".load(new "+Callback.class.getCanonicalName()+"(){");
				out.println("public void onLoaded("+PhotoAlbum.class.getCanonicalName()+" album){");
				if (!StringUtils.isEmpty(autoPlay) && Boolean.parseBoolean(autoPlay))
				{
					out.println(context.getWidget()+".play();");
				}
				out.println("}");
				out.println("public void onError(Throwable t){");
				out.println(Crux.class.getCanonicalName()+".getErrorHandler().handleError(t);");
				out.println("}");
				out.println("});");
			}
        }
	}
	
	@TagConstraints(minOccurs="0", maxOccurs="unbounded", tagName="param")
	@TagAttributesDeclaration({
		@TagAttributeDeclaration(value="name", required=true),
		@TagAttributeDeclaration(value="value", required=true)
	})
	public static class ServiceParameterProcessor extends WidgetChildProcessor<SlideshowContext>
	{
		@Override
		public void processChildren(SourcePrinter out, SlideshowContext context) throws CruxGeneratorException
		{
			String name = context.readChildProperty("name");
			String value = context.readChildProperty("value");
			if (StringUtils.isEmpty(name))
			{
				throw new CruxGeneratorException("Paramter name is required for slideshow service. WidgetID["+context.getWidgetId()+"]");
			}
			JMethod[] methods = JClassUtils.findSetterMethods(context.serviceClass, name);
			boolean validSetter = false;
			for (JMethod method : methods)
            {
				validSetter = true;
				JType type = method.getParameters()[0].getType();
				if (type.getQualifiedSourceName().equals(String.class.getCanonicalName()))
				{
					out.println(context.serviceVariable + "." + method.getName() + "("+EscapeUtils.quote(value)+");");
				}
				else if ((type.getQualifiedSourceName().equals("int")) || (type.getQualifiedSourceName().equals(Integer.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "("+value+");");
				}
				else if ((type.getQualifiedSourceName().equals("long")) || (type.getQualifiedSourceName().equals(Long.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "("+value+");");
				}
				else if ((type.getQualifiedSourceName().equals("short")) || (type.getQualifiedSourceName().equals(Short.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "((short)"+value+");");
				}
				else if ((type.getQualifiedSourceName().equals("byte")) || (type.getQualifiedSourceName().equals(Byte.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "((byte)"+value+");");
				}
				else if ((type.getQualifiedSourceName().equals("boolean")) || (type.getQualifiedSourceName().equals(Boolean.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "("+value+");");
				}
				else if ((type.getQualifiedSourceName().equals("char")) || (type.getQualifiedSourceName().equals(Character.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "('"+value+"');");
				}
				else if ((type.getQualifiedSourceName().equals("double")) || (type.getQualifiedSourceName().equals(Double.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "("+value+");");
				}
				else if ((type.getQualifiedSourceName().equals("float")) || (type.getQualifiedSourceName().equals(Float.class.getCanonicalName())))
				{
					out.println(context.serviceVariable + "." + method.getName() + "((float)"+value+");");
				}
				else
				{
					validSetter = false;
				}
				if (validSetter)
				{
					break;
				}
            }
			if (!validSetter)
			{
				throw new CruxGeneratorException("Service class does not support the property ["+name+"]. WidgetID["+context.getWidgetId()+"]");
			}
		}
	}
	
	@Override
    public SlideshowContext instantiateContext()
    {
	    return new SlideshowContext();
    }

	@Override
	public void instantiateWidget(SourcePrinter out, SlideshowContext context) throws CruxGeneratorException
	{
		String className = getWidgetClassName();
		out.println("final "+className + " " + context.getWidget()+" = GWT.create("+className+".class);");
	}
}
