package edu.kit.kastel.eclipse.common.view.utilities;

import java.util.ArrayList;
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

public final class JDTUtilities {
	private static final String STUDENT_CODE_PATH_REGEX = "\\/?[^\\/]+\\/assignment\\/.*";

	private JDTUtilities() {
		throw new IllegalStateException("Utility class");
	}

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

	public static List<ICompilationUnit> getAllCompilationUnits(IProject project) throws JavaModelException {
		List<ICompilationUnit> compilationUnits = new ArrayList<>();
		for (var packageFragment : getAllStudentPackages(project)) {
			for (ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
				compilationUnits.add(compilationUnit);
			}
		}

		return compilationUnits;
	}

	private static boolean isStudentPackage(IPackageFragment packageFragment) throws JavaModelException {
		return packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE
				&& packageFragment.getPath().toString().matches(STUDENT_CODE_PATH_REGEX);
	}
}
