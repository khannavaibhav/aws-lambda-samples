package poc.amitk.lambda.sb.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
//@Profile("!local")
public class ResourceServerConfiguration
{

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	 /** Jason Web Keys that our resource server will lookup keys to validate the signature against */
	 @Value("${security.oauth2.resource.jwk.key-set-uri}")
	 private String jwksUrl;

//	private static final String ROOT_PATTERN = "/**";
	 
	 @Bean
	 public JwtDecoder jwtDecoder() {
	     return NimbusJwtDecoder.withJwkSetUri(jwksUrl).build();
	 }
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		logger.info("Setting up API Svc security");
		// Root context for application info
       http.oauth2ResourceServer(resourceServer -> resourceServer.jwt());
       
//       http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
//			response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"Restricted Content\"");
//			response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
//		});
       
        http.authorizeHttpRequests(authz -> authz
        	//.requestMatchers("/**").permitAll()
            .requestMatchers("/pub/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated());
        return http.build();
    }

}
