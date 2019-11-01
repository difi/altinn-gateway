#
class altinn_gateway::test_setup inherits altinn_gateway {


  if ($platform::test_setup) {
    file { "${altinn_gateway::config_dir}${altinn_gateway::application}/config/difi-virkscert-altinn.jks":
      ensure => 'file',
      source => "puppet:///modules/${caller_module_name}/difi-virkscert-altinn.jks",
      owner  => $altinn_gateway::tomcat_user,
      group  => $altinn_gateway::tomcat_group,
      mode   => '0644',
      notify => Class['altinn_gateway::Service'],
    }
  }
}