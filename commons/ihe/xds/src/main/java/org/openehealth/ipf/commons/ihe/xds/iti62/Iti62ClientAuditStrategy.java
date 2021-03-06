/*
 * Copyright 2013 the original author or authors.
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
package org.openehealth.ipf.commons.ihe.xds.iti62;

import org.openehealth.ipf.commons.ihe.core.atna.AuditorManager;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsRemoveAuditDataset;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsRemoveAuditStrategy30;

/**
 * Client audit strategy for ITI-62.
 * @author Boris Stanojevic
 */
public class Iti62ClientAuditStrategy extends XdsRemoveAuditStrategy30 {

    /**
     * Constructs the audit strategy.
     */
    public Iti62ClientAuditStrategy() {
        super(false);
    }

    @Override
    public void doAudit(XdsRemoveAuditDataset auditDataset) {
        AuditorManager.getCustomXdsAuditor().auditClientIti62(
                auditDataset.getEventOutcomeCode(),
                auditDataset.getUserId(),
                auditDataset.getUserName(),
                auditDataset.getServiceEndpointUrl(),
                auditDataset.getPatientId(),
                auditDataset.getPurposesOfUse());
    }

}
