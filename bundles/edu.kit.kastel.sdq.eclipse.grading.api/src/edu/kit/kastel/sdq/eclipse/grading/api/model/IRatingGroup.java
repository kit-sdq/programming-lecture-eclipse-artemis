package edu.kit.kastel.sdq.eclipse.grading.api.model;

/**
 * {@link IMistakeType}s belong to a {@link IRatingGroup}. Rating Groups may introduce penalty limits for calculation, capping the maximum penalty that
 * all {@link IMistakeType}s belonging to one {@link IRatingGroup} can reach in sum.
 *
 */
public interface IRatingGroup {

	/**
	 *
	 * @return A more elaborate name.
	 */
	String getDisplayName();

	/**
	 *
	 * @return the maximum penalty that all {@link IMistakeType}s belonging to one {@link IRatingGroup} can reach in sum
	 */
	Double getPenaltyLimit();

	/**
	 *
	 * @return the <i>unique</> shortName of this ratingGroup
	 */
	String getShortName();

	/**
	 * The rating group might have a penalty limit or not. See {@link #getPenaltyLimit()}
	 */
	boolean hasPenaltyLimit();
}
