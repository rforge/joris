
<!-- This is the project specific website template -->
<!-- It can be changed as liked or replaced by other content -->

<?php

$domain=ereg_replace('[^\.]*\.(.*)$','\1',$_SERVER['HTTP_HOST']);
$group_name=ereg_replace('([^\.]*)\..*$','\1',$_SERVER['HTTP_HOST']);
$themeroot='http://r-forge.r-project.org/themes/rforge/';

echo '<?xml version="1.0" encoding="UTF-8"?>';
?>
<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en   ">

  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title><?php echo $group_name; ?></title>
	<link href="<?php echo $themeroot; ?>styles/estilo1.css" rel="stylesheet" type="text/css" />
  </head>

<body>

<!-- R-Forge Logo -->
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr><td>
<a href="http://r-forge.r-project.org/"><img src="http://<?php echo $themeroot; ?>/images/logo.png" border="0" alt="R-Forge Logo" /> </a> </td> </tr>
</table>


<!-- get project title  -->
<!-- own website starts here, the following may be changed as you like -->

<h1>Welcome to JORIS</h1>
<h3>The Java ordinary R infrastructure support project</h3>
<p>
The goal for this project is to supply a set of useful java code that facilitates using R in combination with java.
jr-connect is one maven module to connect to R either via JRI or Rserve.
some tools from the project MORET are migrated bit by bit to provide services for multi model management.
There is lots of code for mapping objects from an arbitraty type into a storable and searchable tree (graph) structure.
the last step will be to create ui widgets for desktop applications as a reference how to use this packages.
</p>
<p>
About the project structure. The project build system is maven (2 or newer). In this early stage the full build process might hang up on some machines
like a windows machine blocking in a non forking process. If the build seems to stop break and retry.</br>
<h6>folders</h6>
<ul>
    <li>root ... contains the maven and java sources</li>
    <li>maven-artifacts ... contains some jar files that were not found on public repositories yet. Please install manually using mvn install:install-file
    refer to the manual if necessary <a href="http://maven.apache.org/plugins/maven-install-plugin/usage.html">http://maven.apache.org/plugins/maven-install-plugin/usage.html</a> or drop me a few lines</li>
</ul>
</p>

<!-- end of project description -->

<p>
	<h3>Getting started.</h3> 
	You need to install R and a connection to R first in order to utilize
	<a href="docu/irconnection.php">IRConnection</a>.
</p>
<p>
	<h3>Example</h3>
	After finishing an R and Rserve installation you might want to use some fancy R libraries, too within your project.
	Using JORIS this is one file configuration and you're up to do what you like in your application.
	<a href="docu/example-1.php">Example create IRConnection with loaded R-libraries</a>.
</p>
<p> The <strong>project summary page</strong> you can find <a href="http://<?php echo $domain; ?>/projects/<?php echo $group_name; ?>/"><strong>here</strong></a>. </p>

</body>
</html>
