package indybench;

import com.github.mustachejava.codegen.CodegenObjectHandler;
import com.github.mustachejava.codegen.CodegenReflectionWrapper;
import com.github.mustachejava.indy.IndyObjectHandler;
import com.github.mustachejava.indy.IndyWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class Main {
  
  @Argument(alias = "n", description = "Number of times to run each benchmark")
  private static Integer num = 10;
  
  @Argument(alias = "t", description = "Number of times to run each method call in each benchmark")
  private static Integer times = 100000000;

  @Argument(description = "Enable the reflection object handler benchmark")
  private static Boolean reflectionOH = false;
  
  @Argument(description = "Enable the codegeneration reflection object handler benchmark")
  private static Boolean codegenReflectionOH = false;
  
  @Argument(description = "Enable the invokedynamic benchmark")
  private static Boolean indy = false;

  @Argument(description = "Enable the invokedynamic with no guard benchmark")
  private static Boolean indyNoGaurd = false;

  @Argument(description = "Enable the invokedynamic object handler benchmark")
  private static Boolean indyOH = false;

  @Argument(description = "Enable reflection benchmark")
  private static Boolean reflection = false;

  @Argument(description = "Enable the cached reflection benchmark")
  private static Boolean cachedReflection = false;

  @Argument(description = "Enable unreflected method handle with lookup benchmark")
  private static Boolean unreflection = false;

  @Argument(description = "Enable the cached unreflected method handle benchmark")
  private static Boolean cachedUnreflection = false;

  @Argument(description = "Enable the method handle benchmark")
  private static Boolean mh = false;

  @Argument(description = "Enable the cached method handle benchmark")
  private static Boolean cachedMH = false;

  @Argument(description = "Enable the direct method call benchmark")
  private static Boolean direct = false;

  @Argument(description = "Enable all benchmarks")
  private static Boolean all = false;
  
  public static void main(String[] args) throws Throwable {
    Args.parseOrExit(Main.class, args);
    if (!all && !reflectionOH && !codegenReflectionOH && !indy && !indyNoGaurd && !reflection &&
            !cachedReflection && !unreflection && !cachedUnreflection && !mh && !cachedMH && !direct) {
      System.err.println("You must select at least 1 benchmark");
      Args.usage(Main.class);
      System.exit(1);
    }
    Main indyDemo = new Main();
    for (int i = 0; i < num; i++) {
      if (all || reflectionOH) timeReflectionOH(indyDemo);
      if (all || codegenReflectionOH) timeCodegenReflectionOH(indyDemo);
      if (all || indy) timeIndy(indyDemo);
      if (all || indyNoGaurd) timeIndyNoGuard(indyDemo);
      if (all || indyOH) timeIndyOH(indyDemo);
      if (all || reflection) timeReflection(indyDemo);
      if (all || cachedReflection) timeReflectionCached(indyDemo);
      if (all || unreflection) timeUnReflection(indyDemo);
      if (all || cachedUnreflection) timeUnReflectionCached(indyDemo);
      if (all || mh) timeMH(indyDemo);
      if (all || cachedMH) timeMHCached(indyDemo);
      if (all || direct) timeDirect(indyDemo);
      System.out.println("-----------------");
    }
  }

  public static void timeReflectionOH(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      REFLECTED.call(scopes);
    }
    System.out.println("reflection OH: " + (System.currentTimeMillis() - start));
  }

  public static void timeCodegenReflectionOH(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      CODEGEN_REFLECTED.call(scopes);
    }
    System.out.println("codegen reflection OH: " + (System.currentTimeMillis() - start));
  }

  public static void timeIndy(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      INDY.call(scopes);
    }
    System.out.println("indy wrapper: " + (System.currentTimeMillis() - start));
  }

  public static void timeIndyNoGuard(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      INDY_NOGUARD.call(scopes);
    }
    System.out.println("indy wrapper no guard: " + (System.currentTimeMillis() - start));
  }

  public static void timeIndyOH(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      INDY_OH.call(scopes);
    }
    System.out.println("indy OH: " + (System.currentTimeMillis() - start));
  }

  public static void timeReflection(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    int REFLECTION_TIMES = 10000000;
    for (int i = 0; i < REFLECTION_TIMES; i++) {
      Main.class.getDeclaredMethod("someMethod").invoke(indyDemo);
    }
    System.out.println("reflection: " + (times / REFLECTION_TIMES)*(System.currentTimeMillis() - start));
  }

  public static void timeReflectionCached(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    Method someMethod = Main.class.getDeclaredMethod("someMethod");
    for (int i = 0; i < times; i++) {
      someMethod.invoke(indyDemo);
    }
    System.out.println("reflection cached: " + (System.currentTimeMillis() - start));
  }

  public static void timeUnReflection(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    int REFLECTION_TIMES = 10000;
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    for (int i = 0; i < REFLECTION_TIMES; i++) {
      int result = (int) lookup.unreflect(Main.class.getDeclaredMethod("someMethod")).invokeExact(indyDemo);
    }
    System.out.println("unreflection: " + (times / REFLECTION_TIMES)*(System.currentTimeMillis() - start));
  }

  public static void timeUnReflectionCached(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodHandle someMethod = lookup.unreflect(Main.class.getDeclaredMethod("someMethod"));
    for (int i = 0; i < times; i++) {
      int result = (int) someMethod.invokeExact(indyDemo);
    }
    System.out.println("unreflection cached: " + (System.currentTimeMillis() - start));
  }

  public static void timeMH(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    int REFLECTION_TIMES = 10000;
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType type = MethodType.methodType(Integer.TYPE);
    for (int i = 0; i < REFLECTION_TIMES; i++) {
      MethodHandle someMethod = lookup.findVirtual(Main.class, "someMethod", type);
      int result = (int) someMethod.invokeExact(indyDemo);
    }
    System.out.println("methodhandle: " + (times / REFLECTION_TIMES)*(System.currentTimeMillis() - start));
  }

  public static void timeMHCached(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType type = MethodType.methodType(Integer.TYPE);
    MethodHandle someMethod = lookup.findVirtual(Main.class, "someMethod", type);
    for (int i = 0; i < times; i++) {
      int result = (int) someMethod.invokeExact(indyDemo);
    }
    System.out.println("methodhandle cached: " + (System.currentTimeMillis() - start));
  }

  public static void timeDirect(Main indyDemo) throws Throwable {
    long start = System.currentTimeMillis();
    for (int i = 0; i < times; i++) {
      indyDemo.someMethod();
    }
    System.out.println("direct: " + (System.currentTimeMillis() - start));
  }

  private static Wrapper REFLECTED;
  private static Wrapper INDY;
  private static IndyWrapper INDY_NOGUARD;

  private static Wrapper CODEGEN_REFLECTED;

  private static final Wrapper INDY_OH;

  static {
    Main indyDemo = new Main();
    REFLECTED = new ReflectionObjectHandler().find("someMethod", new Object[] { indyDemo });
    CODEGEN_REFLECTED = new CodegenObjectHandler().find("someMethod", new Object[] { indyDemo });
    INDY = IndyWrapper.create((CodegenReflectionWrapper) CODEGEN_REFLECTED);
    INDY_NOGUARD = IndyWrapper.create((CodegenReflectionWrapper) CODEGEN_REFLECTED, false);
    INDY_OH = new IndyObjectHandler().find("someMethod", new Object[] { indyDemo });
  }

  private int length = 0;

  public int someMethod() {
    return length++;
  }
}