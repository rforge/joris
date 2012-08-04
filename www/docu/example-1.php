<<?php

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

<h1>Advanced IRConnection example</h1>
<p>You might want to check out the sources and after compiling use the example subfolder <a href="https://r-forge.r-project.org/scm/viewvc.php/root/examples/?root=joris">rconnection</a></p>
<p>The first and only thing you will do is to set up which libraries you might want to use in a configuration file, like
<pre class="source">
@Configuration
@ImportResource("classpath*:spring/r-service.spring.xml")
public class SpringConfiguration {

    @Bean
    public IRConnection managedIRConnection(IRConnectionConfig configuration, @Qualifier("rStarterProcess") ProcessService rStarterService) {
	final IRConnectionMgrImpl irConnectionMgrImpl = new IRConnectionMgrImpl();
	irConnectionMgrImpl.setService(rStarterService);
	irConnectionMgrImpl.setFactory(new REngineConnectionFactory());
	return irConnectionMgrImpl.getIRConnection(configuration);
    }

    // -- helper
    
    private List<String> requiredInstallLibraries() {
	return Arrays.asList("TIMP");
    }
    
    private List<String> requiredLibraries() {
	return Arrays.asList("TIMP", "MASS");
    }

    @Bean 
    IRConnectionConfig connectionConfiguration() {
	final IRConnectionConfigImpl irConnectionConfigImpl = new IRConnectionConfigImpl();
	final List<IRConnectionConfigStep> configurationSteps = new ArrayList<IRConnectionConfigStep>();
	for (final String libraryName : requiredInstallLibraries()) {
	    final LibraryInstallationStep installLib = new LibraryInstallationStep();
	    installLib.setLibrary(libraryName);
	    configurationSteps.add(installLib);
	}
	for (final String libraryName : requiredLibraries()) {
	    final LoadLibraryStep loadLib = new LoadLibraryStep();
	    loadLib.setLibrary(libraryName);
	    configurationSteps.add(loadLib);
	}
	irConnectionConfigImpl.setSteps(configurationSteps );
	return irConnectionConfigImpl;
    }
}
</pre>
All you need to change is the requiredInstallLibraries() and requiredLibraries(). As the name hints, the first one are checked
for existance and installed if not present. The required libs are loaded whenever you create a new IRConnection.<br/>
To use this in an example you need just two lines of code:
<pre>
	final ApplicationContext springContext = new AnnotationConfigApplicationContext(SpringConfiguration.class);
	final IRConnection connection = springContext.getBean(IRConnection.class);
</pre>
Now connection is your IRConnection, running with loaded TIMP and MASS library and you are ready to do with that whatever you like.
</p>
</body>
</html>