/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.airavata.workflow.catalog;

import org.airavata.appcatalog.cpi.AppCatalogException;
import org.airavata.appcatalog.cpi.WorkflowCatalog;
import org.apache.aiaravata.application.catalog.data.impl.AppCatalogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowCatalogFactory {
    private static Logger logger = LoggerFactory.getLogger(WorkflowCatalogFactory.class);
	private static WorkflowCatalog workflowCatalog;
	
	public static WorkflowCatalog getWorkflowCatalog() throws AppCatalogException{
		try {
            if (workflowCatalog==null) {
                workflowCatalog = AppCatalogFactory.getAppCatalog().getWorkflowCatalog();
            }
            return workflowCatalog;
        } catch (AppCatalogException e) {
            logger.error("Unable to create workflow catalog instance", e);
            throw new AppCatalogException(e);
        }
    }
}
