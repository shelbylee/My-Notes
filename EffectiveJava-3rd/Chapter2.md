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

### Item 11: Always override hashCode when you override equals

如果 override equals，必须 override hashCode，因为如果不这样做就会违反了 Object 的其中一条规约
> If two objects are equal according to the equals(Object) method, then calling hashCode on the two objects must produce the same integer result.

相等的对象必须拥有相等的 hash code。

如果不遵守规约，那么这个类可能就无法与 HashMap 正常合作。

那么，应该怎样 override hashCode 才合适呢？

首先，必须要保证用 equals 比较相等的对象的 hashCode 返回值相等；再者，用 equals 比较的不同的对象的 hashCode 返回值要尽量不相等。写完一个 hashCode 方法要检查一下，是否能保证相等的对象拥有相等的 hash code。

通常 hashCode() 应该返回一个 int 型值（也可以是负数），并合理地组合实例域的 hash code，以便能让各个不同对象产生的 hash code 更均匀。

并且使用 null 安全的方法 Objects.hashCode()，参数为 null 会返回 0。同时如果有 double 类型，最好使用静态方法 Double.hashCode() 来避免创建 Double 对象。

如果需要组合多个散列值，可以使用 Objects.hash 并提供多个参数。

### Item 12: Always override toString

toString 方法应该返回所有值得关注的信息

### Item 13: Override clone judiciously


### Item 14: Consider implementing Comparable

实现 Comparable 接口，就说明该类的实例具有 natural ordering，实现这个接口的对象就可以使用 Arrays.sort() 、Collections.sort() 以及可以作为 TreeMap 的 key 或者 TreeSet 的元素，而不需要指定 Comparator。

这个接口只有一个方法 compareTo(T).

几乎所有的 Java 类库中的 value classes 都实现了 Comparable<T>，如果要编写的类是一个 value class，并且有很明显的内在排序关系，像按字母排序、按数值排序、按年代排序等等，就应该实现 Comparable 接口。

compareTo 和 equals 类似但也不同，e.g. BigDecimal 类，它的 compareTo 和 equals 不一致。如果在 HashSet 中添加 new BigDecimal("1.0") 和 new BigDecimal("1.00")，两个都会添加进去，因为它是使用 equals 比较是不相等的；如果在 TreeSet 中添加，就只会添加进去一个元素，因为它是使用 compareTo 比较是相等的。