.. reviewed 0.4
.. include:: /replace.rst
.. default-domain:: py

Deployment descriptors
----------------------

A Java coweb server instance typically consists of an admin servlet acting as a directory of running sessions and a CometD servlet governing session events bundled into a web application archive (WAR). Configuration of both servlets is driven by standard web deployment descriptors (i.e., :file:`web.xml` files).

The generation of deployment descriptors for new coweb applications requires:

#. The installation of the coweb Maven modules.
#. The use of the Maven :file:`coweb-archetype`.

Both of these requirements are satisfied by the various Maven build scripts included in the source distribution and documented under :doc:`/tutorial/install`.

Generating a deployment descriptor
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To generate a deployment descriptor for a new coweb project, use the Maven `coweb-archetype` as described in the :ref:`maven-archetype` section.

This archetype produces a deployment descriptor under :file:`src/main/webapp/WEB-INF/web.xml` in the project directory. The generated file configures one `org.coweb.servlet.AdminServlet`_ and one `org.cometd.server.CometdServlet`_ managing any number of independent sessions.

.. sourcecode:: xml

   <?xml version="1.0" encoding="UTF-8"?>
   <web-app xmlns="http://java.sun.com/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
            version="2.5">

       <servlet>
           <servlet-name>cometd</servlet-name>
           <servlet-class>org.cometd.server.CometdServlet</servlet-class>
           <init-param>
               <param-name>logLevel</param-name>
               <param-value>0</param-value>
           </init-param>
           <init-param>
               <param-name>timeout</param-name>
               <param-value>30000</param-value>
           </init-param>
           <init-param>
               <param-name>jsonDebug</param-name>
               <param-value>false</param-value>
           </init-param>
           <async-supported>true</async-supported>
           <load-on-startup>1</load-on-startup>
       </servlet>
       <servlet-mapping>
           <servlet-name>cometd</servlet-name>
           <url-pattern>/cometd/*</url-pattern>
       </servlet-mapping>

       <servlet>
           <servlet-name>admin</servlet-name>
           <servlet-class>org.coweb.servlet.AdminServlet</servlet-class>
           <load-on-startup>2</load-on-startup>
       </servlet>
       <servlet-mapping>
           <servlet-name>admin</servlet-name>
           <url-pattern>/admin/*</url-pattern>
       </servlet-mapping>
   </web-app>

Configuring CometD options
~~~~~~~~~~~~~~~~~~~~~~~~~~

The CometD servlet accepts all of the `CometD 2 Java Server
configuration`_ options. The defaults are acceptable for coweb applications in
most situations. If you are developing locally, you may wish to set the
`maxSessionsPerBrowser` option to `-1` to allow any number of local tabs or
windows from the same browser to join the session without delay.

.. sourcecode:: xml

   <init-param>
      <param-name>maxSessionsPerBrowser</param-name>
      <param-value>-1</param-value>
   </init-param>

Configuring coweb options
~~~~~~~~~~~~~~~~~~~~~~~~~

coweb configuration is specified within a configuration json file. To use a custom coweb configuration, set the ``ConfigURI`` init parameter to the AdminServlet in your :file:`WEB-INF/web.xml`. 

:file:`WEB-INF/web.xml`
################################

.. sourcecode:: xml

   <servlet>
      <servlet-name>admin</servlet-name>
      <servlet-class>org.coweb.servlet.AdminServlet</servlet-class>
      <load-on-startup>2</load-on-startup>
      <init-param>
	      <param-name>ConfigURI</param-name>
	      <param-value>/WEB-INF/cowebConfig.json</param-value>
      </init-param>
   </servlet>

The contents of :file:`cowebConfig.json` are considered a JSON object. The following are available properties that can be specified in the config json file:

updaterTypeMatcherClass (java class name)
   String name of a :class:`org.coweb.UpdaterTypeMatcher` subclass to use to match an Updater Type for a late joiner.

logLevel (integer)
   Log Level value of the coweb logger. Valid values are 0 (WARNING), 1 (INFO) and 2 (FINE). The default is 0.

captureIncoming (string)
   Path to a file where the captured incoming data will be written.

captureOutgoing (string)
   Path to a file where the captured outgoing data will be written.

moderatorIsUpdater (boolean)
	The moderator will be the updater for late joiners to an OCW session. You will probably want to specify a custom `org.coweb.SessionModerator` class with the `sessionModerator` option (below).

sessionModerator (java class name)
   String name of a :class:`org.coweb.SessionModerator` subclass to use for managing session moderation.

operationEngine (boolean)
   Boolean indicating if server-side operation engine should be used.

.. note::
	`moderatorIsUpdater` set to true implies `operationEngine` being true. In other words, if `moderatorIsUpdater` is true, then the server will automatically use the server-side operation engine.

cacheState (boolean)
   Boolean indicting if state should be cached.

bots (json object array)
   Array of bot json configuration objects. Each bot configuration objects provides a "service" property which is the service name. It can optionally provide a "broker" property that is a name of a :class:`org.coweb.bots.Transport` subclass.

Custom Updater Type Matcher
################################################

Say a certain app deployment requires the coweb server to control user access
to sessions and their abilities in the session. The cowebConfig.json file
configures a custom Updater Type Matcher.

Contents of the cowebConfig.json will look like the following.

.. sourcecode:: json

   {
       "updaterTypeMatcherClass": "org.someorg.CustomUpdaterTypeMatcher"
   }
