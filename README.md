# Programming Lecture: Eclipse Artemis
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
