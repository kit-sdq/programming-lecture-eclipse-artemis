/* Licensed under EPL-2.0 2022-2023. */
package edu.kit.kastel.eclipse.common.view.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Contains utility methods for working with the JDT.
 */
public final class JDTUtilities {
	private static final String STUDENT_CODE_PATH_REGEX = "\\/?[^\\/]+\\/assignment\\/.*";

	private JDTUtilities() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Finds all packages that are part of the student's solution.
	 *
	 * @param project the project to search in
	 * @return all package that the student created, including the default package
	 *         if there are classes in it
	 * @throws JavaModelException if the JDT fails
	 */
	public static List<IPackageFragment> getAllStudentPackages(IProject project) throws JavaModelException {
		var javaProject = JavaCore.create(project);

		List<IPackageFragment> packages = new ArrayList<>();
		for (IPackageFragment packageFragment : javaProject.getPackageFragments()) {
			if (isStudentPackage(packageFragment)) {
				packages.add(packageFragment);
			}
		}
		return packages;
	}

	/**
	 * Searches for the main class (i.e. the class containing the main method) in
	 * the student's code.
	 *
	 * @param project the project to search in
	 * @return a main class if one could be found
	 * @throws JavaModelException if the JDT fails
	 */
	public static Optional<IType> findMainClass(IProject project) throws JavaModelException {
		for (var packageFragment : getAllStudentPackages(project)) {
			for (ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
				for (IType type : compilationUnit.getAllTypes()) {
					for (IMethod method : type.getMethods()) {
						if (method.isMainMethod()) {
							return Optional.of(type);
						}
					}
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Finds all compilation units (i.e. Java files) created by the student.
	 *
	 * @param project the project to search in
	 * @return the student's compilation units
	 * @throws JavaModelException if the JDT fails
	 */
	public static List<ICompilationUnit> getAllCompilationUnits(IProject project) throws JavaModelException {
		List<ICompilationUnit> compilationUnits = new ArrayList<>();
		for (IPackageFragment packageFragment : getAllStudentPackages(project)) {
			Collections.addAll(compilationUnits, packageFragment.getCompilationUnits());
		}

		return compilationUnits;
	}

	private static boolean isStudentPackage(IPackageFragment packageFragment) throws JavaModelException {
		return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE && packageFragment.getPath().toString().matches(STUDENT_CODE_PATH_REGEX);
	}
}
