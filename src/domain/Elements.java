package domain;

public enum Elements {

	TIERS("tier_name"),
	NODES("node_name"),
	EXTERNAL_CALLS("external_call_name"),
	BUSINESS_TRANSACTIONS("business_transaction_name");
	
	Elements(String descripcion){
		this.descripcion = descripcion;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	private String descripcion;
	
}
