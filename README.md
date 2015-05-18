# Alchemy Inject

 - [Overview](#overview)
 - [Usage](#usage)
   - [Adding gradle dependency](#adding-gradle-dependency)
   - [Adding maven dependency](#adding-maven-dependency)
   - [Marking modules for discovery](#marking-modules-for-discovery)
   - [Creating the injector](#creating-the-injector)
   - [Excluding modules while creating the injector](#excluding-modules-while-creating-the-injector)
 - [Demo](#demo)
 - [Contributing](#contributing)
 - [Copyright and license](#copyright-and-license)


## Overview
A guice module discovery module with support for multiple environments. Alchemy Inject tries to deal with the problem of creating guice injector in a reliable and decentralized manner. The idea is to write a guice module and mark it with an environment it is relevant it and it should be applied.

The injector also contains a discovery mechanism for discovering [Jackson] modules and auto injecting the ObjectMapper.

## Usage

### Adding gradle dependency

```
compile 'com.strandls.alchemy:alchemy-inject:0.9'


```

### Adding maven dependency

```
<dependency>
	<groupId>com.strandls.alchemy</groupId>
	<artifactId>alchemy-inject</artifactId>
	<version>0.9</version>
</dependency>

```

### Marking modules for discovery

You use Alchemy Inject by annotating your guice modules with the AlchemyModule annotation like so

```
import com.google.inject.AbstractModule;
import com.strandls.alchemy.inject.AlchemyModule;
import com.strandls.alchemy.inject.AlchemyModule.Environment;

/**
 * Bindings for authentication.
 *
 * @author ashish
 *
 */
@AlchemyModule(Environment.All)
public class AuthModule extends AbstractModule {
 .
 .
 .
```

A module is be annotated with an environment it is to be used in. Three values are supported today.

 - **Prod** - this module should be used in production settings
 - **Test** - this module should be used in test settings
 - **All** - this module should be used across both test and production settings

### Creating the injector

Create a guice injector using all production modules, including modules annotated with environment All as well, like so

```
import com.google.inject.Injector;
import com.strandls.alchemy.inject.AlchemyModule.Environment;
import com.strandls.alchemy.inject.AlchemyModuleLister;

Injector injector = Guice.createInjector(new AlchemyModuleLister().getModules(Environment.Prod))
```

Note you can now keep adding newer guice modules without having to worry about changing the injector creatiion code.

### Excluding modules while creating the injector

With large complex projects you might hit a case where you have conflicting bindings. With Alchemy inject you could resolve these bindings or filter out some modules using a configuration file placed in your application classpath.

The file, written in .ini format, should be named **alchemy-modules.ini**. Here is a sample file

```
[Prod]
filter=(?i).*dummy.*
filter=com.strandls.alchemy.webservices.auth.AuthModule

[Test]
filter=com.strandls.alchemy.webservices.client.StaticCredentialsModule
filter=com.strandls.alchemy.webservices.client.JaxRsClientModule

```

With this configuration file all modules with dummy (ignoring case) in there fully qualified class name, will be filtered out from the production environment. The filter expressions are [JavaRegex][Java regular expressions].


## Demo

The [Alchemy Rest Client Demo][ARCDemo] project is a good demostration of real life use of this module.

## Contributing



Please refer to [Contribution Guidlines][Contrib] if you are not familiar with contributing to open source projects.

The gist for making a contibution is

1. [Fork]
2. Create a topic branch - `git checkout -b <your branch>`
3. Make your changes
4. Push to your branch - `git push origin <your branch>`
5. Create an [Issue] with a link to your branch

#### Setting up eclipse
Run
```
gradle/gradlew eclipse
```

Import alchemy inject to eclipse using File > Import > Existing Projects into Workspace

The project has been setup to auto format the code via eclipse save actions. Please try not to disturb this.


## Copyright and license

Code and documentation copyright 2015 [Strand Life Sciences]. Code released under the [Apache License 2.0]. Docs released under Creative Commons.

[ARCDemo]:https://github.com/strandls/alchemy-rest-client-demo/
[Alchemy Inject]:https://github.com/strandls/alchemy-inject/
[Apache License 2.0]:http://www.apache.org/licenses/LICENSE-2.0.html
[Strand Life Sciences]:http://www.strandls.com/
[Fork]: http://help.github.com/forking/
[Issues]: https://github.com/strandls/alchemy-rest-client-demo/issues
[Contrib]: https://guides.github.com/activities/contributing-to-open-source/
[Jackson]: https://github.com/FasterXML/jackson
[JavaRegex]: http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html

