# SpringBoot-HTTPS-Using SSL Certificate
How to implement Spring Boot with HTTPS using SSL Certificate 
Step-1 : Create JKS File with the help of keytool command
		-Go to the bin folder of Jdk
		-Open Command prompt
		-Use Following Command to generate jks file with Different ssl properties.
		>keytool -genkey -alias selfsigned_ssl -keyalg RSA -keysize 2048 -validity 365 -keypass secretpass -storepass secretpass -keystore E://spring-ssl.jks
		
		Let’s understand above command :

		-genkey => is the keytool command to generate the certificate
		-alias selfsigned_ssl => indicates the alias of the certificate, which is used by SSL/TLS layer
		-keyalg RSA -keysize 2048 -validity 365 => are self descriptive parameters indicating the crypto algorithm, keysize and 
		 certificate validity in days.
		-keypass changeit -storepass changeit => are the passwords of our truststore and keystore
		-keystore E://spring-ssl.jks => is the actual keystore where the certificate and public/private key will be stored. 
		 Here we are using JKS fromat – Java Key Store, there are other formats as well for keystore.

Step-2 : Copy the generated file from E:// location and paste in src/main/resources folder	i.e classpath
Step-3 : Add following properties in application.properties file.
		server.port=8443
		server.ssl.key-alias=selfsigned_ssl
		server.ssl.key-password=secretpass
		server.ssl.key-store=classpath:spring-ssl.jks
		server.ssl.key-store-provider=SUN
		server.ssl.key-store-type=JKS
		
Step-4 : Create Java configuration class for redirect HTTP to HTTPS Request configuration.
		
		This is an optional step in case you want to redirect your HTTP traffic to HTTPS, so that the full site becomes secured.
		To do that in spring boot, we need to add HTTP connector at 8080 port and then we need to set redirect port 8443. So that
		any request in 8080 through http, it would be automatically redirected to 8443 and https.

		To do that you just need to add below configuration, but this work with Spring boot 1.5 or 1.7.
		
		import org.apache.catalina.Context;
		import org.apache.catalina.connector.Connector;
		import org.apache.tomcat.util.descriptor.web.SecurityCollection;
		import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
		import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
		import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
		import org.springframework.context.annotation.Bean;
				
		
		@Configuration
		public class ServletContainerConfiguration {

		   @Bean
			public EmbeddedServletContainerFactory servletContainer() {
			  TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
			      @Override
			      protected void postProcessContext(Context context) {
			        SecurityConstraint securityConstraint = new SecurityConstraint();
			        securityConstraint.setUserConstraint("CONFIDENTIAL");
			        SecurityCollection collection = new SecurityCollection();
			        collection.addPattern("/*");
			        securityConstraint.addCollection(collection);
			        context.addConstraint(securityConstraint);
			      }
			    };
			   
			  tomcat.addAdditionalTomcatConnectors(redirectConnector());
			  return tomcat;
			}
			 
			private Connector redirectConnector() {
			  Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
			  connector.setScheme("http");
			  connector.setPort(8080);
			  connector.setSecure(false);
			  connector.setRedirectPort(8443);
			   
			  return connector;
			}

		}
		
Step-5 : Hit Following URL in Browser :https://localhost:8443/swagger-ui.html#/

		Proceed by accepting selfsigned certificate.
		
		Note : To Use existing CA Verified Cerificate use the same steps by installing it.
//************************************************************************************************************************************