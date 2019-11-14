package SetCoverSampling;

public class StateAction {
	
	public StateAction()
	{
		this.state=state;
		this.action=action;
		this.nameState=nameState;
	}
	
	public StateAction(String state, String action, String nameState)
	{
		this.state=state;
		this.action=action;
		this.nameState=nameState;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * @return the nameState
	 */
	public String getNameState() {
		return nameState;
	}

	/**
	 * @param nameState the nameState to set
	 */
	public void setNameState(String nameState) {
		this.nameState = nameState;
	}

	private String state;
	private String action;
	private String nameState;

	


}
