/*
 * Copyright 2009 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti64

import org.apache.camel.spring.SpringRouteBuilder

import static org.openehealth.ipf.platform.camel.hl7.HL7v2.*

/**
 * Camel route for generic unit tests.
 * @author Boris Stanojevic
 */
class Iti64TestRouteBuilder extends SpringRouteBuilder {

    void configure() throws Exception {

    from('xpid-iti64://0.0.0.0:18491')
        .onException(Exception.class)
            .maximumRedeliveries(0)
            .end()
        .process(validatingProcessor())
        .transform(ack())
        .process(validatingProcessor())

    }
}
 
