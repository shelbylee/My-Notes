* [Java 基础知识](#java-基础知识)
  * [一、对象与类](#一对象与类)
    + [1. 用户自定义类](#1-用户自定义类)
      - [1.1 构造器](#11-构造器)
      - [1.1.2 隐式参数与显式参数](#112-隐式参数与显式参数)
      - [1.1.3 final 实例域](#113-final-实例域)
    + [1.2 静态域与静态方法](#12-静态域与静态方法)
      - [1.2.1 静态域](#121-静态域)
      - [1.2.2 静态常量](#122-静态常量)
      - [1.2.3 静态方法](#123-静态方法)
      - [1.2.4 call by value or call by reference?](#124-call-by-value-or-call-by-reference)
    + [1.3 对象构造](#13-对象构造)
      - [1.3.1 重载（overloading）](#131-重载overloading)
      - [1.3.2 默认域初始化](#132-默认域初始化)
      - [1.3.3 无参构造器](#133-无参构造器)
      - [1.3.4 调用另一个构造器](#134-调用另一个构造器)
  * [2. 继承](#2-继承)
    + [2.1 类、超类、子类](#21-类-超类-子类)
      - [2.1.1 super 关键字](#211-super-关键字)
      - [2.1.2 多态](#212-多态)
      - [2.1.3 理解方法调用](#213-理解方法调用)
      - [2.1.4 阻止继承：final 类和方法](#214-阻止继承final-类和方法)
      - [2.1.5 强制类型转换](#215-强制类型转换)
      - [2.1.6 抽象类](#216-抽象类)

## Java 基础知识

### 一、对象与类

#### 1. class

##### 1.1 构造器

1. 构造器必须与类同名
2. 构造器不能有返回值
3. 构造器总是伴随着 new 操作符的执行被调用
4. 不能在构造器中定义与实例域重名的局部变量
```java
class Employee {

    private String name;
    private double salary;

    public Employee (String n, double s) {
        String name = n; // Error
        double salary = s; // Error
    }
}
```

##### 1.1.2 隐式参数与显式参数

对于一个非静态方法，总是会有一个隐式（implicit）参数，也就是调用这个方法的对象。关键字 this 表示隐式参数，下面这种用 this 代指的方式可以明显的区分实例域和局部变量。
```java
public void raiseSalary(double byPercent) {
    double raise = this.salary * byPercent / 100;
    this.salary += raise;
}
```

##### 1.1.3 final 实例域

如果一个域被 final 修饰，那么要求构建对象时 **必须初始化** 这样的域，并且在后面的操作中 **不能对该域进行修改**。
```java
class Student {

    private final String name;;
    private final int id;

    public Student(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name; // 错误: 无法为最终变量name分配值
    }

    @Override
    public String toString() {
        return getClass().getName() + "[name=" + name + ",id=" + id + "]";
    }

    public static void main(String[] args) {
        Student student = new Student("Amanda", 2015);
        student.setName("123");
        System.out.println(student.toString());
    }
}
```
如上所示，无法为 name 域编写 set 方法，即无法在构造器对 final 实例域初始化后对其进行修改。

final 大多用在修饰 primitive 类型域 or immutable 类的域，因为如果用在可变的类上，会使 final 含义的表达有些混乱。

#### 1.2 静态域与静态方法

##### 1.2.1 静态域

如果一个域为 static 域，那么每个类中只有一个这样的域，这个域 **属于类**，而不属于对象。

而对于非 static 域，每个对象对其都有一个自己的 copy。

比如下面这个例子，Amanda 和 Joe 都有对实例域 id 的一个 copy，但是 Amanda 和 Joe 共享同一个 nextId，并且即使不存在 Amanda 和 Joe 对象，静态域 nextId 也存在，因为它属于类 Student~
```java
class Student {

    private int id;
    private static int nextId = 1;

    public void setId() {
        id = nextId; // Amanda.id = Student.nextId;
        nextId++; // Student.nextId++;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }

    public static void main(String[] args) {
        Student Amanda = new Student();
        Student Joe = new Student();
        Amanda.setId();
        Joe.setId();
        System.out.println(Amanda.toString());
        System.out.println(Joe.toString());
    }
}
```

##### 1.2.2 静态常量

static final 即静态常量，比如 Math 中的 PI
```java
public class Math {
    ...
    public static final double PI = ...;
    ...
}
```
可以通过 Math.PI 获取这个静态常量。

会发现，在这里 PI 域被设置为 public，通常我们不会轻易将域设置为 public，因为每个类对象都可以对 public 域进行修改，这会破坏封装性，但是这里上 static final，被 final 修饰说明该域初始化后不允许被修改，因此将 final 域设置为 public 是没有问题的。

##### 1.2.3 静态方法

前面提到过，非静态方法都是有隐式参数的，那么也就是说，静态方法上没有隐式参数的，即没有 this 参数的方法。

static 方法上不能操作对象的，因此也不能访问对象的非 static 实例域，而访问 static field 是没有问题的。

那么，什么时候需要使用 static method 呢？

1. 一个方法不需要访问对象状态，其所需参数都由显式参数提供即可（e.g. Math.pow）
2. 一个方法只需要访问类的静态域

##### 1.2.4 call by value or call by reference?

- call by value: 接收的是值
- call by reference: 接收的是变量地址

方法可以修改 call by reference 对应的变量值，但是不能修改 call by value 对应的值。

Java 的方法参数的传递方式是 call by value 。

对于 primitive 类型的变量很好理解
```java
int id = 1;
changeId(id);

public static void changeId(int id) {
    id++;
}
```
很明显我们是无法通过 changeId 修改变量 id 的值的，即 **不能修改 primitive 类型的参数**。

而对于 reference 类型的变量就让人有些迷惑了，因为很容易就 **可以改变传入的对象的状态**，这是为啥呢？

因为传入的是对象的引用的 copy！此时对象引用和对象引用的 copy 都引用了同一个对象，因此通过 copy 改变了对象的状态。

再比如，如果 Java 是 call by reference，那么下面这种交换两个学生对象的方法应该能改变最后的输出结果
```java
class Student {

    private int id;

    public Student(int id) {
        this.id = id;
    }

    public static void swap(Student a, Student b) {
        Student temp = a;
        a = b;
        b = temp;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + "]";
    }

    public static void main(String[] args) {
        Student Amanda = new Student(1);
        Student Joe = new Student(2);
        swap(Amanda, Joe);
        System.out.println(Amanda.toString());
        System.out.println(Joe.toString());
    }
}
```
然而最后的输出结果仍然是
```
Student[id=1]
Student[id=2]
```
说明 Java 中的方法是 **不能让对象参数引用一个新对象** 的

#### 1.3 对象构造

##### 1.3.1 重载（overloading）

动词 overload 本就是超载的意思，名字为 A 的一个方法，却承载着多个功能，不就是 overloading 了么，这也是 Java 为我们提供的多种编写构造器来构造对象的一种机制。

一些类有多个构造器，e.g. 你可以这样构造一个空 StringBuilder 对象：
```java
    StringBuilder messages = new StringBuilder();
```
or
```java
    StringBuilder todoList = new StringBuilder("To do:\n");
```
这就是 overloading。当多个方法具有相同的名字且拥有不同的参数时，就会出现重载，由编译器决定调用哪个方法，这个过程被称为 overloading resolution（重载解析）。

不只是可以重载构造器，Java 允许重载任何方法，要完整的描述一个方法，需要指出方法名和参数类型，这叫做方法的签名（signature）。

e.g. String 有四个 public 的 indexOf 方法，它们的签名是：
```java
    indexOf(int)
    indexOf(int, int)
    indexOf(String)
    indexOf(String, int)
```
需要注意的是，返回类型并不是 signature 的一部分，也就是说，编译器无法通过返回类型来区分不同的重载方法，因此，重载方法是同名不同参的方法，与返回类型无关。

**关于覆盖（override）**

虽然 override 不适合放在 “对象与类” 部分里，应该在 “继承” 里总结，但是这俩容易混，所以放在这记录一下。

当父类中的一些方法对子类不适用时，就需要提供一个新的方法来 override 父类中的这个方法。

那么既然是对 **父类** 的 override，就不能去修改这个方法的签名，只能对内部做调整，而且不仅仅是签名不能改，方法返回类型以及访问权限也不能改。

因此，当发现子类中某个方法和父类的签名相同时，那么这一定是 override 而不是 overload。因为要求的是签名相同，而返回类型不属于签名，但是也要保证返回类型的兼容，语法上是 **允许子类在 override 时将返回类型定义为原方法返回类型的子类类型的**。

e.g.
```java
// 这两个方法具有可协变的返回类型

public Student getBuddy() {...}

public Monitor getBuddy() {...}
```

还有就是，在 override 时，**子类方法的可见性不能低于超类方法**。

在编写代码时，建议在覆盖一个方法时加上 @override，除了标识的作用，它还会帮助你检查你覆盖的这个方法是否在父类中真的存在。

**关于区分 overload 和 override**

其实不需要死记其语法，只需要知道，overload 主要是在构造对象时发挥了很大的作用，通过 overload 可以有多种构造对象的方式，而 override 是出现在发生继承时才有意义。

##### 1.3.2 默认域初始化

如果在构造器中没有对域进行初始化，那么这些域就会被初始化为默认值，e.g. 数值为 0，boolean 值为 false，对象引用为 null。而局部变量就不会被默认初始化，如果没有给局部变量赋值就会出错。

但是，良好的习惯是需要对域进行初始化的。

##### 1.3.3 无参构造器

如果编写一个类时用户没有提供构造器，那么系统会提供一个无参的默认构造器，其中域的值都是默认初始化的值。

如果用户提供了一个构造器，但是没有提供无参构造器，此时就不存在无参的构造器了，因为系统不会再为你提供了。因此如果已经给出了一个构造器，同时又想要采用 new ClassName() 的方式构造对象，一定要记得再编写一个无参构造器。

##### 1.3.4 调用另一个构造器

this 除了表示方法的隐式参数，还可以在一个构造器中调用同一个类的另一个构造器，但是这条调用语句必须是这个构造器的第一句。

```java
import java.util.*;

class Student {

    private static int nextId;

    private int id;
    private String name = ""; // 初始化实例域

    // static 初始化块
    static {
        Random generator = new Random();
        nextId = generator.nextInt(10000);
    }

    // 对象初始化块
    {
        id = nextId;
    }

    public Student(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Student() {}

    public Student(String name) {
        // 调用另一个构造器 Student(String name, int id)
        this(name, nextId);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[name=" + name + ",id=" + id + "]";
    }

    public static void main(String[] args) {
        Student[] students = new Student[3];
        students[0] = new Student("Joe", 1);
        students[1] = new Student("Amanda");
        students[2] = new Student();

        for (Student s : students) {
            System.out.println(s.toString());
        }
    }
}
```

### 2. 继承

#### 2.1 类、超类、子类

##### 2.1.1 super 关键字

super 是指示编译器 **调用超类方法** 的特殊关键字。

除了调用超类方法，它还可以调用超类的构造器，和 this 类似，这条语句也必须是构造器的第一句。

总结一下 this 和 super 这两个关键字：

- this 的作用：
    - 引用隐式参数
    - 调用该类的其他构造器
- super 的作用
    - 调用超类方法
    - 调用超类的构造器
- 共同点：使用时，调用构造器的语句必须是第一句
- 不同点：this 是对一个对象的引用，但 super 不是，不能将 super 赋给另一个对象变量。

##### 2.1.2 多态

一个变量可以指示多种实际类型的现象称为多态（polymorphism），e.g. 一个 Employee 变量可以引用 Employee 对象，也可以引用 Manager 对象等子类的对象。

可以通过 "is-a" 规则判断是否应该设计为继承关系，这个规则表明子类的每个对象也是超类的对象。

另一种表述是 *置换法则*，它表明程序中出现超类对象的任何地方都可以用子类对象置换。然而不能将超类的引用赋给子类变量。

但是要注意的是，在 Java 中，子类数组的引用是可以赋给超类数组的，并且不需要强转 e.g.
```java
Manager[] managers = new Manager[10];
Employee[] staff = managers; // OK

// 现在 managers 和 staff 引用的是同一个数组

```
如果将一个超类对象赋给子类数组呢？
```java
staff[0] = new Employee("Amanda",...);
```
会发现，编译器是不会报错的，但是这样做是错误的，因为一个 Manager 数组怎么能存储比它低级别的 Employee 对象呢，所以运行时会报错
```
Exception in thread "main" java.lang.ArrayStoreException: Employee
```

##### 2.1.3 理解方法调用

x.fun(param) 方法，x 为隐式参数，代指某个对象，调用过程如下：

1. 编译器查看对象的声明类型和签名。

因为可能有多个 overload 的 fun 方法，e.g. fun(String) 和 fun(int)，所以编译器会列举出这些重名方法和其超类中 public 的名为 fun 的方法。

现在，编译器已经获得了所有可能被调用的候选方法。

2. 然后，编译器查看调用方法时提供的参数类型。如果提供的这个参数类型可以和候选方法中的任意一个匹配上，就选择这个方法，这个过程叫做 overloading resolution。如果没有找到，编译器就会报错。

现在，编译器已经获得了要调用的方法的名称和参数类型，假设是 fun(String)，但是还没有找到要调用的方法。

3. 如果这个方法是 private or static or final 方法或构造器，那么编译器现在就可以准确知道应该调用哪个方法，这种调用方式称为 **静态绑定（static binding）**。如果不是前面几种方法的话，那么调用哪个方法取决于隐式参数 x 的实际类型，并且在运行时实现 **动态绑定（dynamic binding）**（在运行时能够自动选择调用哪个方法的现象）。
4. 当程序运行，并且采用 dynamic binding 方式调用方法时，虚拟机会在 *方法表（method table，其中列出了所有方法的签名）* 中查找和 fun(String) 匹配的方法，比如匹配上的是 A.fun(String)，假设 x 为 A 类，当然也有可能匹配上的是 B.fun(String)，B 为 A 的超类。

到这里，整个方法的调用就完成了。

dynamic binding 的一个好处就是：对程序扩展后，不需要完全重新编译程序。比如，如果新增一个 Developer 类，有可能有变量 e 引用这个新类的对象，那么我们不需要重新编译包含调用 e.getXXX() 的代码，因为如果 e 引用了 Developer 类的对象，那么运行时就会自动调用 Developer.getXXX() 方法，这就是 dynamic binding。

##### 2.1.4 阻止继承：final 类和方法

final 类时不允许被扩展的类，也就是说，final 类不允许被继承，对于方法也是类似，final 方法无法被 override。

final 类中的方法会被自动成为 final，但域不是。

将方法或类声明为 final 主要是为了确保它们在子类中的语义不会改变。

##### 2.1.5 强制类型转换

使用类型转换的唯一原因是：在暂时忽略对象的实际类型后，能够使用对象的全部功能。

```java
        Employee[] staff = new Employee[10];

        staff[0] = new Manager("Amanda", 2015, 10000);
        staff[1] = new Employee("Joe", 2016, 8000);
```

```java
        // 将 staff[0] 强转为 Manager -> 暂时忽略对象的实际类型
        // 以便 boss 引用这个对象时可以使用 Manager 的新增的变量 -> 使用对象的全部功能
        Manager boss1 = (Manager) staff[0]; 
```
将子类引用赋给超类变量是不需要强转的，反过来必须强转，这样才能通过运行时的检查。

如果将超类引用赋给子类变量强转时撒谎，会发生什么呢？
```java
        // staff[1] 本身为 Employee
        // 强转时谎称自己为 Manager，想骗别人自己是 boss 没那么容易哟
        Manager boss2 = (Manager) staff[1]; // java.lang.ClassCastException
```

因此在进行强转之前，要用 instanceof 检查一下能否强转成功再进行强转，以避免 ClassCastException 导致程序退出。
```java
        if (staff[1] instanceof Manager) {
            Manager boss2 = (Manager) staff[1];
        }
```

总结：

1. 只有在继承层次内才能使用强转，即将超类强转为子类类型。
2. 强转前要用 instanceof 检查。
3. 没有必要用强转的地方就不要用，应该尽量少用类型转换和 instanceof 运算符。

##### 2.1.6 抽象类

很可能有这种需求：希望一个基类来派生其他类，因为这个基类更通用，但是不想让它作为用来使用的特定的实例类。

比如，Person 类可以派生出 Student 和 Teacher 类，之所以进行这么高层次的抽象是为了将一些通用的方法和属性放到通用的超类中，比如 name,sex 等等，从而避免了在多个子类中再去重复的定义这些属性或方法。

但是有一个问题，当在一些子类中有用的方法，比如 getDescription() 方法，在超类 Person 中就不知如何定义，此时就需要用到 abstract 关键字了，声明为 abstract 的方法为抽象方法，我们不需要实现抽象方法。

抽象方法起到 **占位** 的作用，具体的实现交给子类。

在扩展抽象类时有两种方式：

1. 在抽象类中只定义部分 abstract 方法甚至是不定义 abstract 方法，这样的话子类也必须定义为抽象类。
2. 定义全部方法为 abstract 方法，这样子类就不需要定义为抽象类。

需要注意一点：**抽象类不能被实例化**，不过可以定义抽象类的对象变量，但是这个变量只能引用 **非抽象子类** 的对象。

