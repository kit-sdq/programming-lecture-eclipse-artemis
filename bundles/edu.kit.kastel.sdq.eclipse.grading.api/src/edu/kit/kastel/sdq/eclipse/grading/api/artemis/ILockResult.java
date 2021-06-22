package edu.kit.kastel.sdq.eclipse.grading.api.artemis;

import java.util.Collection;

public interface ILockResult {

	int getId();

	Collection<IFeedback> getPreexistentFeedbacks();
}
