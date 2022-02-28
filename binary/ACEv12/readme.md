PGP SupportPac for App Connect Enterprise
======================================================

About
-----
Since ACE doen't support configurable services anymore, the plugin has been reworked to reflect these changes.
The underlying code has been changed to get it functional again.
 - Connection with brokerproxy has been removed
 - Policies are directly accessed via ```MbPolicy mbPol = getPolicy("UserDefined", getPgpPolicy());```

All references to configurable services (comments, name, variables) have been replaced by policy or policies.

Because of this rework, you will need to readd/reconfigure these nodes in your message flow(s) and redeploy the flow(s).   

Attention points
----------------
Since ACE requires all policies to be defined in a policy project, you need to either define a default policy project
or you need to supply the policy project name along with the PGP policy name.
 - default policy project:

![](image/default_pp.png)
 - dedicated policy project:

![](image/dedicated_pp.png)


Installation
------------
**Runtime:**
If you have a configured the node to use a dedicated lilPath, copy the files from .\lib to that directory. 
Else copy the contents from .\lib to %MQSI_BASE_FILEPATH%\server\jplugin for windows or to 
$MQSI_BASE_FILEPATH/server/jplugin for linux

**Toolkit:**
Copy the contents from ./plugins to %MQSI_BASE_FILEPATH%\tools\plugins for windows or to
$MQSI_BASE_FILEPATH/tools/plugins for linux