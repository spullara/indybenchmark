package indybench;

import com.github.mustachejava.codegen.CodegenObjectHandler;
import com.github.mustachejava.codegen.CodegenReflectionWrapper;
import com.github.mustachejava.indy.IndyObjectHandler;
import com.github.mustachejava.indy.IndyWrapper;
import com.github.mustachejava.reflect.ReflectionObjectHandler;
import com.github.mustachejava.util.Wrapper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

@State(Scope.Benchmark)
public class Main {
  
  private static final int times = 1000000;

  private static final Main indyDemo = new Main();

  @Benchmark
  public void timeReflectionOH() throws Throwable {
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      REFLECTED.call(scopes);
    }
  }

  @Benchmark
  public void timeCodegenReflectionOH() throws Throwable {
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      CODEGEN_REFLECTED.call(scopes);
    }
  }

  @Benchmark
  public void timeIndy() throws Throwable {
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      INDY.call(scopes);
    }
  }

  @Benchmark
  public void timeIndyNoGuard() throws Throwable {
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      INDY_NOGUARD.call(scopes);
    }
  }

  @Benchmark
  public void timeIndyOH() throws Throwable {
    Object[] scopes = {indyDemo};
    for (int i = 0; i < times; i++) {
      INDY_OH.call(scopes);
    }
  }

  @Benchmark
  public void timeReflection() throws Throwable {
    for (int i = 0; i < times; i++) {
      Main.class.getDeclaredMethod("someMethod").invoke(indyDemo);
    }
  }

  @Benchmark
  public void timeReflectionCached() throws Throwable {
    Method someMethod = Main.class.getDeclaredMethod("someMethod");
    for (int i = 0; i < times; i++) {
      someMethod.invoke(indyDemo);
    }
  }

  @Benchmark
  public void timeUnReflection() throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    for (int i = 0; i < times; i++) {
      int result = (int) lookup.unreflect(Main.class.getDeclaredMethod("someMethod")).invokeExact(indyDemo);
    }
  }

  @Benchmark
  public void timeUnReflectionCached() throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodHandle someMethod = lookup.unreflect(Main.class.getDeclaredMethod("someMethod"));
    for (int i = 0; i < times; i++) {
      int result = (int) someMethod.invokeExact(indyDemo);
    }
  }

  @Benchmark
  public void timeMH() throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType type = MethodType.methodType(Integer.TYPE);
    for (int i = 0; i < times; i++) {
      MethodHandle someMethod = lookup.findVirtual(Main.class, "someMethod", type);
      int result = (int) someMethod.invokeExact(indyDemo);
    }
  }

  @Benchmark
  public void timeMHCached() throws Throwable {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType type = MethodType.methodType(Integer.TYPE);
    MethodHandle someMethod = lookup.findVirtual(Main.class, "someMethod", type);
    for (int i = 0; i < times; i++) {
      int result = (int) someMethod.invokeExact(indyDemo);
    }
  }

  private final static MethodHandle someMethod;
  static {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    MethodType type = MethodType.methodType(Integer.TYPE);
    try {
      someMethod = lookup.findVirtual(Main.class, "someMethod", type);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Benchmark
  public void timeMHConstant() throws Throwable {
    for (int i = 0; i < times; i++) {
      int result = (int) someMethod.invokeExact(indyDemo);
    }
  }

  @Benchmark
  public void timeDirect() throws Throwable {
    for (int i = 0; i < times; i++) {
      indyDemo.someMethod();
    }
  }

  private static Wrapper REFLECTED;
  private static Wrapper INDY;
  private static IndyWrapper INDY_NOGUARD;

  private static Wrapper CODEGEN_REFLECTED;

  private static final Wrapper INDY_OH;

  static {
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