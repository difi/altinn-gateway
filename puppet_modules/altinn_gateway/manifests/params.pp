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
  $altinn_url                       = 'https://at23.altinn.cloud/maskinporten-api'
  $altinn_escape_scopenames         = false
  $altinn_rights_keystore_type             = 'JKS'
  $altinn_rights_keystore_filename         = 'difi-virkscert-altinn.jks'
  $altinn_rights_keystore_password         = 'oBr8YZuZsbic4gpP'
  $altinn_rights_keystore_key_alias        = 'avsender'
  $altinn_rights_keystore_key_password     = 'oBr8YZuZsbic4gpP'
  $altinn_keystore_type             = 'JKS'
  $altinn_keystore_filename         = 'difi-virkscert-altinn.jks'
  $altinn_keystore_password         = 'oBr8YZuZsbic4gpP'
  $altinn_keystore_key_alias        = 'avsender'
  $altinn_keystore_key_password     = 'oBr8YZuZsbic4gpP'
  $altinn_gateway_basic_username    = 'user'
  $altinn_gateway_basic_password    = 'password'
  $altinn_gateway_client_id         = 'altinn-gateway'
  $altinn_gateway_cache_max = 500
  $altinn_gateway_cache_expire_s = 50
  $maskinporten_token_endpoint      = 'https://oidc-test1.difi.eon.no/idporten-oidc-provider/token'
  $maskinporten_aud                 = 'https://oidc-test1.difi.eon.no/idporten-oidc-provider/'
  $altinn_aud_endpoint              = 'https://tt02.altinn.no/maskinporten-api/'
  $altinn_access_scopes             = 'altinn:maskinporten/delegations.admin'
  $server_tomcat_max_threads        = 200
  $server_tomcat_min_spare_threads  = 10
  $health_show_details              = 'always'
  $test_tom_liste                   = null
  $test_ikke_tilgjengelig           = null
  $test_mock                        = false
  $maskinporten_client_kid          = ''
  $tomcat_tmp_dir                   = '/opt/altinn-gateway/tmp'
  $altinn_authorization_endpoint    = 'https://tt02.altinn.no/api/serviceowner/authorization'
  $altinn_api_key                   = '86926452-CE1B-4960-9BA6-7E28D8DE3A53'
}
