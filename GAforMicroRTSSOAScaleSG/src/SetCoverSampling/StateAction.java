package SetCoverSampling;

public class StateAction {
	
	public StateAction()
	{
		this.state=state;
		this.action=action;
	}
	
	public StateAction(String state, String action)
	{
		this.state=state;
		this.action=action;
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

	private String state;
	private String action;
	


}
