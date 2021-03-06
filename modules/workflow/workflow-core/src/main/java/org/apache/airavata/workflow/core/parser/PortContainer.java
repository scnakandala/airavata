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

package org.apache.airavata.workflow.core.parser;

import org.apache.airavata.workflow.core.dag.port.InPort;
import org.apache.airavata.workflow.model.graph.DataPort;


public class PortContainer {
    private DataPort dataPort;
    private InPort inPort;


    public PortContainer(DataPort dataPort, InPort inPort) {
        this.dataPort = dataPort;
        this.inPort = inPort;
    }

    public DataPort getDataPort() {
        return dataPort;
    }

    public void setDataPort(DataPort dataPort) {
        this.dataPort = dataPort;
    }

    public InPort getInPort() {
        return inPort;
    }

    public void setInPort(InPort inPort) {
        this.inPort = inPort;
    }
}
