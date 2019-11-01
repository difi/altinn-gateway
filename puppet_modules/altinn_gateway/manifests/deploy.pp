# deploy
class altinn_gateway::deploy inherits altinn_gateway {

  difilib::spring_boot_deploy { $altinn_gateway::application:
    package      => $altinn_gateway::group_id,
    artifact     => $altinn_gateway::artifact_id,
    service_name => $altinn_gateway::service_name,
    install_dir  => "${altinn_gateway::install_dir}${altinn_gateway::application}",
  }
}
