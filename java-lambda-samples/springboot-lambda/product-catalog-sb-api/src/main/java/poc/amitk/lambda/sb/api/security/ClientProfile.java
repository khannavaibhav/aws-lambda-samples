package poc.amitk.lambda.sb.api.security;

public class ClientProfile implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
  
    private String scopes;
    private String clientId;
  
	public String getScopes() {
		return scopes;
	}
	public void setScopes(String scopes) {
		this.scopes = scopes;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
