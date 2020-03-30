package no.difi.altinn.audit;

import no.idporten.log.audit.AuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLog {

    private final AuditLogger auditLogger;

    @Autowired
    public AuditLog(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    public void rightLookup(String pid, String orgNo) {
        auditLogger.log(AuditMessages.rightLookup.getMessageId(), pid, orgNo);
    }

    enum AuditMessages {
        rightLookup("AG_1");

        private String messageId;

        AuditMessages(String messageId) {
            this.messageId = messageId;
        }

        public String getMessageId() {
            return messageId;
        }
    }

}
