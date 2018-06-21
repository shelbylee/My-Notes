- [1. Creating and Destroying Objects](#1-creating-and-destroying-objects)
  * [Item 1: Consider static factory methods instead of constructors](#item-1-consider-static-factory-methods-instead-of-constructors)
  * [Item 2: Consider a builder when faced with many constructor parameters](#item-2-consider-a-builder-when-faced-with-many-constructor-parameters)
  * [Item 3: Enforce the singleton property with a private constructor or an enum type](#item-3--enforce-the-singleton-property-with-a-private-constructor-or-an-enum-type)
  * [Item 4: Enforce noninstantiability with a private constructor](#item-4-enforce-noninstantiability-with-a-private-constructor)
  * [Item 5: Prefer dependency injection to hardwiring resources](#item-5-prefer-dependency-injection-to-hardwiring-resources)
  * [Item 6: Avoid creating unnecessary objects](#item-6-avoid-creating-unnecessary-objects)
  * [Item 7: Eliminate obsolete object references](#item-7-eliminate-obsolete-object-references)
  * [Item 8: Avoid finalizers and cleaners](#item-8-avoid-finalizers-and-cleaners)
  * [Item 9: Prefer try-with-resources to try-finally](#item-9-prefer-try-with-resources-to-try-finally)

## 1. Creating and Destroying Objects

### Item 1: Consider static factory methods instead of constructors

使用静态工厂方法有很多优势
1. 有名字，更清晰
2. 不用每次调用时都创建新对象，提升性能
3. 可以返回原返回类型的任意子类型，更灵活

比如在 Guava 的 collect 包中就大量使用静态工厂方法代替构造器来创建对象。
```java
  /**
   * Creates a new, empty {@code ArrayListMultimap} with the default initial capacities.
   *
   * <p>This method will soon be deprecated in favor of {@code
   * MultimapBuilder.hashKeys().arrayListValues().build()}.
   */
  public static <K, V> ArrayListMultimap<K, V> create() {
    return new ArrayListMultimap<>();
  }
```
### Item 2: Consider a builder when faced with many constructor parameters

lombok 已经给我们提供了用 builder 创建对象的便捷方式。

### Item 3: Enforce the singleton property with a private constructor or an enum type

作者称 "a single-element enum type is often the best way to implement a singleton"，虽然有些不习惯，但还是应转向用枚举来实现单例模式，因为它更 concise，并且提供序列化机制，绝对防止多次实例化。

EnumSingleton.java
```java
public enum EnumSingleton {

     INSTANCE;

     int value;

     public int getValue() {
        return value;
     }

     public void setValue(int value) {
        this.value = value;
     }

}
```
SingletonDemo.java
```java
public class SingletonDemo {
    
    public static void main(String[] args) {
        EnumSingleton singleton = EnumSingleton.INSTANCE;
        System.out.println(singleton.getValue());
        singleton.setValue(111);
        System.out.println(singleton.getValue());
    }
}
```

### Item 4: Enforce noninstantiability with a private constructor
这个很常见，用私有构造器来防止实例化

### Item 5: Prefer dependency injection to hardwiring resources
这是新增的一个 Item，用依赖注入来代替 hardwiring 资源，依赖注入就是在创建时将所依赖的资源注入，依赖注入提高来灵活性和可测试性。

比如文中举的例子
```java
// Dependency injection provides flexibility and testability
public class SpellChecker {
    private final Lexicon dictionary;
    
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }
    
    public boolean isValid(String word) {...}
    public List<String> suggestions(String typo) {...}
}
```
将 dictionary 注入到依赖于它的 SpellChecker 中（在创建 SpellChecker 时）

最好使用依赖注入框架 Dagger or Guice or Spring。

总之，不要用单例或者静态工具类来实现一个依赖于一个或多个底层资源的类（这些资源的行为会影响到这个类），并且也不要让这些类直接创建这些资源。应该采用依赖注入的方式，将这些资源或者是创建这些资源的工厂传递给构造器（或者静态工厂、builder）。

### Item 6: Avoid creating unnecessary objects
创建过多对象是会降低性能的，因此要避免创建不必要的对象，作者给出了几条避免创建不必要对象的策略：
1. 通过重用不可变对象从而避免创建不必要的对象。

e.g. "str" 本身就是一个 String 对象，不需要再 new
```java
String s = new String("str"); // DON'T DO THIS!
```
String 是不可变类，对于不可变类，如果有提供静态工厂方法，那么就不要用构造器来创建。e.g. 使用 Boolean.valueOf(String) 而不是 Boolean(String) 来创建一个 Boolean 对象。

2. 除了不可变对象，如果可变对象确定不会被修改，当然也可以重用

```java
// Performance can be greatly improved!
static boolean isRomanNumberal(String s) {
    return s.matches("^(?=.)M*(C[MD]|D?C{0,3})" + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```
这段代码是用正则表达式来判断一个字符串是否是罗马数字，String.matches 在内部会为正则表达式创建一个 Pattern 对象，却只使用一次，之后就可以被 GC，并且创建 Pattern 对象是昂贵的，因为它需要将正则表达式编译成 finite state machine（有限状态机）。

所以，怎样做才能提高性能呢？
```java
// Reusing expensive object for improved performance
public class RomanNumberals {
    private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})" + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    
    static boolean isRomanNumberal(String s) {
        return ROMAN.matcher(s).matches();
    }
}
```
将正则表达式显式编译为一个 Pattern 对象，并将其标志为 static final，因此这个对象只会在该类初始化时创建一次，并且这个对象的引用也不会发生改变，从而保证了这个对象不可改变，并起到了缓存的作用，最后在 isRomanNumberal 中调用它，就可以避免重复创建 Pattern 对象。

3. 再有就是要注意自动装箱和拆箱问题，要优先使用基本类型而不是装箱基本类型，因为装箱可能会创建不必要的对象和高开销。


### Item 7: Eliminate obsolete object references

"memory leak" 是一个很难察觉的问题

1. 第一种常见的来源是使用数组存储对象引用时
```java
import java.util.*;

class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        return elements[--size];
    }

    /**
    * Ensure space for at least one more element, roughly
    * doubling the capacity each time the array needs to grow.
    */
    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```
哪里有 "memory leak" 呢？栈在 push 后再 pop 出的对象不会被 GC，因为 elements 数组在一开始创建时就指定了大小，数组的长度是不能动态变化的，而数组是一个对象，对于 GC 来说，elements 数组中的所有对象引用都是有效的，而这些引用在 size 以外的是过期引用。如果只有几个这样的过期引用不会有太大影响，但多了之后必定会影响性能。

解决方法就是清空这些过期引用
```java
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null; // Eliminate obsolete reference
        return result;
    }
```

2. 第二种常见来源是使用缓存时，可能容易忘了缓存里还有东西。

可以帮助我们解决这样的问题的有 WeakHashMap，它可以在 key 不被引用时，对这个 map 进行 GC。

### Item 8: Avoid finalizers and cleaners

Java 9 已经弃用里 finalizer 机制，使用 cleaner 来代替。但 cleaner 还是无法保证能及时执行，所以仍然要避免使用

### Item 9: Prefer try-with-resources to try-finally

try-with-resources 是 Java 7 的新语法
```java
    // try-with-resources - the best way to close resources!
    static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(path))) {
            return br.readLine();
        }
    }
```
在上面的代码中，会发现并没有显式地调用 BufferReader 的 close 方法关闭流，以前通常我们会这样做，在 finally 中关闭资源。
```java
    // try-finally - No longer the best way to close resources!
    static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
```
但是有一种情况，当 firstLineOfFile 方法由于底层物理设备出故障时，调用 readLine 就会发生异常，同时 close 也可能会因此发生异常，并且调用 close 产生的异常会 obliterates 调用 readLine 产生的异常，那么此时就无法追踪第一个异常了。

而使用 try-with-resources 可以解决这个问题，只要资源实现了 AutoCloseable 接口，就可以使用这个语法，上面的 BufferReader 会自动调用 close 方法，无论是正常执行还是发生异常。

当然，在 try-with-resources 中同样可以使用 catch 处理异常
```java
    // try-with-resources with a catch clause
    static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new FileReader(path))) {
            return br.readLine();
        } catch (IOException e) {
            return defaultVal;
        }
    }
```