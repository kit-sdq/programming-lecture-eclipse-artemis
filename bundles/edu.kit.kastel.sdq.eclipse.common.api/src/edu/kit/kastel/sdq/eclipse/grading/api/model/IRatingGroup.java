/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.grading.api.model;

import java.util.List;

/**
 * {@link IMistakeType}s belong to a {@link IRatingGroup}. Rating Groups may
 * introduce penalty limits for calculation, capping the maximum penalty that
 * all {@link IMistakeType}s belonging to one {@link IRatingGroup} can reach in
 * sum.
 *
 */
public interface IRatingGroup {
	// TODO hier getMistakeTypes ! transient oder sowas (?), nicht serialisiert!

	/**
	 *
	 * @return A more elaborate name.
	 */
	String getDisplayName();

	/**
	 *
	 * @return the MistakeTypes that define this RatingGroup as its rating group.
	 */
	List<IMistakeType> getMistakeTypes();

	/**
	 *
	 * @return the maximum penalty that all {@link IMistakeType}s belonging to one
	 *         {@link IRatingGroup} can reach in sum
	 */
	double getPenaltyLimit();

	/**
	 *
	 * @return the <i>unique</> shortName of this ratingGroup
	 */
	String getShortName();

	/**
	 * The rating group might have a penalty limit or not. See
	 * {@link #getPenaltyLimit()}
	 */
	boolean hasPenaltyLimit();
}
