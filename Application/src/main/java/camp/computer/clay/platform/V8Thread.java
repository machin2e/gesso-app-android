package camp.computer.clay.platform;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class V8Thread extends Thread
{
    String source = "print('hello');";

    private WebView webView;

    private double pi;
    private int i, j;

    public V8Thread(int i, int j)
    {
        pi = 0.0;
        this.i = i;
        this.j = j;

        source = "function test(){ V8Thread.setResult('blah'); }";

        webView = new WebView(Application.getView().getContext());
    }

    @SuppressWarnings("unused")
    public void setResult(String in)
    {
        Log.d("Pi",in);
    }

    public double getResult()
    {
        return pi;
    }

    @Override
    public void run()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "V8Thread");
        webView.loadData(source, "text/html", "utf-8");
        //webView.loadUrl("javascript:Androidpicalc("+i+","+j+")");
        webView.loadUrl("javascript:test()");
        Log.d("V8Thread","Here");
        webView.loadUrl("javascript:print('hello');");
        webView.loadUrl("javascript:android.onData(functionThatReturnsSomething)");
    }

    @JavascriptInterface
    public void onData(String value) {
        //.. do something with the data
        Log.v("V8", "JS Data: " + value);
    }
}
