package br.com.sysmap.crux.showcase.client;
 
import java.util.Date;

import br.com.sysmap.crux.core.client.controller.Create;
import br.com.sysmap.crux.core.client.datasource.DataSourceAsyncCallbackAdapter;
import br.com.sysmap.crux.core.client.datasource.RemoteBindableEditableStreamingDataSource;
import br.com.sysmap.crux.core.client.datasource.RemoteEditableStreamingDataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSource;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceBinding;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceColumn;
import br.com.sysmap.crux.core.client.datasource.annotation.DataSourceColumns;

import com.google.gwt.user.client.rpc.AsyncCallback;

@DataSource("myLocalPagedDataSource")
//@DataSourceBinding(identifier="valor1")
@DataSourceColumns(
	identifier="valor1",
	columns={
		@DataSourceColumn("col1"),
		@DataSourceColumn("col2"),
		@DataSourceColumn(value="col3",type=Date.class),
})
public class MyLocalPagedDataSource extends RemoteEditableStreamingDataSource
{
	public void fetch(int start, int end)
	{
//		myservice.teste(start, end, new DataSourceAsyncCallbackAdapter<MyDto>(this));
	}
}
