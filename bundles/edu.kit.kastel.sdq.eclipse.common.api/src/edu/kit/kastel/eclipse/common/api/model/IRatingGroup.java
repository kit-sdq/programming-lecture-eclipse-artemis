/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.model;

import java.util.List;

import edu.kit.kastel.eclipse.common.api.util.Pair;

/**
 * {@link IMistakeType}s belong to a {@link IRatingGroup}. Rating Groups may
 * introduce penalty limits for calculation, capping the maximum penalty that
 * all {@link IMistakeType}s belonging to one {@link IRatingGroup} can reach in
 * sum.
 *
 */
public interface IRatingGroup {
	String getDisplayName();

	/**
	 * Not suitable for official annotations
	 * 
	 * @default is the official German name
	 * @since 2.7
	 * @return A more elaborate name in the respective language
	 */
	String getLanguageSensitiveDisplayName(String language);

	/**
	 *
	 * @return the MistakeTypes that define this RatingGroup as its rating group.
	 */
	List<IMistakeType> getMistakeTypes();

	/**
	 *
	 * @return the minimum or maximum penalty that all {@link IMistakeType}
	 *         belonging to one {@link IRatingGroup} can reach in sum
	 */
	double setToRange(double points);

	/**
	 * @return [negative_limit, positive_limit]
	 */
	Pair<Double, Double> getRange();

	/**
	 *
	 * @return the <i>unique</> shortName of this ratingGroup
	 */
	String getShortName();

}
