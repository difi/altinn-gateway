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
  Integer $read_timeout                     = $altinn_gateway::params::read_timeout,
  String $altinn_url                        = $altinn_gateway::params::altinn_url,
  String $altinn_keystore_type              = $altinn_gateway::params::altinn_keystore_type,
  String $altinn_keystore_filename          = $altinn_gateway::params::altinn_keystore_filename,
  String $altinn_keystore_password          = $altinn_gateway::params::altinn_keystore_password,
  String $altinn_keystore_key_alias         = $altinn_gateway::params::altinn_keystore_key_alias,
  String $altinn_keystore_key_password      = $altinn_gateway::params::altinn_keystore_key_password,
  String $altinn_gateway_basic_username     = $altinn_gateway::params::altinn_gateway_basic_username,
  String $altinn_gateway_basic_password     = $altinn_gateway::params::altinn_gateway_basic_password,
  String $altinn_gateway_client_id          = $altinn_gateway::params::altinn_gateway_client_id,
  String $maskinporten_url                  = $altinn_gateway::params::maskinporten_url,
  String $altinn_aud_endpoint               = $altinn_gateway::params::altinn_aud_endpoint,
  String $altinn_access_scopes              = $altinn_gateway::params::altinn_access_scopes,
  Integer $server_tomcat_max_threads        = $altinn_gateway::params::server_tomcat_max_threads,
  Integer $server_tomcat_min_spare_threads  = $altinn_gateway::params::server_tomcat_min_spare_threads,
  Boolean $health_details_hide              = $altinn_gateway::params::health_details_hide,
  String $test_tom_liste                    = $altinn_gateway::params::test_tom_liste,
  String $test_ikke_tilgjengelig            = $altinn_gateway::params::test_ikke_tilgjengelig,
  Boolean $test_mock                        = $altinn_gateway::params::test_mock,

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
