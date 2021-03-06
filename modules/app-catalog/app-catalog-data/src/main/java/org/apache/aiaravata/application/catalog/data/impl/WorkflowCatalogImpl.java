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

package org.apache.aiaravata.application.catalog.data.impl;

import org.airavata.appcatalog.cpi.AppCatalogException;
import org.airavata.appcatalog.cpi.WorkflowCatalog;
import org.apache.aiaravata.application.catalog.data.resources.*;
import org.apache.aiaravata.application.catalog.data.util.AppCatalogThriftConversion;
import org.apache.aiaravata.application.catalog.data.util.AppCatalogUtils;
import org.apache.airavata.model.Workflow;
import org.apache.airavata.model.appcatalog.appinterface.InputDataObjectType;
import org.apache.airavata.model.appcatalog.appinterface.OutputDataObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowCatalogImpl implements WorkflowCatalog {
    private final static Logger logger = LoggerFactory.getLogger(WorkflowCatalogImpl.class);

    @Override
    public List<String> getAllWorkflows(String gatewayId) throws AppCatalogException {
        List<String> workflowIds = new ArrayList<String>();
        try {
            WorkflowResource resource = new WorkflowResource();
            resource.setGatewayId(gatewayId);
            workflowIds = resource.getAllIds();
        } catch (Exception e) {
            logger.error("Error while retrieving all the workflow template ids...", e);
            throw new AppCatalogException(e);
        }
        return workflowIds;
    }

    @Override
    public Workflow getWorkflow(String workflowTemplateId) throws AppCatalogException {
        try {
            WorkflowResource resource = new WorkflowResource();
            WorkflowResource wfResource = (WorkflowResource)resource.get(workflowTemplateId);
            return AppCatalogThriftConversion.getWorkflow(wfResource);
        } catch (Exception e) {
            logger.error("Error while retrieving the workflow...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void deleteWorkflow(String workflowTemplateId) throws AppCatalogException {
        try {
            WorkflowResource resource = new WorkflowResource();
            resource.remove(workflowTemplateId);
        } catch (Exception e) {
            logger.error("Error while deleting the workflow...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String registerWorkflow(Workflow workflow, String gatewayId) throws AppCatalogException {
        try {
            WorkflowResource resource = new WorkflowResource();
            resource.setWfTemplateId(AppCatalogUtils.getID(workflow.getName()));
            resource.setWfName(workflow.getName());
            resource.setGraph(workflow.getGraph());
            resource.setGatewayId(gatewayId);
            if (workflow.getImage() != null){
                resource.setImage(new String(workflow.getImage()));
            }
            resource.save();
            workflow.setTemplateId(resource.getWfTemplateId());
            List<InputDataObjectType> workflowInputs = workflow.getWorkflowInputs();
            if (workflowInputs != null && workflowInputs.size() != 0){
                for (InputDataObjectType input : workflowInputs){
                    WorkflowInputResource wfInputResource = new WorkflowInputResource();
                    wfInputResource.setWorkflowResource(resource);
                    wfInputResource.setInputKey(input.getName());
                    wfInputResource.setInputVal(input.getValue());
                    wfInputResource.setWfTemplateId(resource.getWfTemplateId());
                    wfInputResource.setDataType(input.getType().toString());
                    wfInputResource.setAppArgument(input.getApplicationArgument());
                    wfInputResource.setStandardInput(input.isStandardInput());
                    wfInputResource.setUserFriendlyDesc(input.getUserFriendlyDescription());
                    wfInputResource.setMetadata(input.getMetaData());
                    wfInputResource.save();
                }
            }
            List<OutputDataObjectType> workflowOutputs = workflow.getWorkflowOutputs();
            if (workflowOutputs != null && workflowOutputs.size() != 0){
                for (OutputDataObjectType output : workflowOutputs){
                    WorkflowOutputResource outputResource = new WorkflowOutputResource();
                    outputResource.setWorkflowResource(resource);
                    outputResource.setOutputKey(output.getName());
                    outputResource.setOutputVal(output.getValue());
                    outputResource.setWfTemplateId(resource.getWfTemplateId());
                    outputResource.setDataType(output.getType().toString());
                    outputResource.save();
                }
            }
            return resource.getWfTemplateId();
        } catch (Exception e) {
            logger.error("Error while saving the workflow...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void updateWorkflow(String workflowTemplateId, Workflow workflow) throws AppCatalogException {
        try {
            WorkflowResource resource = new WorkflowResource();
            WorkflowResource existingWF = (WorkflowResource)resource.get(workflowTemplateId);
            existingWF.setWfName(workflow.getName());
            existingWF.setGraph(workflow.getGraph());
            if (workflow.getImage() != null){
                existingWF.setImage(new String(workflow.getImage()));
            }
            existingWF.save();
            List<InputDataObjectType> existingwFInputs = workflow.getWorkflowInputs();
            if (existingwFInputs != null && existingwFInputs.size() != 0){
                for (InputDataObjectType input : existingwFInputs){
                    WorkflowInputResource wfInputResource = new WorkflowInputResource();
                    Map<String, String> ids = new HashMap<String, String>();
                    ids.put(AbstractResource.WFInputConstants.WF_TEMPLATE_ID,existingWF.getWfTemplateId());
                    ids.put(AbstractResource.WFInputConstants.INPUT_KEY,input.getName());
                    WorkflowInputResource existingInput = (WorkflowInputResource)wfInputResource.get(ids);
                    existingInput.setWorkflowResource(existingWF);
                    existingInput.setInputKey(input.getName());
                    existingInput.setInputVal(input.getValue());
                    existingInput.setWfTemplateId(existingWF.getWfTemplateId());
                    existingInput.setDataType(input.getType().toString());
                    existingInput.setAppArgument(input.getApplicationArgument());
                    existingInput.setStandardInput(input.isStandardInput());
                    existingInput.setUserFriendlyDesc(input.getUserFriendlyDescription());
                    existingInput.setMetadata(input.getMetaData());
                    existingInput.save();
                }
            }
            List<OutputDataObjectType> workflowOutputs = workflow.getWorkflowOutputs();
            if (workflowOutputs != null && workflowOutputs.size() != 0){
                for (OutputDataObjectType output : workflowOutputs){
                    WorkflowOutputResource outputResource = new WorkflowOutputResource();
                    Map<String, String> ids = new HashMap<String, String>();
                    ids.put(AbstractResource.WFOutputConstants.WF_TEMPLATE_ID,existingWF.getWfTemplateId());
                    ids.put(AbstractResource.WFOutputConstants.OUTPUT_KEY,output.getName());
                    WorkflowOutputResource existingOutput = (WorkflowOutputResource)outputResource.get(ids);
                    existingOutput.setWorkflowResource(existingWF);
                    existingOutput.setOutputKey(output.getName());
                    existingOutput.setOutputVal(output.getValue());
                    existingOutput.setWfTemplateId(existingWF.getWfTemplateId());
                    existingOutput.setDataType(output.getType().toString());
                    existingOutput.save();
                }
            }
        } catch (Exception e) {
            logger.error("Error while updating the workflow...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public String getWorkflowTemplateId(String workflowName) throws AppCatalogException {
        try {
            WorkflowResource resource = new WorkflowResource();
            List<Resource> resourceList = resource.get(AbstractResource.WorkflowConstants.WF_NAME, workflowName);
            if (resourceList != null && !resourceList.isEmpty()){
                WorkflowResource wfResource = (WorkflowResource)resourceList.get(0);
                return wfResource.getWfTemplateId();
            }
        } catch (Exception e) {
            logger.error("Error while retrieving the workflow with the workflow name...", e);
            throw new AppCatalogException(e);
        }
        return null;
    }

    @Override
    public boolean isWorkflowExistWithName(String workflowName) throws AppCatalogException {
        try {
            WorkflowResource resource = new WorkflowResource();
            List<Resource> resourceList = resource.get(AbstractResource.WorkflowConstants.WF_NAME, workflowName);
            if (resourceList != null && !resourceList.isEmpty()){
                return true;
            }
        } catch (Exception e) {
            logger.error("Error while retrieving the workflow with the workflow name...", e);
            throw new AppCatalogException(e);
        }
        return false;
    }

    @Override
    public void updateWorkflowOutputs(String workflowTemplateId, List<OutputDataObjectType> workflowOutputs) throws AppCatalogException {
        WorkflowResource resource = new WorkflowResource();
        WorkflowResource existingWF = (WorkflowResource)resource.get(workflowTemplateId);
        if (workflowOutputs != null && workflowOutputs.size() != 0) {
            for (OutputDataObjectType output : workflowOutputs) {
                WorkflowOutputResource outputResource = new WorkflowOutputResource();
                Map<String, String> ids = new HashMap<String, String>();
                ids.put(AbstractResource.WFOutputConstants.WF_TEMPLATE_ID, existingWF.getWfTemplateId());
                ids.put(AbstractResource.WFOutputConstants.OUTPUT_KEY, output.getName());
                WorkflowOutputResource existingOutput = (WorkflowOutputResource) outputResource.get(ids);
                existingOutput.setWorkflowResource(existingWF);
                existingOutput.setOutputKey(output.getName());
                existingOutput.setOutputVal(output.getValue());
                existingOutput.setWfTemplateId(existingWF.getWfTemplateId());
                existingOutput.setDataType(output.getType().toString());
                existingOutput.save();
            }
        }
    }
}
