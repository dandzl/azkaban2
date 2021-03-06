/*
 * Copyright 2012 LinkedIn, Inc
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

package azkaban.webapp.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;

import azkaban.project.Project;
import azkaban.project.ProjectManager;
import azkaban.user.User;
import azkaban.webapp.AzkabanWebServer;
import azkaban.webapp.session.Session;

/**
 * The main page
 */
public class IndexServlet extends LoginAbstractAzkabanServlet {
	// private static final Logger logger =
	// Logger.getLogger(IndexServlet.class.getName());

	private static final long serialVersionUID = -1;

	@Override
	protected void handleGet(HttpServletRequest req, HttpServletResponse resp, Session session) throws ServletException, IOException {
		
		if(hasParam(req, "doaction")) {
			if(getParam(req, "doaction").equals("search")) {
				String searchTerm = getParam(req, "searchterm");
				
				if(!searchTerm.equals("") && !searchTerm.equals(".*")) {
					handleFilter(req, resp, session, searchTerm);
					return;
				}
			}
		}
		
		User user = session.getUser();

		ProjectManager manager = ((AzkabanWebServer)getApplication()).getProjectManager();
		Page page = newPage(req, resp, session, "azkaban/webapp/servlet/velocity/index.vm");
		if (hasParam(req, "all")) {
			List<Project> projects = manager.getProjects();
			page.add("allProjects", "true");
			page.add("projects", projects);
		}
		else {
			List<Project> projects = manager.getUserProjects(user);
			page.add("projects", projects);
		}
		
		page.render();
	}
	
	private void handleFilter(HttpServletRequest req, HttpServletResponse resp, Session session, String searchTerm) {
		User user = session.getUser();
		ProjectManager manager = ((AzkabanWebServer)getApplication()).getProjectManager();
		Page page = newPage(req, resp, session, "azkaban/webapp/servlet/velocity/index.vm");
		if (hasParam(req, "all")) {
			//do nothing special if one asks for 'ALL' projects
			List<Project> projects = manager.getProjectsByRegex(searchTerm);
			page.add("allProjects", "");
			page.add("projects", projects);
			page.add("search_term", searchTerm);
		}
		else {
			List<Project> projects = manager.getUserProjectsByRegex(user, searchTerm);
			page.add("projects", projects);
			page.add("search_term", searchTerm);
		}
	
		page.render();
	}

	@Override
	protected void handlePost(HttpServletRequest req, HttpServletResponse resp,
			Session session) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}


}
