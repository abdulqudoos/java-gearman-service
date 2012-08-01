package org.gearman.impl.reactor;

public interface CompletionHandler<V,A> {
	public void onComplete(V result,A attachment);
	public void onFailure(Throwable exc, A attachment);
}
