package testplugin_activateByShortcut.git;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.jgit.transport.URIish;

public class EgitGitHandler extends AbstractGitHandler {

	private static final String remoteName = "origin";
	
	public EgitGitHandler(String repoURL) {
		super(repoURL);
	}

	@Override
	public void cloneRepo(final File destination, final String branch) {
		// TODO this currently clones all branches. Sparse checkout might be better?!
		try {
			CloneOperation cloneOperation = new CloneOperation(getURIish(), true, null, destination, branch, remoteName, 0);
			cloneOperation.run(null);
		} catch (Exception e) {
			//TODO rethrow
			System.out.println("[EgitGitHandler - Erreur] Clone failed with exception [" + e.getClass() + "]:"+ e.getMessage());
			return;
		}
		System.out.println("[EgitGitHandler] Clone ran through! ");
	}

	@Override
	public void checkout(String branch, boolean create) {
		// TODO Auto-generated method stub

	}
	
	private URIish getURIish() throws URISyntaxException {
		return new URIish(getRepoURL());
	}

}
