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
import org.airavata.appcatalog.cpi.ApplicationDeployment;
import org.apache.aiaravata.application.catalog.data.resources.*;
import org.apache.aiaravata.application.catalog.data.util.AppCatalogThriftConversion;
import org.apache.aiaravata.application.catalog.data.util.AppCatalogUtils;
import org.apache.airavata.model.appcatalog.appdeployment.ApplicationDeploymentDescription;
import org.apache.airavata.model.appcatalog.appdeployment.ApplicationParallelismType;
import org.apache.airavata.model.appcatalog.appdeployment.SetEnvPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationDeploymentImpl implements ApplicationDeployment {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationDeploymentImpl.class);

    @Override
    public String addApplicationDeployment(ApplicationDeploymentDescription deploymentDescription, String gatewayId) throws AppCatalogException {
        try {
            AppDeploymentResource deploymentResource = new AppDeploymentResource();
            ComputeResourceResource computeHostResource = new ComputeResourceResource();
            AppModuleResource moduleResource = new AppModuleResource();
            if (!computeHostResource.isExists(deploymentDescription.getComputeHostId())){
                logger.error("Compute host does not exist in the system. Please create a Compute host first...");
                throw new AppCatalogException("Compute host does not exist in the system. Please create a Compute host first...");
            }
            if (!moduleResource.isExists(deploymentDescription.getAppModuleId())){
                logger.error("Application module does not exist in the system. Please create an application module first...");
                throw new AppCatalogException("Application module does not exist in the system. Please create an application module first...");
            }
            AppModuleResource module = (AppModuleResource)moduleResource.get(deploymentDescription.getAppModuleId());
            ComputeResourceResource hostResource = (ComputeResourceResource) computeHostResource.get(deploymentDescription.getComputeHostId());
            deploymentResource.setDeploymentId(hostResource.getHostName() + "_" + deploymentDescription.getAppModuleId());
            deploymentResource.setAppModuleId(deploymentDescription.getAppModuleId());
            deploymentResource.setModuleResource(module);
            deploymentResource.setHostId(deploymentDescription.getComputeHostId());
            deploymentResource.setHostResource(hostResource);
            deploymentResource.setAppDes(deploymentDescription.getAppDeploymentDescription());
            deploymentResource.setExecutablePath(deploymentDescription.getExecutablePath());
            deploymentResource.setGatewayId(gatewayId);
            ApplicationParallelismType parallelism = deploymentDescription.getParallelism();
            if (parallelism != null){
                deploymentResource.setParallelism(parallelism.toString());
            }
            deploymentResource.save();
            deploymentDescription.setAppDeploymentId(deploymentResource.getDeploymentId());

            List<String> moduleLoadCmds = deploymentDescription.getModuleLoadCmds();
            if (moduleLoadCmds != null && !moduleLoadCmds.isEmpty()){
                for (String cmd : moduleLoadCmds){
                    ModuleLoadCmdResource cmdResource = new ModuleLoadCmdResource();
                    cmdResource.setAppDeploymentId(deploymentDescription.getAppDeploymentId());
                    cmdResource.setCmd(cmd);
                    cmdResource.save();
                }
            }

            List<String> preJobCommands = deploymentDescription.getPreJobCommands();
            if (preJobCommands != null && !preJobCommands.isEmpty()){
                for (String cmd : preJobCommands){
                    PreJobCommandResource cmdResource = new PreJobCommandResource();
                    cmdResource.setAppDeploymentId(deploymentDescription.getAppDeploymentId());
                    cmdResource.setCommand(cmd);
                    cmdResource.save();
                }
            }

            List<String> postJobCommands = deploymentDescription.getPostJobCommands();
            if (postJobCommands != null && !postJobCommands.isEmpty()){
                for (String cmd : postJobCommands){
                    PostJobCommandResource cmdResource = new PostJobCommandResource();
                    cmdResource.setAppDeploymentId(deploymentDescription.getAppDeploymentId());
                    cmdResource.setCommand(cmd);
                    cmdResource.save();
                }
            }

            List<SetEnvPaths> libPrependPaths = deploymentDescription.getLibPrependPaths();
            if (libPrependPaths != null && !libPrependPaths.isEmpty()){
                for (SetEnvPaths path : libPrependPaths){
                    LibraryPrepandPathResource prepandPathResource = new LibraryPrepandPathResource();
                    prepandPathResource.setAppDeploymentResource(deploymentResource);
                    prepandPathResource.setName(path.getName());
                    prepandPathResource.setValue(path.getValue());
                    prepandPathResource.setDeploymentId(deploymentResource.getDeploymentId());
                    prepandPathResource.save();
                }
            }

            List<SetEnvPaths> libApendPaths = deploymentDescription.getLibAppendPaths();
            if (libApendPaths != null && !libApendPaths.isEmpty()){
                for (SetEnvPaths path : libApendPaths){
                    LibraryApendPathResource apendPathResource = new LibraryApendPathResource();
                    apendPathResource.setAppDeploymentResource(deploymentResource);
                    apendPathResource.setName(path.getName());
                    apendPathResource.setValue(path.getValue());
                    apendPathResource.setDeploymentId(deploymentResource.getDeploymentId());
                    apendPathResource.save();
                }
            }
            List<SetEnvPaths> setEnvironment = deploymentDescription.getSetEnvironment();
            if (setEnvironment != null && !setEnvironment.isEmpty()){
                for (SetEnvPaths path : setEnvironment){
                    AppEnvironmentResource environmentResource = new AppEnvironmentResource();
                    environmentResource.setAppDeploymentResource(deploymentResource);
                    environmentResource.setName(path.getName());
                    environmentResource.setValue(path.getValue());
                    environmentResource.setDeploymentId(deploymentResource.getDeploymentId());
                    environmentResource.save();
                }
            }
            return deploymentResource.getDeploymentId();
        }catch (Exception e) {
            logger.error("Error while saving application deployment...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void updateApplicationDeployment(String deploymentId, ApplicationDeploymentDescription updatedDeployment) throws AppCatalogException {
        try {
            AppDeploymentResource deploymentResource = new AppDeploymentResource();
            AppDeploymentResource existingDep = (AppDeploymentResource)deploymentResource.get(deploymentId);
            ComputeResourceResource computeHostResource = new ComputeResourceResource();
            AppModuleResource moduleResource = new AppModuleResource();
            if (!computeHostResource.isExists(updatedDeployment.getComputeHostId())){
                logger.error("Compute host does not exist in the system. Please create a Compute host first...");
                throw new AppCatalogException("Compute host does not exist in the system. Please create a Compute host first...");
            }
            if (!moduleResource.isExists(updatedDeployment.getAppModuleId())){
                logger.error("Application module does not exist in the system. Please create an application module first...");
                throw new AppCatalogException("Application module does not exist in the system. Please create an application module first...");
            }
            AppModuleResource module = (AppModuleResource)moduleResource.get(updatedDeployment.getAppModuleId());
            existingDep.setAppModuleId(updatedDeployment.getAppModuleId());
            existingDep.setModuleResource(module);
            existingDep.setHostId(updatedDeployment.getComputeHostId());
            existingDep.setHostResource((ComputeResourceResource)computeHostResource.get(updatedDeployment.getComputeHostId()));
            existingDep.setAppDes(updatedDeployment.getAppDeploymentDescription());
            existingDep.setExecutablePath(updatedDeployment.getExecutablePath());
            if (updatedDeployment.getParallelism() != null){
                deploymentResource.setParallelism(updatedDeployment.getParallelism().toString());
            }

            existingDep.save();

            // remove existing module load commands
            ModuleLoadCmdResource cmdResource = new ModuleLoadCmdResource();
            Map<String, String> ids = new HashMap<String, String>();
            ids.put(AbstractResource.ModuleLoadCmdConstants.APP_DEPLOYMENT_ID, deploymentId);
            cmdResource.remove(ids);
            List<String> moduleLoadCmds = updatedDeployment.getModuleLoadCmds();
            if (moduleLoadCmds != null && !moduleLoadCmds.isEmpty()){
                for (String cmd : moduleLoadCmds){
                    ids = new HashMap<String, String>();
                    ids.put(AbstractResource.ModuleLoadCmdConstants.APP_DEPLOYMENT_ID, deploymentId);
                    ids.put(AbstractResource.ModuleLoadCmdConstants.CMD, cmd);
                    if (cmdResource.isExists(ids)){
                        cmdResource = (ModuleLoadCmdResource)cmdResource.get(ids);
                    }
                    cmdResource.setCmd(cmd);
                    cmdResource.setAppDeploymentResource(existingDep);
                    cmdResource.setAppDeploymentId(deploymentId);
                    cmdResource.save();
                }
            }

            PreJobCommandResource preJobCommandResource = new PreJobCommandResource();
            ids = new HashMap<String, String>();
            ids.put(AbstractResource.PreJobCommandConstants.DEPLOYMENT_ID, deploymentId);
            preJobCommandResource.remove(ids);
            List<String> preJobCommands = updatedDeployment.getPreJobCommands();
            if (preJobCommands != null && !preJobCommands.isEmpty()){
                for (String cmd : preJobCommands){
                    ids = new HashMap<String, String>();
                    ids.put(AbstractResource.PreJobCommandConstants.DEPLOYMENT_ID, deploymentId);
                    ids.put(AbstractResource.PreJobCommandConstants.COMMAND, cmd);
                    if (preJobCommandResource.isExists(ids)){
                        preJobCommandResource = (PreJobCommandResource)preJobCommandResource.get(ids);
                    }
                    preJobCommandResource.setCommand(cmd);
                    preJobCommandResource.setAppDeploymentResource(existingDep);
                    preJobCommandResource.setAppDeploymentId(deploymentId);
                    preJobCommandResource.save();
                }
            }

            PostJobCommandResource postJobCommandResource = new PostJobCommandResource();
            ids = new HashMap<String, String>();
            ids.put(AbstractResource.PostJobCommandConstants.DEPLOYMENT_ID, deploymentId);
            postJobCommandResource.remove(ids);
            List<String> postJobCommands = updatedDeployment.getPostJobCommands();
            if (postJobCommands != null && !postJobCommands.isEmpty()){
                for (String cmd : postJobCommands){
                    ids = new HashMap<String, String>();
                    ids.put(AbstractResource.PostJobCommandConstants.DEPLOYMENT_ID, deploymentId);
                    ids.put(AbstractResource.PostJobCommandConstants.COMMAND, cmd);
                    if (postJobCommandResource.isExists(ids)){
                        postJobCommandResource = (PostJobCommandResource)postJobCommandResource.get(ids);
                    }
                    postJobCommandResource.setCommand(cmd);
                    postJobCommandResource.setAppDeploymentResource(existingDep);
                    postJobCommandResource.setAppDeploymentId(deploymentId);
                    postJobCommandResource.save();
                }
            }

            // remove existing lib prepand paths
            LibraryPrepandPathResource prepandPathResource = new LibraryPrepandPathResource();
            ids = new HashMap<String, String>();
            ids.put(AbstractResource.LibraryPrepandPathConstants.DEPLOYMENT_ID, deploymentId);
            prepandPathResource.remove(ids);
            List<SetEnvPaths> libPrependPaths = updatedDeployment.getLibPrependPaths();
            if (libPrependPaths != null && !libPrependPaths.isEmpty()){
                for (SetEnvPaths path : libPrependPaths){
                    ids = new HashMap<String, String>();
                    ids.put(AbstractResource.LibraryPrepandPathConstants.DEPLOYMENT_ID, deploymentId);
                    ids.put(AbstractResource.LibraryPrepandPathConstants.NAME, path.getName());
                    if (prepandPathResource.isExists(ids)){
                        prepandPathResource = (LibraryPrepandPathResource)prepandPathResource.get(ids);
                    }
                    prepandPathResource.setAppDeploymentResource(existingDep);
                    prepandPathResource.setName(path.getName());
                    prepandPathResource.setValue(path.getValue());
                    prepandPathResource.setDeploymentId(deploymentId);
                    prepandPathResource.save();
                }
            }

            List<SetEnvPaths> libApendPaths = updatedDeployment.getLibAppendPaths();
            // remove lib append paths
            LibraryApendPathResource apendPathResource = new LibraryApendPathResource();
            ids = new HashMap<String, String>();
            ids.put(AbstractResource.LibraryApendPathConstants.DEPLOYMENT_ID, deploymentId);
            apendPathResource.remove(ids);
            if (libApendPaths != null && !libApendPaths.isEmpty()){
                for (SetEnvPaths path : libApendPaths){
                    ids = new HashMap<String, String>();
                    ids.put(AbstractResource.LibraryApendPathConstants.DEPLOYMENT_ID, deploymentId);
                    ids.put(AbstractResource.LibraryApendPathConstants.NAME, path.getName());
                    if (apendPathResource.isExists(ids)){
                        apendPathResource = (LibraryApendPathResource)apendPathResource.get(ids);
                    }
                    apendPathResource.setAppDeploymentResource(existingDep);
                    apendPathResource.setName(path.getName());
                    apendPathResource.setValue(path.getValue());
                    apendPathResource.setDeploymentId(deploymentId);
                    apendPathResource.save();
                }
            }

            List<SetEnvPaths> setEnvironment = updatedDeployment.getSetEnvironment();
            // remove existing setEnvPaths
            AppEnvironmentResource environmentResource = new AppEnvironmentResource();
            ids = new HashMap<String, String>();
            ids.put(AbstractResource.AppEnvironmentConstants.DEPLOYMENT_ID, deploymentId);
            environmentResource.remove(ids);
            if (setEnvironment != null && !setEnvironment.isEmpty()){
                for (SetEnvPaths path : setEnvironment){
                    ids = new HashMap<String, String>();
                    ids.put(AbstractResource.AppEnvironmentConstants.DEPLOYMENT_ID, deploymentId);
                    ids.put(AbstractResource.AppEnvironmentConstants.NAME, path.getName());
                    if (environmentResource.isExists(ids)){
                        environmentResource = (AppEnvironmentResource)environmentResource.get(ids);
                    }
                    environmentResource.setAppDeploymentResource(existingDep);
                    environmentResource.setName(path.getName());
                    environmentResource.setValue(path.getValue());
                    environmentResource.setDeploymentId(deploymentId);
                    environmentResource.save();
                }
            }
        }catch (Exception e) {
            logger.error("Error while updating application deployment...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public ApplicationDeploymentDescription getApplicationDeployement(String deploymentId) throws AppCatalogException {
        try {
            AppDeploymentResource deploymentResource = new AppDeploymentResource();
            AppDeploymentResource appDep = (AppDeploymentResource)deploymentResource.get(deploymentId);
            return AppCatalogThriftConversion.getApplicationDeploymentDescription(appDep);
        }catch (Exception e) {
            logger.error("Error while retrieving application deployment...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public List<ApplicationDeploymentDescription> getApplicationDeployements(Map<String, String> filters) throws AppCatalogException {
        List<ApplicationDeploymentDescription> deploymentDescriptions = new ArrayList<ApplicationDeploymentDescription>();
        try {
            AppDeploymentResource resource = new AppDeploymentResource();
            boolean firstTry=true;
            for (String fieldName : filters.keySet() ){
                List<ApplicationDeploymentDescription> tmpDescriptions = new ArrayList<ApplicationDeploymentDescription>();
                if (fieldName.equals(AbstractResource.ApplicationDeploymentConstants.APP_MODULE_ID)){
                    List<Resource> resources = resource.get(AbstractResource.ApplicationDeploymentConstants.APP_MODULE_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                    	tmpDescriptions = AppCatalogThriftConversion.getAppDepDescList(resources);
                    }
                }else if (fieldName.equals(AbstractResource.ApplicationDeploymentConstants.COMPUTE_HOST_ID)){
                    List<Resource> resources = resource.get(AbstractResource.ApplicationDeploymentConstants.COMPUTE_HOST_ID, filters.get(fieldName));
                    if (resources != null && !resources.isEmpty()){
                    	tmpDescriptions = AppCatalogThriftConversion.getAppDepDescList(resources);
                    }
                } else {
                    logger.error("Unsupported field name for app deployment.", new IllegalArgumentException());
                    throw new IllegalArgumentException("Unsupported field name for app deployment.");
                }
                if (firstTry){
                	deploymentDescriptions.addAll(tmpDescriptions);
                    firstTry=false;
                }else{
                    List<String> ids=new ArrayList<String>();
                	for (ApplicationDeploymentDescription applicationDeploymentDescription : deploymentDescriptions) {
						ids.add(applicationDeploymentDescription.getAppDeploymentId());
					}
                    List<ApplicationDeploymentDescription> tmp2Descriptions = new ArrayList<ApplicationDeploymentDescription>();
                	for (ApplicationDeploymentDescription applicationDeploymentDescription : tmpDescriptions) {
						if (ids.contains(applicationDeploymentDescription.getAppDeploymentId())){
							tmp2Descriptions.add(applicationDeploymentDescription);
						}
					}
                	deploymentDescriptions.clear();
                	deploymentDescriptions.addAll(tmp2Descriptions);
                }
            }
        }catch (Exception e){
            logger.error("Error while retrieving app deployment list...", e);
            throw new AppCatalogException(e);
        }
        return deploymentDescriptions;
    }

    @Override
    public List<ApplicationDeploymentDescription> getAllApplicationDeployements(String gatewayId) throws AppCatalogException {
        List<ApplicationDeploymentDescription> deploymentDescriptions = new ArrayList<ApplicationDeploymentDescription>();
        try {
            AppDeploymentResource resource = new AppDeploymentResource();
            resource.setGatewayId(gatewayId);
            List<Resource> resources = resource.getAll();
            if (resources != null && !resources.isEmpty()){
                deploymentDescriptions = AppCatalogThriftConversion.getAppDepDescList(resources);
            }

        }catch (Exception e){
            logger.error("Error while retrieving app deployment list...", e);
            throw new AppCatalogException(e);
        }
        return deploymentDescriptions;
    }

    @Override
    public List<String> getAllApplicationDeployementIds() throws AppCatalogException {
        try {
            AppDeploymentResource resource = new AppDeploymentResource();
            return resource.getAllIds();
        }catch (Exception e){
            logger.error("Error while retrieving app deployment list...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public boolean isAppDeploymentExists(String deploymentId) throws AppCatalogException {
        try {
           AppDeploymentResource deploymentResource = new AppDeploymentResource();
            return deploymentResource.isExists(deploymentId);
        }catch (Exception e){
            logger.error("Error while retrieving app deployment...", e);
            throw new AppCatalogException(e);
        }
    }

    @Override
    public void removeAppDeployment(String deploymentId) throws AppCatalogException {
        try {
            AppDeploymentResource deploymentResource = new AppDeploymentResource();
            deploymentResource.remove(deploymentId);
        }catch (Exception e){
            logger.error("Error while deleting app deployment...", e);
            throw new AppCatalogException(e);
        }
    }
}
