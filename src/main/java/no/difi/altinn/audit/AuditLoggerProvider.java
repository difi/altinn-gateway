package no.difi.altinn.audit;

import no.idporten.log.audit.AuditLogger;
import no.idporten.log.audit.AuditLoggerELFImpl;
import no.idporten.log.elf.ELFWriter;
import no.idporten.log.elf.FileRollerDailyImpl;
import no.idporten.log.elf.WriterCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditLoggerProvider {

    @Value("${application.logging.audit.dir}")
    private String auditLogDir;

    @Value("${application.logging.audit.file}")
    private String auditLogFile;

    @Value("${application.logging.audit.dataSeparator}")
    private String auditLogDataSeparator;

    @Bean
    public AuditLogger auditLogger() {
        ELFWriter elfWriter = new ELFWriter(
                new FileRollerDailyImpl(auditLogDir, auditLogFile),
                new WriterCreator()
        );
        AuditLoggerELFImpl logger = new AuditLoggerELFImpl();
        logger.setELFWriter(elfWriter);
        logger.setDataSeparator(auditLogDataSeparator);
        return logger;
    }

}
