#params.pp
class altinn_gateway::params {
  $java_home                        = hiera('platform::java_home')
  $log_root                         = '/var/log/'
  $log_level                        = 'WARN'
  $install_dir                      = '/opt/'
  $config_dir                       = '/etc/opt/'
  $group_id                         = 'no.difi.altinn'
  $artifact_id                      = 'altinn-gateway'
  $service_name                     = 'altinn-gateway'
  $server_port                      = 10015
  $auditlog_file                    = 'audit.log'
  $auditlog_dir                     = '/var/log/altinn-gateway/audit'
  $application                      = 'altinn-gateway'
  $connect_timeout                  = 10000
  $read_timeout                     = 10000
  $altinn_url                       = 'https://folkeregisteret-api-konsument.sits.no/folkeregisteret/offentlig-med-hjemmel/api/v1/personer'
  $altinn_keystore_type             = 'JKS'
  $altinn_keystore_filename         = 'difi-virkscert-altinn.jks'
  $altinn_keystore_password         = 'oBr8YZuZsbic4gpP'
  $altinn_keystore_key_alias        = 'test'
  $altinn_keystore_key_password     = 'oBr8YZuZsbic4gpP'
  $server_tomcat_max_threads        = 200
  $server_tomcat_min_spare_threads  = 10
  $health_details_hide              = false
}
