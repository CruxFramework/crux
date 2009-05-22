package br.com.sysmap.crux.core.client;

import com.google.gwt.benchmarks.client.Benchmark;

public class JSEngineBenchmarkTest extends Benchmark{

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {
		return "br.com.sysmap.crux.core.JSEngine";
	}

	  private static final int numAllocs = 1000;

	  /**
	   * Allocates java.lang.Object in a for loop 1,000 times.
	   *
	   * The current version of the compiler lifts the declaration of obj outside
	   * of this loop and also does constant folding of numAllocs.
	   * Also, this loop allocs the GWT JS mirror for java.lang.Object
	   * NOT an empty JS object, for example.
	   *
	   */
	  public void testJavaObjectAlloc() 
	  {
	    for ( int i = 0; i < numAllocs; ++i ) 
	    {
	    	@SuppressWarnings("unused")
	    	Object obj = new Object();
	    }
	  }

	  /**
	   * Compares GWT mirror allocations of java.lang.Object to an empty JS object.
	   */
	  public native void testJsniObjectAlloc1() /*-{
	    for (var i = 0; i < @br.com.sysmap.crux.core.client.JSEngineBenchmarkTest::numAllocs; ++i ) {
	      var obj = {}; // An empty JS object alloc
	    }
	  }-*/;

	  /**
	   * Like version 1, but also folds the constant being used in the iteration.
	   */
	  public native void testJsniObjectAlloc2() /*-{
	    for (var i = 0; i < 1000; ++i ) {
	      var obj = {}; // An empty JS object alloc
	    }
	  }-*/;

	  /**
	   * Like version 2, but hoists the variable declaration from the loop.
	   */
	  public native void testJsniObjectAlloc3() /*-{
	    var obj;
	    for (var i = 0; i < 1000; ++i ) {
	      obj = {}; // An empty JS object alloc
	    }
	  }-*/;

	  /**
	   * Like version 3, but counts down (and in a slightly different range).
	   */
	  public native void testJsniObjectAlloc4() /*-{
	    var obj;
	    for (var i = 1000; i > 0; --i ) {
	      obj = {}; // An empty JS object alloc
	    }
	  }-*/;

}
