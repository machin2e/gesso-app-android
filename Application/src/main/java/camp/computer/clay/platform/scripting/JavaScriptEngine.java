package camp.computer.clay.platform.scripting;

import android.util.Log;

import org.liquidplayer.webkit.javascriptcore.JSContext;
import org.liquidplayer.webkit.javascriptcore.JSValue;

import java.text.DecimalFormat;

public class JavaScriptEngine {

    // Reference: https://github.com/ericwlange/AndroidJSCore
    JSContext context;

    public JavaScriptEngine() {

        this.context = new JSContext();

        // Test 1
        context.property("a", 5);
        JSValue aValue = context.property("a");
        double a = aValue.toNumber();
        DecimalFormat df = new DecimalFormat(".#");
        Log.v("AndroidJSCore", (df.format(a))); // 5.0

        // Test 2
        context.evaluateScript("a = 10");
        JSValue newAValue = context.property("a");
        Log.v("AndroidJSCore", df.format(newAValue.toNumber())); // 10.0
        String script =
                "function factorial(x) { var f = 1; for(; x > 1; x--) f *= x; return f; }\n" +
                        "var fact_a = factorial(a);\n";
        context.evaluateScript(script);
        JSValue fact_a = context.property("fact_a");
        Log.v("AndroidJSCore", df.format(fact_a.toNumber())); // 3628800.0
    }
}
