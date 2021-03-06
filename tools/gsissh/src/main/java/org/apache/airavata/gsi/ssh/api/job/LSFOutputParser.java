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
package org.apache.airavata.gsi.ssh.api.job;

import org.apache.airavata.gsi.ssh.api.SSHApiException;
import org.apache.airavata.gsi.ssh.impl.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LSFOutputParser implements OutputParser {
    private final static Logger logger = LoggerFactory.getLogger(LSFOutputParser.class);

    @Override
    public void parseSingleJob(JobDescriptor jobDescriptor, String rawOutput) throws SSHApiException {
        logger.debug(rawOutput);
        //todo we need to implement this but we are not using it airavata runtime
        // if someone is using the gsissh as a tool this will be useful to get a descriptive information about a single job
    }

    @Override
    public String parseJobSubmission(String rawOutput) throws SSHApiException {
        logger.debug(rawOutput);
        return rawOutput.substring(rawOutput.indexOf("<")+1,rawOutput.indexOf(">"));
    }

    @Override
    public JobStatus parseJobStatus(String jobID, String rawOutput) throws SSHApiException {
        boolean jobFount = false;
        logger.debug(rawOutput);
        //todo this is not used anymore
        return JobStatus.C;
    }

    @Override
    public void parseJobStatuses(String userName, Map<String, JobStatus> statusMap, String rawOutput) throws SSHApiException {
        logger.debug(rawOutput);

        String[]    info = rawOutput.split("\n");
//        int lastStop = 0;
        for (String jobID : statusMap.keySet()) {
            String jobName = jobID.split(",")[1];
            boolean found = false;
            for (int i = 0; i < info.length; i++) {
                if (info[i].contains(jobName.substring(0,8))) {
                    // now starts processing this line
                    logger.info(info[i]);
                    String correctLine = info[i];
                    String[] columns = correctLine.split(" ");
                    List<String> columnList = new ArrayList<String>();
                    for (String s : columns) {
                        if (!"".equals(s)) {
                            columnList.add(s);
                        }
                    }
//                    lastStop = i + 1;
                    try {
                        statusMap.put(jobID, JobStatus.valueOf(columnList.get(2)));
                    }catch(IndexOutOfBoundsException e){
                        statusMap.put(jobID, JobStatus.valueOf("U"));
                    }
                    found = true;
                    break;
                }
            }
            if(!found)
                logger.error("Couldn't find the status of the Job with JobName: " + jobName + "Job Id: " + jobID.split(",")[0]);
        }
    }

    public static void main(String[] args) {
        String test = "Job <2477982> is submitted to queue <short>.";
        System.out.println(test.substring(test.indexOf("<")+1, test.indexOf(">")));
        String test1 = "JOBID   USER    STAT  QUEUE      FROM_HOST   EXEC_HOST   JOB_NAME   SUBMIT_TIME\n" +
                "2636607 lg11w   RUN   long       ghpcc06     c11b02      *069656647 Mar  7 00:58\n" +
                "2636582 lg11w   RUN   long       ghpcc06     c02b01      2134490944 Mar  7 00:48";
        Map<String, JobStatus> statusMap = new HashMap<String, JobStatus>();
        statusMap.put("2477983,2134490944", JobStatus.U);
        LSFOutputParser lsfOutputParser = new LSFOutputParser();
        try {
            lsfOutputParser.parseJobStatuses("cjh", statusMap, test1);
        } catch (SSHApiException e) {
            logger.error(e.getMessage(), e);
        }
        System.out.println(statusMap.get("2477983,2134490944"));

    }
}
