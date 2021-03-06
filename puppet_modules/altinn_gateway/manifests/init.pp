#init.pp
class altinn_gateway (

  String $java_home                         = $altinn_gateway::params::java_home,
  String $log_root                          = $altinn_gateway::params::log_root,
  String $log_level                         = $altinn_gateway::params::log_level,
  String $install_dir                       = $altinn_gateway::params::install_dir,
  String $config_dir                        = $altinn_gateway::params::config_dir,
  String $group_id                          = $altinn_gateway::params::group_id,
  String $artifact_id                       = $altinn_gateway::params::artifact_id,
  String $service_name                      = $altinn_gateway::params::service_name,
  Integer $server_port                      = $altinn_gateway::params::server_port,
  String $auditlog_file                     = $altinn_gateway::params::auditlog_file,
  String $auditlog_dir                      = $altinn_gateway::params::auditlog_dir,
  String $application                       = $altinn_gateway::params::application,
  Integer $connect_timeout                  = $altinn_gateway::params::connect_timeout,
  Integer $read_timeout                    = $altinn_gateway::params::read_timeout,
  String $altinn_url                       = $altinn_gateway::params::altinn_url,
  Boolean $altinn_escape_scopenames        = $altinn_gateway::params::altinn_escape_scopenames,
  String $altinn_keystore_type             = $altinn_gateway::params::altinn_keystore_type,
  String $altinn_keystore_filename         = $altinn_gateway::params::altinn_keystore_filename,
  String $altinn_keystore_password         = $altinn_gateway::params::altinn_keystore_password,
  String $altinn_keystore_key_alias        = $altinn_gateway::params::altinn_keystore_key_alias,
  String $altinn_keystore_key_password     = $altinn_gateway::params::altinn_keystore_key_password,
  String $altinn_rights_keystore_type             = $altinn_gateway::params::altinn_rights_keystore_type,
  String $altinn_rights_keystore_filename         = $altinn_gateway::params::altinn_rights_keystore_filename,
  String $altinn_rights_keystore_password         = $altinn_gateway::params::altinn_rights_keystore_password,
  String $altinn_rights_keystore_key_alias        = $altinn_gateway::params::altinn_rights_keystore_key_alias,
  String $altinn_rights_keystore_key_password     = $altinn_gateway::params::altinn_rights_keystore_key_password,
  String $altinn_gateway_basic_username    = $altinn_gateway::params::altinn_gateway_basic_username,
  String $altinn_gateway_basic_password    = $altinn_gateway::params::altinn_gateway_basic_password,
  String $altinn_gateway_client_id         = $altinn_gateway::params::altinn_gateway_client_id,
  Integer $altinn_gateway_cache_max        = $altinn_gateway::params::altinn_gateway_cache_max,
  Integer $altinn_gateway_cache_expire_s   = $altinn_gateway::params::altinn_gateway_cache_expire_s,
  String $maskinporten_token_endpoint      = $altinn_gateway::params::maskinporten_token_endpoint,
  String $maskinporten_aud                 = $altinn_gateway::params::maskinporten_aud,
  String $altinn_aud_endpoint              = $altinn_gateway::params::altinn_aud_endpoint,
  String $altinn_access_scopes             = $altinn_gateway::params::altinn_access_scopes,
  Integer $server_tomcat_max_threads       = $altinn_gateway::params::server_tomcat_max_threads,
  Integer $server_tomcat_min_spare_threads = $altinn_gateway::params::server_tomcat_min_spare_threads,
  String  $health_show_details             = $altinn_gateway::params::health_show_details,
  String $test_tom_liste                   = $altinn_gateway::params::test_tom_liste,
  String $test_ikke_tilgjengelig           = $altinn_gateway::params::test_ikke_tilgjengelig,
  Boolean $test_mock                       = $altinn_gateway::params::test_mock,
  String $maskinporten_client_kid          = $altinn_gateway::params::maskinporten_client_kid,
  String $tomcat_tmp_dir                   = $altinn_gateway::params::tomcat_tmp_dir,
  String $altinn_authorization_endpoint    = $altinn_gateway::params::altinn_authorization_endpoint,
  String $altinn_api_key                   = $altinn_gateway::params::altinn_api_key

) inherits altinn_gateway::params {

  include platform
  include difilib

  anchor { 'altinn_gateway::begin': } ->
  class { '::altinn_gateway::install': } ->
  class { '::altinn_gateway::deploy': } ->
  class { '::altinn_gateway::test_setup': } ->
  class { '::altinn_gateway::config': } ~>
  class { '::altinn_gateway::service': } ->
  anchor { 'altinn_gateway::end': }
}
