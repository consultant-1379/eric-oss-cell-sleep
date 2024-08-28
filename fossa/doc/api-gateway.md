# IDUN-48013 API Gateway for authentication
## Docker
The docker image can be build with the Dockerfile at the root of this project.
For that first an ``mvn install`` is needed to build the artifacts.
After that the image can be built via
```bash
docker build . -t selidocker.seli.gic.ericsson.se/proj-cbpirates/eric-oss-cell-sleep/eric-oss-cell-sleep:1.0.ezszila
```
After success and a local try out (``docker run --rm ..``) the image can be pushed with the tag.
## For the future to check
The cell-sleep MS doesn't have a docker repo created at armdocker. This will need to be created later on. For now we can use the CBPirates one.

## Api Gateway setup
### Information Sources
1. [API-Gateway Developer Guide](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/IDUN/API-Gateway+Developer+Guide?src=breadcrumbs-parent): Contains the information how the automatic setup could be done.
2. [Example commit for single-route](https://gerrit.ericsson.se/#/c/10882274/): This contains an example commit with the added hooks and configuration for one path
3. [Details about defining a helm hook](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/IDUN/API-Gateway+Dynamic+Service+Provisioning) : see 5.2. Option 2.
### Status
The hooks has been created, the service can be deployed using the chart, it gets executed.
It is currently configured to use hall100.
### Install
You can perform an installation with the command below:
```bash
helm install eric-oss-cell-sleep-ezszila -n eiap-ecson charts/eric-oss-cell-sleep/
```
Uninstall it:
``` bash
helm uninstall eric-oss-cell-sleep-ezszila -n eiap-ecson
```
### Validation
This is the part remaining. IAM can be queried from a TS/VTE via https://iam.eiap-ecson.hall100-x1.ews.gic.ericsson.se/
In case we feel the configuration is not working, ie the route-configmap is incorrect, then third information source can be used as a guide.


