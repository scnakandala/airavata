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

package org.apache.airavata.registry.cpi.utils;

public class Constants {
    public final class FieldConstants {
        public final class ProjectConstants {
            public static final String PROJECT_ID = "projectID";
            public static final String GATEWAY_ID = "gateway_id";
            public static final String OWNER = "owner";
            public static final String PROJECT_NAME = "name";
            public static final String DESCRIPTION = "description";
            public static final String CREATION_TIME = "creationTime";
        }

        public final class ExperimentConstants {
            public static final String EXPERIMENT_NAME = "experimentName";
            public static final String USER_NAME = "userName";
            public static final String GATEWAY = "gatewayId";
            public static final String EXPERIMENT_DESC = "experimentDescription";
            public static final String PROJECT_ID = "projectId";
            public static final String CREATION_TIME = "creationTime";
            public static final String APPLICATION_ID = "applicationId";
            public static final String APPLICATION_VERSION = "applicationVersion";
            public static final String WORKFLOW_TEMPLATE_ID = "workflowTemplateId";
            public static final String WORKFLOW_TEMPLATE_VERSION = "worklfowTemplateVersion";
            public static final String USER_CONFIGURATION_DATA = "userConfigurationData";
            public static final String WORKFLOW_EXECUTION_ID = "workflowExecutionInstanceId";
            public static final String EXPERIMENT_INPUTS = "experimentInputs";
            public static final String EXPERIMENT_OUTPUTS = "experimentOutputs";
            public static final String EXPERIMENT_STATUS = "experimentStatus";
            public static final String STATE_CHANGE_LIST = "stateChangeList";
            public static final String WORKFLOW_NODE_LIST = "workflowNodeDetailsList";
            public static final String ERROR_DETAIL_LIST = "errors";
            public static final String FROM_DATE = "fromDate";
            public static final String TO_DATE = "toDate";

        }

        public final class ConfigurationDataConstants {
            public static final String EXPERIMENT_ID = "experimentId";
            public static final String AIRAVATA_AUTO_SCHEDULE = "airavataAutoSchedule";
            public static final String OVERRIDE_MANUAL_PARAMS = "overrideManualScheduledParams";
            public static final String SHARE_EXP = "shareExperimentPublicly";
            public static final String COMPUTATIONAL_RESOURCE_SCHEDULING = "computationalResourceScheduling";
            public static final String ADVANCED_INPUT_HANDLING = "advanceInputDataHandling";
            public static final String ADVANCED_OUTPUT_HANDLING = "advanceOutputDataHandling";
            public static final String QOS_PARAMS = "qosParams";
        }

        public final class WorkflowNodeConstants {
            public static final String EXPERIMENT_ID = "experimentId";
            public static final String NODE_NAME = "nodeName";
            public static final String NODE_INPUTS = "nodeInputs";
            public static final String NODE_OUTPUTS = "nodeOutputs";
            public static final String WF_NODE_STATUS = "workflowNodeStatus";
            public static final String TASK_LIST = "taskDetailsList";
            public static final String NODE_ERRORS = "errors";
        }

        public final class TaskDetailConstants {
            public static final String EXPERIMENT_ID = "experimentId";
            public static final String NODE_ID = "nodeId";
        }

        public final class JobDetaisConstants {
            public static final String TASK_ID = "taskId";
        }

        public final class DataTransferDetailConstants {
            public static final String TASK_ID = "taskId";
        }

        public final class WorkflowNodeStatusConstants {
            public static final String EXPERIMENT_ID = "experimentId";
        }

        public final class ErrorDetailsConstants {
            public static final String EXPERIMENT_ID = "experimentId";
            public static final String NODE_ID = "nodeId";
            public static final String TASK_ID = "taskId";
            public static final String JOB_ID = "jobId";
        }
    }
}
