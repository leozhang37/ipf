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
package org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept;

import org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.Hl7v2Interceptor;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpEndpoint;


/**
 * Camel interceptor interface for PIX/PDQ transactions.
 * @author Dmytro Rud
 */
public interface MllpInterceptor<T extends MllpEndpoint> extends Hl7v2Interceptor {

    /**
     * Returns the endpoint instance to which this interceptor belongs.
     */
    public T getMllpEndpoint();

}
