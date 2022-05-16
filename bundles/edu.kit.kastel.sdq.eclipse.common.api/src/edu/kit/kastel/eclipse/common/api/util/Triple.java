/* Licensed under EPL-2.0 2022. */
package edu.kit.kastel.eclipse.common.api.util;

public record Triple<A, B, C> (A first, B second, C third) {

	private static final Triple<?, ?, ?> EMPTY = createEmptyTriple();

	private static Triple<?, ?, ?> createEmptyTriple() {
		return new Triple<>(null, null, null);
	}

	@SuppressWarnings("unchecked")
	public static final <A, B, C> Triple<A, B, C> empty() {
		return (Triple<A, B, C>) EMPTY;
	}

	public boolean isEmpty() {
		return this.first == null && this.second == null && this.third == null;
	}
}
