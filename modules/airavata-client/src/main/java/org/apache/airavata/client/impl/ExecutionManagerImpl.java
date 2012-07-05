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

package org.apache.airavata.client.impl;

import java.util.Calendar;
import java.util.List;

import org.apache.airavata.client.airavata.AiravataClient;
import org.apache.airavata.client.api.AiravataAPIInvocationException;
import org.apache.airavata.client.api.ExecutionManager;
import org.apache.airavata.workflow.model.wf.Workflow;
import org.apache.airavata.workflow.model.wf.WorkflowInput;
import org.apache.airavata.xbaya.monitor.Monitor;
import org.apache.airavata.xbaya.monitor.MonitorEventListener;

public class ExecutionManagerImpl implements ExecutionManager {
	private AiravataClient client;
	
	public ExecutionManagerImpl(AiravataClient client) {
		setClient(client);
	}
	
	@Override
	public String runWorkflow(String workflowTemplateId,
			List<WorkflowInput> inputs) throws AiravataAPIInvocationException {
		return runWorkflow(workflowTemplateId, inputs ,getClient().getCurrentUser(),null, workflowTemplateId+"_"+Calendar.getInstance().getTime().toString());
	}

	@Override
	public String runWorkflow(Workflow workflow, List<WorkflowInput> inputs)
			throws AiravataAPIInvocationException {
		return runWorkflow(workflow,inputs, getClient().getCurrentUser(),null);
	}

	@Override
	public String runWorkflow(String workflowTemplateId,
			List<WorkflowInput> inputs, String user, String metadata, String workflowInstanceName)
			throws AiravataAPIInvocationException {
		try {
			return getClient().runWorkflow(workflowTemplateId, inputs, user, metadata, workflowInstanceName);
		} catch (Exception e) {
			throw new AiravataAPIInvocationException(e);
		}

	}

	@Override
	public String runWorkflow(Workflow workflow, List<WorkflowInput> inputs,
			String user, String metadata) throws AiravataAPIInvocationException {
		try {
			return getClient().runWorkflow(workflow, inputs, user, metadata,workflow.getName()+"_"+Calendar.getInstance().getTime().toString());
		} catch (Exception e) {
			throw new AiravataAPIInvocationException(e);
		}
	}

	@Override
	public Monitor getWorkflowIntanceMonitor(String topic)
			throws AiravataAPIInvocationException {
		return getClient().getWorkflowExecutionMonitor(topic);
	}

	@Override
	public Monitor getWorkflowInstanceMonitor(String topic,
			MonitorEventListener listener)
			throws AiravataAPIInvocationException {
		return getClient().getWorkflowExecutionMonitor(topic,listener);
	}
	
	public AiravataClient getClient() {
		return client;
	}
	public void setClient(AiravataClient client) {
		this.client = client;
	}

}