# 
class altinn_gateway::config inherits altinn_gateway {

  file { "${altinn_gateway::install_dir}${altinn_gateway::application}/${altinn_gateway::artifact_id}.conf":
    ensure  => 'file',
    content => template("${module_name}/${altinn_gateway::artifact_id}.conf.erb"),
    owner   => $altinn_gateway::service_name,
    group   => $altinn_gateway::service_name,
    mode    => '0400',
  } ->
  file { "${altinn_gateway::config_dir}${altinn_gateway::application}/application.yaml":
    ensure  => 'file',
    content => template("${module_name}/application.yaml.erb"),
    owner   => $altinn_gateway::service_name,
    group   => $altinn_gateway::service_name,
    mode    => '0400',
  } ->
  file { "/etc/rc.d/init.d/${altinn_gateway::service_name}":
    ensure => 'link',
    target => "${altinn_gateway::install_dir}${altinn_gateway::application}/${altinn_gateway::artifact_id}.jar",
  }

  difilib::logback_config { $altinn_gateway::application:
    application       => $altinn_gateway::application,
    owner             => $altinn_gateway::service_name,
    group             => $altinn_gateway::service_name,
    performance_class => 'no.difi.altinn.gateway.logging.performance',
    loglevel_no       => $altinn_gateway::log_level,
    loglevel_nondifi  => $altinn_gateway::log_level,
  }

}
