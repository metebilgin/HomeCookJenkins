#!groovy

import jenkins.model.*
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule


def instance = Jenkins.getInstance()
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def adminUsername = System.getenv('JENKINS_USERNAME') ?: 'admin'
def adminPassword = System.getenv('JENKINS_PASSWORD') ?: 'password'

hudsonRealm.createAccount(adminUsername, adminPassword)
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)
instance.getDescriptor("jenkins.CLI").get().setEnabled(false)
instance.injector.getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false);
instance.setSlaveAgentPort(-1);
HashSet<String> newProtocols = new HashSet<>(instance.getAgentProtocols());
newProtocols.removeAll(Arrays.asList( "JNLP3-connect", "JNLP2-connect", "JNLP-connect", "CLI-connect" ));
instance.setAgentProtocols(newProtocols);
instance.save()
