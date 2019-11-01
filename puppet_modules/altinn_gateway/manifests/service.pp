#service.pp
class altinn_gateway::service inherits altinn_gateway {

  include platform

  if ($platform::deploy_spring_boot) {
    service { $altinn_gateway::service_name:
      ensure => running,
      enable => true,
    }
  }
}
