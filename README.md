# CoatRack

[![FIWARE Third Party](https://nexus.lab.fiware.org/static/badges/chapters/api-management.svg)](https://www.fiware.org/developers/catalogue/)
[![License: Apache](https://img.shields.io/github/license/coatrack/coatrack.svg)](https://opensource.org/licenses/Apache-2.0)
[![Docker badge](https://img.shields.io/docker/pulls/coatrack/admin.svg)](https://hub.docker.com/r/coatrack/admin/)
[![SOF support badge](https://nexus.lab.fiware.org/repository/raw/public/badges/stackoverflow/fiware.svg)](http://stackoverflow.com/questions/tagged/fiware)
<br/>
![Status](https://nexus.lab.fiware.org/static/badges/statuses/incubating.svg)
[![CII Best Practices](https://bestpractices.coreinfrastructure.org/projects/4948/badge)](https://bestpractices.coreinfrastructure.org/projects/4948)

CoatRack is a framework to manage backend-to-backend communication via REST services, consisting of:

* distributed, lightweight API gateways and
* a centralized web application to generate and manage those API gateways.

CoatRack can facilitate your work if you have existing REST APIs and you want to do one (or more) of the following: 

* monitoring the access to your APIs,
* authentication/authorization of calls to your APIs via API keys,
* monetization of API calls, based on pay-per-call rules or flatrates.

This project is part of [FIWARE](https://www.fiware.org/). For more information check the FIWARE Catalogue entry for
[API Management](https://github.com/FIWARE/catalogue/tree/master/data-publication).

| :books: [Documentation](https://github.com/coatrack/coatrack/wiki) |  :whale: [Docker Hub](https://hub.docker.com/r/coatrack/admin/) | :dart: [Roadmap](https://github.com/coatrack/coatrack/blob/master/docs/roadmap.md) |
| ----------------------------------------------| ----------------------------------------------------------------| --------------------------------------------------------------------|



## Contents

* [Background](#background)
* [Install](#install)
* [Usage](#usage)
* [License](#license)



## Background

In case you would like to manage backend-to-backend communication via REST APIs, e.g. offering backend services to other parties, some general work is required in addition to developing the actual service API, e.g.:

* implementing mechanisms for authentication/authorisation, 
* providing access credentials to the users, 
* monitoring calls to the API, 
* generating statistics. 

CoatRack facilitates these general tasks, so that you can focus on developing the actual service API. 

CoatRack comprises a central web application and distributed lighweight API gateways, which are:

* generated/configured via the central application and 
* delivering statistics about monitored service API calls to the central application.

The following figure shows the typical CoatRack architecture, the CoatRack web application is depicted on the right and one CoatRack Service gateway is depicted on the left. The calls from the client to the service API are routed and logged by a custom CoatRack Gateway, which can be installed in the service provider's local network. Configuration and statistics are accessible via the CoatRack web application.

![CoatRack architecture overview](./spring-boot/admin/src/main/resources/static/images/coatrack-architecture-overview.png)



## Installation using production-ready Docker Images

 The following prerequisites are required:

*  a Linux shell - Windows users can use WSL of the Git Bash shell instead
*  docker
*  docker-compose



### Deployment For Newcomers

This approach is suggested if you are completely new to CoatRack and want to just want to experience what it actually does. Follow these instructions:

1. Change the `INSERT_SAMPLE_DATA_ON_STARTUP` parameter in `.env` to `true` to initialize the example Gateway in the database.
2. Go to the directory `docker/docker-compose-deployment` and execute:

```sh
bash initialize-databases-if-necessary.sh
```

* The previous step is required to create the databases within PostgreSQL. Now you can set up CoatRack via:

```sh
docker-compose --profile example-gateway up -d
```

* The last step is to check out the service provided by the example Gateway. Search for this URL in your browser:

```http
http://localhost:8088/humidity-by-location?api-key=ee11ee22-ee33-ee44-ee55-ee66ee77ee88
```

* The Example-Gateway should accept the provided API key and redirect you to the website of the `humidity-by-location` service.
* CoatRack can be shutdown by just executing:

```sh
docker-compose --profile example-gateway down
```

* If you want to remove CoatRack and all associated traces, execute:

```sh
bash stop-containers-and-clean-up-traces-if-existent.sh
```



### Deployment for Production

This approach is meant for developers and people who would like to deploy a production-ready CoatRack instance with a clean, empty database and without an Example-Gateway. Just go to the directory `docker-compose-deployment` and execute:

```sh
docker-compose up -d
```

CoatRack will be accessible at `localhost:8080`.



## Operations from Scratch

To build CoatRack from scratch the following prerequisites are required in addition to the dependencies mentioned in the previous section:

* OpenJDK 11
* Maven 3.6.3 or higher



There are two scripts in the `docker` directory helping you with that:

* `build-and-push-images` builds the docker image of each CoatRack modul from source and pushes the images to Dockerhub. This only works, when the docker daemon is already logged in to the CoatRack repository. This script is especially useful within a CI pipeline to update the latest Dockerhub images.
* `build-and-deploy-images-locally` also builds the above mentioned docker images from scratch for local use and deploys a production-ready instance locally using these images. This is especially useful for developers who apply changes to the source code locally and want to test the impact of these changes in a realistic setup.



## Usage

To start using CoatRack: 

- either run the admin application locally, as explained in the [Install](#install) section
- or open the public instance available at (https://coatrack.eu).

To log-in to CoatRack, a Github account is required. After logging in and filling in the registration form, the CoatRack admin application will open. Use the "Getting started tutorials" that are accessible from inside the application to learn the basics of using CoatRack.

There are two tutorials inside the application:

- Offering service APIs via CoatRack
- Using service APIs offered via CoatRack



## License

CoatRack is licenced under [Apache 2.0 License](./LICENSE).

© 2013 - 2021 Corizon | Institut für angewandte Systemtechnik Bremen GmbH (ATB)
