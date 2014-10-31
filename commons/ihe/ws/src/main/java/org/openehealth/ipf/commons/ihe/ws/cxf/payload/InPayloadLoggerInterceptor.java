/*
 * Copyright 2012 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.ws.cxf.payload;

import lombok.experimental.Delegate;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.AttachmentInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.AbstractSafeInterceptor;

import java.util.Collection;
import java.util.Collections;

/**
 * CXF interceptor which stores incoming HTTP payload
 * into files with user-defined name patterns.
 * <p>
 * Members of {@link WsPayloadLoggerBase} are mixed into this class.
 *
 * @author Dmytro Rud
 */
class InPayloadLoggerInterceptor extends AbstractSafeInterceptor {
    @Delegate private final WsPayloadLoggerBase base = new WsPayloadLoggerBase();

    public InPayloadLoggerInterceptor(String fileNamePattern) {
        super(Phase.RECEIVE);
        addAfter(AttachmentInInterceptor.class.getName());
        setFileNamePattern(fileNamePattern);
    }


    @Override
    public Collection<PhaseInterceptor<? extends Message>> getAdditionalInterceptors() {
        return Collections.<PhaseInterceptor<? extends Message>> singletonList(
                new InPayloadExtractorInterceptor(StringPayloadHolder.PayloadType.HTTP));
    }


    @Override
    public void process(SoapMessage message) {
        if (canProcess()) {
            logPayload(message);
        }
    }
}
