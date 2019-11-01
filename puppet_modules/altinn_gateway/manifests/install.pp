#install.pp
class altinn_gateway::install inherits altinn_gateway {

  user { $altinn_gateway::service_name:
    ensure => present,
    shell  => '/sbin/nologin',
    home   => '/',
  } ->
  file { "${altinn_gateway::config_dir}${altinn_gateway::application}":
    ensure => 'directory',
    mode   => '0755',
    owner  => $altinn_gateway::service_name,
    group  => $altinn_gateway::service_name,
  } ->
  file { "${altinn_gateway::config_dir}${altinn_gateway::application}/config":
    ensure => 'directory',
    owner  => $altinn_gateway::service_name,
    group  => $altinn_gateway::service_name,
    mode   => '0755',
  } ->
  file { "${altinn_gateway::log_root}${altinn_gateway::application}":
    ensure => 'directory',
    mode   => '0755',
    owner  => $altinn_gateway::service_name,
    group  => $altinn_gateway::service_name,
  } ->
  file { $altinn_gateway::auditlog_dir:
    ensure => 'directory',
    mode   => '0755',
    owner  => $altinn_gateway::service_name,
    group  => $altinn_gateway::service_name,
  } ->
  file { "${altinn_gateway::install_dir}${altinn_gateway::application}":
    ensure => 'directory',
    mode   => '0644',
    owner  => $altinn_gateway::service_name,
    group  => $altinn_gateway::service_name,
  }

  difilib::spring_boot_logrotate { $altinn_gateway::application:
    application => $altinn_gateway::application,
  }

  if ($platform::install_cron_jobs) {
    $log_cleanup_command = "find ${altinn_gateway::log_root}${altinn_gateway::application}/ -type f -name \"*.gz\" -mtime +7 -exec rm -f {} \\;"
    $auditlog_cleanup_command = "find ${altinn_gateway::log_root}${altinn_gateway::application}/audit/ -type f -name \"*audit.log\" -mtime +7 -exec rm -f {} \\;"

    cron { "${altinn_gateway::application}_log_cleanup":
      command => $log_cleanup_command,
      user    => 'root',
      hour    => '03',
      minute  => '00',
    } ->
      cron { "${altinn_gateway::application}_log_cleanup_audit":
        command => $auditlog_cleanup_command,
        user    => 'root',
        hour    => '03',
        minute  => '05',
      }
  }

}
