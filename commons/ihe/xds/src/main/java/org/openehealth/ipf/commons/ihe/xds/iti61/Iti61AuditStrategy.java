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
package org.openehealth.ipf.commons.ihe.xds.iti61;

import org.openehealth.ipf.commons.ihe.core.atna.AuditorManager;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsSubmitAuditDataset;
import org.openehealth.ipf.commons.ihe.xds.core.audit.XdsSubmitAuditStrategy30;

/**
 * Audit strategy for ITI-61.
 * @author Dmytro Rud
 */
public class Iti61AuditStrategy extends XdsSubmitAuditStrategy30 {

    public Iti61AuditStrategy(boolean serverSide) {
        super(serverSide);
    }


    @Override
    public void doAudit(XdsSubmitAuditDataset auditDataset) {
        AuditorManager.getCustomXdsAuditor().auditIti61(
                isServerSide(),
                auditDataset.getEventOutcomeCode(),
                auditDataset.getUserId(),
                auditDataset.getUserName(),
                auditDataset.getServiceEndpointUrl(),
                auditDataset.getClientIpAddress(),
                auditDataset.getSubmissionSetUuid(),
                auditDataset.getPatientId(),
                auditDataset.getPurposesOfUse());
    }

}
