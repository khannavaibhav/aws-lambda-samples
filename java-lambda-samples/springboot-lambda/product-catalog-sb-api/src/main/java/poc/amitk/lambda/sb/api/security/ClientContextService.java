package poc.amitk.lambda.sb.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;


@Service
public class ClientContextService {
    private static Logger logger = LoggerFactory.getLogger(ClientContextService.class);

	@Value("${spring.config.myID}")
    String myId;

    public static ClientProfile getClientProfile() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication) {
			logger.warn("Authentication is null");
			return null;
		}
		var profile = new ClientProfile();
		
		Jwt principal = (Jwt) authentication.getPrincipal();
		if (null != principal) {
			System.out.println("User has authorities: " + principal.getClaimAsString("sub"));
			var clientId = principal.getClaimAsString("sub");
			var scope = principal.getClaimAsString("scope");
			profile.setClientId(clientId);
			profile.setScopes(scope);
		}

		return profile;
	}

    public static boolean hasScope(String scope) {
		var profile = getClientProfile();
		if(profile.getScopes()!=null && profile.getScopes().contains(scope)) return true;
		return false;
	}

}
