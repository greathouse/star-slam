Codename: star-slam
=========
Deployment Tracking Service

This project is intended to track the artifacts of software projects deployed to production. Desired features include:
* New/Updated Dependencies
* Config File Changes
* SQL Schema Changes

This is currently the pet project du jour. It is pre-pre-alpha. Version: 0.0.0.0.

Technologies in use
* Groovy - http://groovy.codehaus.org/
* * KnockoutJS - http://knockoutjs.com/
* ~~Ratpack Framework - http://www.ratpack-framework.org/~~
* ~~Google Guice - https://code.google.com/p/google-guice/~~
* Spring-mvc & Spring-ioc


Fully tested, including REST API endpoints.

NOTE ABOUT RATPACK<br />
This project started out using Ratpack+Google Guice. However, Ratpack is still under <em>heavy</em> development. Every update seemed to break compatibility. For that reason, this project has been switched over to use Spring-MVC (with Spring-IOC). It is a shame, because Google Guice would be nice.
