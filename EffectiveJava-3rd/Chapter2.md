[TOC]

## 2. Methods Common to All Objects

### Item 10: Obey the general contract when overriding equals

Object 的非 final 方法（equals、hashCode、toString、clong、finalize）都有明确的 general contracts，它们被设计为被子类 override。在 override 这些方法时，必须遵守这些 contracts，以防依赖于这些 contracts 的类（e.g. HashMap、HashSet）无法正常工作

不过并不是百分百必须 override equals() 方法，这两种条件同时成立时，才必须 override equals（毕竟 override 了就有可能出错，如果可以不 override 那当然最好）

1. 类要求定义自己的 "logical equals" 的概念
2. 超类没有 override equals() 或者 override 了但子类继承这个不合适

这种类 effective java 称其为 value class（值类），通常 value class 是需要 override equals() 的，不过有一种 value class 不用，就是枚举类，因为它是 instance control 的类，可以确保每个值只有一个对象，对这样的类，"logical equals" = "Object's equals"。

然后 effJ 用了大篇幅去解释 override equals() 应该遵守的约定，最后得出的编写高质量 equals 方法的结论和 Core Java 的基本一致：

把 Core Java 的例子放一下：
```java
public class Employee {
    ...
    public boolean equals(Object otherObject) {

        // 检查参数是否是这个对象的引用
        if (this == otherObject) return true;

        // 参数为 null ，则根据约定，对任何非 null 的引用值 x，x.equals(null) 必须返回 false
        if (otherObject == null) return false;

        // 类不同，不相谋
        if (getClass() != otherObject.getClass())
            return false;

        // 把参数转换成正确的类型
        Employee other = (Employee)otherObject;

        // logical equals
        return name.equals(other.name)
            && salary == other.salary
            && hireDay.equals(other.hireDay);
    }
}
```
CoreJ 使用 getClass 判断类型，而不是使用 instanceof 的原因是，如果超类的 equals 对子类不适用，也就是说 equals 的语义在每个子类中有变化，那么就只能用 getClass 判断，如果子类语义都相同，用 instanceof 就可以，当然，如果超类中定义了 equals，就要先调用超类的 equals 看是否相等，如果为 false，就不用继续了。

### Item11: Always override hashCode when you override equals