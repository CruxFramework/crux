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
package org.cruxframework.crux.gadgets.server.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.servlet.MakeRequestServlet;

import com.google.inject.Inject;

/**
 * @author Thiago da Rosa de Bustamante
 *
 */
public class SSOMakeRequestServlet extends MakeRequestServlet
{
    private static final long serialVersionUID = -4785735511224732596L;

    private transient SSOMakeRequestHandler ssoMakeRequestHandler;
    
    @Inject
    public void setSSOMakeRequestHandler(SSOMakeRequestHandler ssoMakeRequestHandler) {
      checkInitialized();
      this.ssoMakeRequestHandler = ssoMakeRequestHandler;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
      try {
        ssoMakeRequestHandler.fetch(request, response);
      } catch (GadgetException e) {
        int responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        if (e.getCode() != GadgetException.Code.INTERNAL_SERVER_ERROR) {
          responseCode = HttpServletResponse.SC_BAD_REQUEST;
        }
        response.sendError(responseCode, e.getMessage() != null ? e.getMessage() : "");
      }
    } 
}
