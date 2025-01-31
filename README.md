# R&S Store

## Introduction

The goal of the project is the development of a multi-user store where users can register accounts and buy regional products.
This system will be composed by a _backend component_ and a _frontend application_.

The _backend component_ is comprised by:
- A PostgreSQL Relational Database Management System (RDBMS), responsible for storing all the system's durable data, including the messages.
- A JVM-based application, responsible for:
    - Exposing an HTTP API, used by the _frontend application_, with all the functionality provided by the system.
    - Interacting with the RDBMS.
    - Ensuring all the domain requirements, namely data mutation integrity and access control.
- Other required sub-components, such as load balancers.

The _frontend application_ will run on the end user browsers and will be responsible for:
- Presenting a user interface.
- Interacting with the HTTP API exposed by the _backend component_, in order to implement all the functionalities of the system.

## Functionalities

### User interface

The user interface provided by the _frontend application_ allow the following interactions:

* Registration of an user.
* Authentication of an user, given an username and password.
* Listing of all products available in the store.
* Listing of all products available in the store, filtered by a predicate.
* Purchase of a product.

#### Authenticated functionalities
* Announcement of a new product.
* Removal of a product.
* Eddition of a product.

## Not implemented functionalities

The following functionalities were not yet implemented in the project:

* Registration of an user.
* Authentication of an user, given an username and password.
* Listing of all products available in the store.
* Listing of all products available in the store, filtered by a predicate.
* Purchase of a product.

* Announcement of a new product.
* Removal of a product.
* Eddition of a product.
