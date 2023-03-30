# Programming Lecture: Eclipse Artemis
[![Chat on Matrix](https://matrix.to/img/matrix-badge.svg)](https://matrix.to/#/#eclipse-artemis:kit.edu)
[![GitHub issues](https://img.shields.io/github/issues/kit-sdq/programming-lecture-eclipse-artemis.svg?style=square)](https://github.com/kit-sdq/programming-lecture-eclipse-artemis/issues)
[![GitHub license](https://img.shields.io/badge/license-EPL_2.0-blue.svg?style=square)](https://github.com/kit-sdq/programming-lecture-eclipse-artemis/blob/main/LICENSE)


Eclipse-Plugin for grading with the [Artemis Project](https://github.com/ls1intum/Artemis)

## Features
* [Grading Support](https://kit-sdq.github.io/programming-lecture-eclipse-artemis.docs/grading/grading/) for Java Projects in Artemis

## Update Site
The Update Site is located here: https://kit-sdq.github.io/programming-lecture-eclipse-artemis/

**Note: The update site also contains the development dependencies for the project.**

## Using the plugin(s) / Development of the plugin(s)
Please take a look at our [Docs](https://kit-sdq.github.io/programming-lecture-eclipse-artemis.docs/).

### Update the Versions
See [Tycho Plugin](https://www.eclipse.org/tycho/sitedocs/tycho-release/tycho-versions-plugin/set-version-mojo.html#tycho-versions-set-version). Run with `products` profile: `mvn -Pproducts -DnewVersion=X.X.X tycho-versions:set-version`
