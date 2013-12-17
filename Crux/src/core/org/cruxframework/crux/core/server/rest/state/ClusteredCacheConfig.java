/*
 * Copyright 2013 cruxframework.org.
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
package org.cruxframework.crux.core.server.rest.state;

import org.cruxframework.crux.core.i18n.DefaultServerMessage;
import org.cruxframework.crux.core.shared.rest.annotation.GET;
import org.jgroups.JChannel;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public interface ClusteredCacheConfig
{
	@DefaultServerMessage(JChannel.DEFAULT_PROTOCOL_STACK)
	String channelConfigPropertyFile();

	@DefaultServerMessage("1500")
	String rpcTimeout();

	@DefaultServerMessage((GET.ONE_DAY*1000)+"")
	String cachingTime();

	@DefaultServerMessage("true")
	String useL1Cache();

	@DefaultServerMessage("-1")
	String l1ReapingInterval();

	@DefaultServerMessage("5000")
	String l1MaxNumberOfEntries();

	@DefaultServerMessage("30000")
	String l2ReapingInterval();

	@DefaultServerMessage("-1")
	String l2MaxNumberOfEntries();

	@DefaultServerMessage("__CruxServiceResourcesInfo__")
	String clusterName();

	@DefaultServerMessage("2")
	String replCount();
}
