<?php

$domain=ereg_replace('[^\.]*\.(.*)$','\1',$_SERVER['HTTP_HOST']);
$group_name=ereg_replace('([^\.]*)\..*$','\1',$_SERVER['HTTP_HOST']);
$themeroot='http://r-forge.r-project.org/themes/rforge/';

echo '<?xml version="1.0" encoding="UTF-8"?>';
?>
<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title><?php echo $group_name; ?></title>
	<link href="<?php echo $themeroot; ?>styles/estilo1.css" rel="stylesheet" type="text/css" />
  </head>

<body>

<h1>IRConnection</h1>
<p>
<a href="http://www.rforge.net/Rserve/">Rserve</a> is a de facto standard of how to connect to R. Apart from using a server
<a href="http://www.rforge.net/JRI/">JRI</a> provides another tool that allows you to connect to java.</p> 
<p>
Both packages provide a very similar API but are not interchangeable once you use one of both for your projects.
IRConnection is ment to provide a wrapper interface so you can interchange both of those.
</p>
<p>
Alas IRConnection does not facilitate the installation of neither Rserve nor JRI. The one you want to use needs to be installed correctly first
and how to acchieve is best read from the manual of this software.
</p>
<p>
One thing can be acchieved more easily though, and that is the starting/stopping of the Rserve once you need connections.
My objective with the following spring-based approach is to reuse a running Rserve (when available) or else to create a running
server.
</p>
<h2>Preconditions</h2>
<p>
You must have R installed and an Rserve that fits your current system. On my current windows 7 intel 64bit platform I run
R <span style="font-weight:bold">x86_64-pc-mingw32/x64 (64-bit)</span> with Rserve <span>Rserve_x64.exe</span>. This configuration
works, but on your system you might have another version of R installed and another brand of Rserve must be used.
When you start Rserve manually you should face the following output:
<pre>
C:\Program Files\R\R-2.12.1\bin\x64>R CMD Rserve_x64.exe
Rserve: Ok, ready to answer queries.
</pre>
</p>
<p>
Now when you are developing java application you do not want to start Rserve manually again every new day. So you can use a bit of spring configuration
to support automatic start, or participation on a running process:
<div>spring configuration (as found in r-service)</div>
<pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd"&gt;

	&lt;bean name="rConnectionFactory" class="org.rosuda.rengine.REngineConnectionFactory" factory-method="getInstance"/&gt;
	
	&lt;bean name="setup.rStarterFactory" class="org.rosuda.util.r.impl.RStartContext"&gt;
		&lt;property name="connectionFactory" ref="rConnectionFactory" /&gt;
	&lt;/bean&gt;
	
	&lt;bean name="rStarterFactory" class="org.rosuda.util.r.impl.RStarterFactory"&gt;
		&lt;property name="context" ref="setup.rStarterFactory" /&gt;
	&lt;/bean&gt;
	
	&lt;bean name="rStarterProcess" class="org.rosuda.util.process.ProcessService" factory-bean="rStarterFactory" factory-method="createService"/&gt;
&lt;/beans&gt;
</pre>
Now when you need R running in any java class you can use spring annotation in that class like
<pre>
	@Autowired
	@Qualifier("rStarterProcess")
	private ProcessService<IRConnection> service;
</pre>
This will use the rStarterProcess as defined above. When you need R you use
<pre>
	service.start();
</pre>
and to shut down Rserve 
<pre>
	service.stop();
</pre>
</p>
</body>