/*
 * Copyright 2014 cruxframework.org.
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
package org.cruxframework.crux.smartfaces.client.grid;


public interface Type 
{
	public static enum SelectStrategy
	{
		SINGLE,
		SINGLE_RADIOBUTTON_COLUMN,
		SINGLE_CUSTOM_COLUMN,
		MULTIPLE,
		MULTIPLE_CHECKBOX_COLUMN,
		MULTIPLE_CUSTOM_COLUMN;
	}
	
	
//	boolean isTree();
//	boolean isSlider();
//	
//	public static enum LargeType implements Type
//	{
//		VERTICAL_TREE("VerticalTree", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_TREE + Menu.SPACE + Menu.CLEARFIX),
//		VERTICAL_SLIDE("VerticalSlider", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_SLIDE + Menu.SPACE + Menu.CLEARFIX),
//		VERTICAL_ACCORDION("VerticalAccordion", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_ACCORDION + Menu.SPACE + Menu.CLEARFIX),
//		VERTICAL_DROPDOWN("VerticalDropdown", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_DROPDOWN + Menu.SPACE + Menu.CLEARFIX),
//		HORIZONTAL_ACCORDION("HorizontalAccordion", Menu.STYLE_FACES_HORIZONTAL + Menu.SPACE + Menu.STYLE_FACES_ACCORDION + Menu.SPACE + Menu.CLEARFIX),
//		HORIZONTAL_DROPDOWN("HorizontalDropdown", Menu.STYLE_FACES_HORIZONTAL + Menu.SPACE + Menu.STYLE_FACES_DROPDOWN + Menu.SPACE + Menu.CLEARFIX);
//		
//		String friendlyName;
//		String styleName;
//		LargeType(String friendlyName, String styleName)
//		{
//			this.friendlyName = friendlyName;
//			this.styleName = styleName;	
//		}
//		
//		@Override
//		public String toString() 
//		{
//			return friendlyName;
//		}
//		
//		public static LargeType getByName(String friendlyName)
//		{
//			for(LargeType type : LargeType.values())
//			{
//				if(StringUtils.unsafeEquals(type.friendlyName, friendlyName))
//				{
//					return type;
//				}
//			}
//			return null;
//		}
//
//		@Override
//		public boolean isTree()
//		{
//			return this.equals(LargeType.VERTICAL_TREE);	
//		}
//		
//		@Override
//		public boolean isSlider()
//		{
//			return this.equals(LargeType.VERTICAL_SLIDE);	
//		}
//	}
//	
//	public static enum SmallType implements Type
//	{
//		VERTICAL_TREE("VerticalTree", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_TREE),
//		VERTICAL_SLIDE("VerticalSlider", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_SLIDE),
//		VERTICAL_ACCORDION("VerticalAccordion", Menu.STYLE_FACES_VERTICAL + Menu.SPACE + Menu.STYLE_FACES_ACCORDION),
//		HORIZONTAL_ACCORDION("HorizontalAccordion", Menu.STYLE_FACES_HORIZONTAL + Menu.SPACE + Menu.STYLE_FACES_ACCORDION);
//		
//		String friendlyName;
//		String styleName;
//		SmallType(String friendlyName, String styleName)
//		{
//			this.friendlyName = friendlyName;
//			this.styleName = styleName;
//		}
//		
//		@Override
//		public String toString() 
//		{
//			return friendlyName;
//		}
//		
//		public static SmallType getByName(String friendlyName)
//		{
//			for(SmallType type : SmallType.values())
//			{
//				if(StringUtils.unsafeEquals(type.friendlyName, friendlyName))
//				{
//					return type;
//				}
//			}
//			return null;
//		}
//		
//		@Override
//		public boolean isTree()
//		{
//			return this.equals(SmallType.VERTICAL_TREE);	
//		}
//		
//		@Override
//		public boolean isSlider()
//		{
//			return this.equals(SmallType.VERTICAL_SLIDE);	
//		}
//	}
}
