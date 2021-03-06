package lc.api.stargate;

/**
 * Iris state enumeration
 * 
 * @author AfterLifeLochie
 *
 */
public enum IrisState {
	/** No iris is present. */
	None,
	/** Iris is in error state. */
	Error,
	/** Iris is open */
	Open,
	/** Iris is closed */
	Closed,
	/** Iris is opening */
	Opening,
	/** Iris is closing */
	Closing;

	public static IrisState fromOrdinal(int ordinal) {
		return IrisState.values()[ordinal];
	}
}
