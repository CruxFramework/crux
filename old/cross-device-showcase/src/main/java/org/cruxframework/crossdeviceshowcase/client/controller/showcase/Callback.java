package org.cruxframework.crossdeviceshowcase.client.controller.showcase;

import org.cruxframework.crux.core.client.rpc.AsyncCallbackAdapter;
import org.cruxframework.crux.widgets.client.dialog.ProgressBox;

/**
 * A simple callback adapter which shows a progress message.
 * @param <T>
 * @author Gesse Dafe
 */
public abstract class Callback<T> extends AsyncCallbackAdapter<T>
{
	private ProgressBox progress;

	public Callback()
	{
		this.progress = ProgressBox.show("Processando...");
	}
	
	@Override
	public void onComplete(T result)
	{
		progress.hide();
		applyResult(result);	
	}

	@Override
	public void onError(Throwable e)
	{
		progress.hide();
		super.onError(e);
	}
	
	public abstract void applyResult(T result);
}
