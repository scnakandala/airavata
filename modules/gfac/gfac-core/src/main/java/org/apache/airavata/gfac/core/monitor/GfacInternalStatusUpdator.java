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
package org.apache.airavata.gfac.core.monitor;

import java.io.File;
import java.io.IOException;

import org.apache.airavata.common.exception.ApplicationSettingsException;
import org.apache.airavata.common.utils.AiravataZKUtils;
import org.apache.airavata.common.utils.Constants;
import org.apache.airavata.common.utils.ServerSettings;
import org.apache.airavata.common.utils.listener.AbstractActivityListener;
import org.apache.airavata.gfac.core.monitor.state.GfacExperimentStateChangeRequest;
import org.apache.airavata.gfac.core.states.GfacExperimentState;
import org.apache.airavata.gfac.core.utils.GFacUtils;
import org.apache.airavata.messaging.core.Publisher;
import org.apache.airavata.messaging.core.impl.RabbitMQProducer;
import org.apache.airavata.messaging.core.impl.RabbitMQTaskLaunchConsumer;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

public class GfacInternalStatusUpdator implements AbstractActivityListener, Watcher {
    private final static Logger logger = LoggerFactory.getLogger(GfacInternalStatusUpdator.class);

    private ZooKeeper zk;

    private static Integer mutex = -1;

    @Subscribe
    public void updateZK(GfacExperimentStateChangeRequest statusChangeRequest) throws Exception {
        logger.info("Gfac internal state changed to: " + statusChangeRequest.getState().toString());
        MonitorID monitorID = statusChangeRequest.getMonitorID();
        String experimentNode = ServerSettings.getSetting(Constants.ZOOKEEPER_GFAC_EXPERIMENT_NODE, "/gfac-experiments");
        String experimentPath = experimentNode + File.separator + ServerSettings.getSetting(Constants.ZOOKEEPER_GFAC_SERVER_NAME)
                + File.separator + statusChangeRequest.getMonitorID().getExperimentID();
        Stat exists = null;
        if(!(GfacExperimentState.COMPLETED.equals(statusChangeRequest.getState()) || GfacExperimentState.FAILED.equals(statusChangeRequest.getState()))) {
            try {
                if (!zk.getState().isConnected()) {
                    String zkhostPort = AiravataZKUtils.getZKhostPort();
                    zk = new ZooKeeper(zkhostPort, AiravataZKUtils.getZKTimeout(), this);
                    logger.info("Waiting for zookeeper to connect to the server");
                    synchronized (mutex) {
                        mutex.wait(5000);
                    }
                }
                exists = zk.exists(experimentPath, false);
                if (exists == null) {
                    logger.error("ZK path: " + experimentPath + " does not exists !!");
                    logger.error("Zookeeper is in an inconsistent state !!! ");
                    return;
                }
            } catch (KeeperException e) {
                logger.error("Error while updating zk", e);
                throw new Exception(e.getMessage(), e);
            } catch (InterruptedException e) {
                logger.error("Error while updating zk", e);
                throw new Exception(e.getMessage(), e);
            } catch (IOException e) {
                logger.error("Error while updating zk", e);
                throw new Exception(e.getMessage(), e);
            }
            Stat state = zk.exists(experimentPath + File.separator + AiravataZKUtils.ZK_EXPERIMENT_STATE_NODE, false);
            if (state == null) {
                // state znode has to be created
                zk.create(experimentPath + File.separator + AiravataZKUtils.ZK_EXPERIMENT_STATE_NODE,
                        String.valueOf(statusChangeRequest.getState().getValue()).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                zk.setData(experimentPath + File.separator + AiravataZKUtils.ZK_EXPERIMENT_STATE_NODE,
                        String.valueOf(statusChangeRequest.getState().getValue()).getBytes(), state.getVersion());
            }
        }
        switch (statusChangeRequest.getState()) {
            case COMPLETED:
                logger.info("Experiment Completed, So removing the ZK entry for the experiment" + monitorID.getExperimentID());
                logger.info("Zookeeper experiment Path: " + experimentPath);
                break;
            case FAILED:
                logger.info("Experiment Failed, So removing the ZK entry for the experiment" + monitorID.getExperimentID());
                logger.info("Zookeeper experiment Path: " + experimentPath);
                break;
            default:
        }
    }

    public void setup(Object... configurations) {
        for (Object configuration : configurations) {
            if (configuration instanceof ZooKeeper) {
                this.zk = (ZooKeeper) configuration;
            }
        }
    }

    public void process(WatchedEvent watchedEvent) {
        logger.info(watchedEvent.getPath());
        synchronized (mutex) {
            Event.KeeperState state = watchedEvent.getState();
            if (state == Event.KeeperState.SyncConnected) {
                mutex.notify();
            }
        }
    }
}
