/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.sdq.eclipse.common.core.config;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.util.StdConverter;

import edu.kit.kastel.sdq.eclipse.common.core.model.MistakeType;
import edu.kit.kastel.sdq.eclipse.common.core.model.RatingGroup;

/**
 * Used by {@link JsonFileConfigDao} to add rating Group - mistake type
 * associations.
 */
public final class ExerciseConfigConverter extends StdConverter<ExerciseConfig, ExerciseConfig> {

	@Override
	public ExerciseConfig convert(final ExerciseConfig exerciseConfig) {
		List<RatingGroup> ratingGroups = exerciseConfig.getRatingGroups();
		for (MistakeType mistakeType : exerciseConfig.getMistakeTypes()) {
			// find rating group
			Optional<RatingGroup> ratingGroupOptional = ratingGroups.stream()
					.filter(ratingGroup -> ratingGroup.getShortName().equals(mistakeType.getAppliesTo())).findFirst();
			if (ratingGroupOptional.isEmpty()) {
				// Since Checked exceptions can't be thrown, here...
				throw new RuntimeException("no rating group could be associated with mistake Type" + mistakeType.getShortName() + " with appliesTo="
						+ mistakeType.getAppliesTo() + " and available ratingGroups=" + ratingGroups);
			}
			final RatingGroup ratingGroup = ratingGroupOptional.get();
			// set both associations
			mistakeType.setRatingGroup(ratingGroup);
			ratingGroup.addMistakeType(mistakeType);
		}
		return exerciseConfig;
	}

}
