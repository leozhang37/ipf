/*
 * Copyright 2014 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.dispatch

import org.apache.camel.impl.DefaultExchange
import org.junit.BeforeClass
import org.junit.Test
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTestContainer

import static org.junit.Assert.assertEquals
import static org.openehealth.ipf.platform.camel.hl7.HL7v2.validatingProcessor

/**
 * @author Dmytro Rud
 */
class TestDispatch1 extends TestDispatch {
    static final String CONTEXT_DESCRIPTOR = 'dispatch/dispatch1.xml'


    static void main(args) {
        init(CONTEXT_DESCRIPTOR, true)
    }

    @BeforeClass
    static void setUpClass() {
        init(CONTEXT_DESCRIPTOR, false)
    }

    @Override
    protected String getDispatcherPort() {
        '18500'
    }
}
