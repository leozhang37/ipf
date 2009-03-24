/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.flow.extend

import static org.apache.camel.builder.Builder.*

import org.apache.camel.impl.SerializationDataFormat
import org.apache.camel.spring.SpringRouteBuilder

/**
 * @author Martin Krasser
 */
class GroovyFlowRouteBuilder extends SpringRouteBuilder {
    
    void configure() {
        
        def serialization = new SerializationDataFormat()
        
        errorHandler(noErrorHandler())
        
        // --------------------------------------------------------------
        //  Linear Flows
        // --------------------------------------------------------------

        from("direct:flow-test-1")
            .initFlow("test-1")
                .application("test")
                .outType(String.class)
            .setHeader("foo", constant("test-1"))
            .to("mock:mock")
            .ackFlow()

        from("direct:flow-test-2")
            .initFlow("test-2")
                .application("test")
                .inFormat(serialization)
                .outFormat(serialization)
            .setHeader("foo", constant("test-2"))
            .to("mock:mock")
            .ackFlow()

        from("direct:flow-test-3")
            .initFlow("test-3")
                .application("test")
                .inFormat(serialization)
                .outConversion(false)
            .setHeader("foo", constant("test-3"))
            .to("mock:mock")
            .ackFlow()

        from("direct:flow-test-4")
            .initFlow("test-4")
                .application("test")
                .outType(String.class)
            .setHeader("foo", constant("test-4"))
            .dedupeFlow()
            .to("mock:mock")
            .ackFlow()

        from("direct:flow-test-5")
            .initFlow("test-5")
                .application("test")
                .outType(String.class)
            .throwFault("unhandled fault")
            .to("mock:mock")

        from("direct:flow-test-6")
            .initFlow("test-6")
                .replayErrorHandler("mock:error")
                .application("test")
                .outType(String.class)
            .throwFault("handled fault")
            .to("mock:mock")
            
        // --------------------------------------------------------------
        //  Split Flows (original Camel splitter)
        // --------------------------------------------------------------
        
        from("direct:flow-test-split")
            .initFlow("test-split")
                .application("test")
                .outType(String.class)
            .multicast()
            .to("direct:out-1")
            .to("direct:out-2")
    
        from("direct:out-1")
            .to("mock:mock-1")
            .ackFlow()
    
        from("direct:out-2")
            .to("mock:mock-2")
            .ackFlow()

        // --------------------------------------------------------------
        //  Pipe Flows
        // --------------------------------------------------------------
        
        from("direct:flow-test-pipe")
            .initFlow("test-pipe")
                .application("test")
                .outType(String.class)
            .to("direct:out-1")
            .to("direct:out-2")
        
    }
    
}