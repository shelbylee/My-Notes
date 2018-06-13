- [Java 容器](#java---)
  * [一、概述](#----)
  * [二、源码学习](#------)
    + [1. Map](#1-map)
      - [1.1 HashMap](#11-hashmap)
      - [1.2 LinkedHashMap](#12-linkedhashmap)
      - [1.3 TreeMap](#13-treemap)
      - [1.4 ConcurrentHashMap](#14-concurrenthashmap)
    + [2. Set](#2-set)
      - [2.1 HashSet](#21-hashset)
      - [2.2 LinkedHashSet](#22-linkedhashset)
      - [2.3 TreeSet](#23-treeset)
    + [3. List](#3-list)
      - [3.1 ArrayList](#31-arraylist)
      - [3.2 LinkedList](#32-linkedlist)
      - [3.3 Vector](#33-vector)
    + [4. Queue](#4-queue)
      - [4.1 LinkedList](#41-linkedlist)
      - [4.2 PriorityQueue](#42-priorityqueue)

## Java 容器

嘛，容器的源码，慢慢学咯

主要是数据结构方面的，还有接口的设计，设计模式，并发...，自己看有的地方还是很吃力，比如 RBT =.=。很多东西一深挖真的很难，这两天就写了这些，还没写完，红黑树刚开始看，ConcurrentHashMap 对我来说有的东西还不好理解，应该是知识面还不够，会再继续。

后续应该会补充上迭代器，时间复杂度。

### 一、概述

![image](http://www.codejava.net/images/articles/javacore/collections/collections%20framework%20overview.png)
图源： http://www.codejava.net

这是整个 Java Collections Framework 的架构，可以看到，Map 是单独的一个接口，而其他的接口 Set、List、Queue 均继承于 Collection 接口。

我打算主要学习以下几个常用的容器：

- Map:
    - HashMap
    - LinkedHashMap
    - ConcurrentHashMap
    - TreeMap
- Set:
    - HashSet
    - LinkedHashSet
    - TreeSet
- List:
    - ArrayList
    - LinkedList
    - Vector
- Queue:
    - LinkedList
    - PriorityQueue

### 二、源码学习
（基于 JDK 1.8）

#### 1. Map
##### 1.1 HashMap
这个比较长，直接写到博客里了。  
[Java 容器学习之 HashMap](https://shelbylee.github.io/post/2018/05/30/)

##### 1.2 LinkedHashMap

1. 简介

和 HashMap 不同的地方在于 LinkedHashMap 维护了双链表，我觉得就相当于 HashMap 和 LinkedList 的结合体，因此 LinkedHashMap 可以保证顺序，并且这个顺序可以是 access-order or 
insertion-order，由 accessOrder 这个成员变量来维护。LinkedHashMap 的核心就在于它对于顺序的维护和对 LRU 的实现。
```java
    final boolean accessOrder;
```

2. 定义
```java
public class LinkedHashMap<K,V>
    extends HashMap<K,V>
    implements Map<K,V>
```

3. 静态内部类 Entry

因为 JDK 1.8 在 HashMap 中引入了红黑树，所以 HashMap 的一些结点使用的是 tree，因此 LinkedHashMap.Entry 这个类被当作  intermediary node class （中间结点类），也可以将其转换成树的形式，所以用 Entry 这个名字可能有点 confusing，但是名字因为某些原因不能改 orz 作者也做了说明。

同时还需要维护两个字段 head、tail 来维护双链表
```java
    static class Entry<K,V> extends HashMap.Node<K,V> {
        // 前驱后继
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

    // 双链表的头结点 or 最先进入的结点
    transient LinkedHashMap.Entry<K,V> head;

    // 双链表的尾结点 or 最后进入的结点
    transient LinkedHashMap.Entry<K,V> tail;
```

4. 对 LRU 的实现

LinkedHashMap 对 put 没有 override，也就是说直接调用的 HashMap 的 put 方法，那它怎么实现的 LRU 呢？因为在 HashMap 的 put 中其实调用了相应的方法 afterNodeAccess、afterNodeInsertion，只不过是借助于回调函数，让这些方法在 LinkedHashMap 才实现而已。
```java
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        {

                    ......

            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```
回调函数
```java
    // Callbacks to allow LinkedHashMap post-actions
    void afterNodeAccess(Node<K,V> p) { }
    void afterNodeInsertion(boolean evict) { }
    void afterNodeRemoval(Node<K,V> p) { }
```

LinkedHashMap 对 get 进行了 override
```java
    public V get(Object key) {
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) == null)
            return null;
        if (accessOrder)
            afterNodeAccess(e);
        return e.value;
    }
```

LinkedHashMap 默认是不开启 LRU 的，如果开启了 LRU 规则，也就是说 accessOrder == true，才会调用相应方法。

看一下 afterNodeAccess，这个方法的作用是将最近访问的结点放到双链表尾部。
```java
    void afterNodeAccess(Node<K,V> e) { // move node to last
        LinkedHashMap.Entry<K,V> last;
        if (accessOrder && (last = tail) != e) {
            // p:当前结点，b:前驱 a:后继
            LinkedHashMap.Entry<K,V> p =
                (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            // 将当前结点和后继断开
            p.after = null;
            // 如果前驱为空，那么后继就变成了当前双链表的头结点
            if (b == null)
                head = a;
            else
            // 下面连接前驱后继
                b.after = a;
            if (a != null)
                a.before = b;
            else
            // 如果后继为空，让前驱先成为尾结点
                last = b;
            if (last == null)
                head = p;
            else {
                // 将当前结点插入到尾部
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
    }
```

前面在 HashMap 的 put 方法中还看到最后调用了 afterNodeInsertion，默认的 evict（可以翻译为驱逐） 为 true。
```java
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
```

那么在 afterNodeInsertion 中会再次判断一下是否需要删除最旧的结点，这是通过 removeEldestEntry 来判断的。
```java
    void afterNodeInsertion(boolean evict) { // possibly remove eldest
        LinkedHashMap.Entry<K,V> first;
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }
```

```java
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }
```
会发现这个方法始终返回 false，也就是默认不会删除，在需要实现 LRU cache 时可以继承后 override 这个方法，开启淘汰机制，按照自己的需求决定什么时候才删除最旧的结点。

TODO: LRU 算法具体实现

##### 1.3 TreeMap

1. 简介

基于红黑树的 NavigableMap 的实现，可按 key 自然顺序排序，或者根据提供的 Comparator 排序。

2. 定义

```java
public class TreeMap<K,V>
    extends AbstractMap<K,V>
    implements NavigableMap<K,V>, Cloneable, java.io.Serializable
```

3. 重要属性
```java
    // 比较器可以用来保证顺序，如果为空，则按 key 的自然顺序
    private final Comparator<? super K> comparator;

    // 红黑树根结点
    private transient Entry<K,V> root;

    // 树中结点个数（容器大小）
    private transient int size = 0;

    private transient int modCount = 0;

    // Red-black mechanics

    private static final boolean RED   = false;
    private static final boolean BLACK = true;
```

Entry 中的属性包括
```java
        K key;
        V value;
        Entry<K,V> left;
        Entry<K,V> right;
        Entry<K,V> parent;
        boolean color = BLACK;
```

4. Red-Black Tree

要想弄明白 TreeMap，还是要先理解 RBT。

- 红黑树是一种高效的二叉查找树（BST, Binary Search Tree），时间复杂度为 O(logn)，首先它肯定具有 BST 的特点：
    - BST 的特点：
        - 左子的值 <= 父节点的值
        - 右子的值 >= 父节点的值

BST 虽然也可以快速查找，但是有一个问题，在最坏的情况下，如果要插入的序列本身就是有序的，那么这棵树显然就是一个只有左子 or 只有右子的树，这种情况再进行查找，就和遍历链表无异了。因此，为了改进 BST，相应的出现了一些基于 BST，但比其更高效的数据结构，比如 RBT。

- RBT 除了上面的特点之外，还具有以下五个特点：
    - 节点是红色或黑色。
    - 根节点是黑色。
    - 每个叶子节点都是黑色的空节点（NIL节点）。
    - 每个红色节点的两个子节点都是黑色。(从每个叶子到根的所有路径上不能有两个连续的红色节点)
    - 从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点。

同时，为了保持上面的五个特点，在增删改的过程中需要对树进行相应的操作（左、右旋，变色）。

5. RBT 和 AVL 树

AVL 和 RBT 都是自平衡的二叉树，不过 RBT 并不符合 AVL 树的平衡条件，也就是说相对于 RBT，AVL “更平衡”，RBT 是用这种非严格的平衡来换取增删结点时旋转次数的降低，由于 RBT 的设计，任何不平衡都会在三次旋转之内解决，而 AVL 也被称为高度平衡树，它的左子和右子的高度差距不能超过一，因此 AVL 在增删时，旋转的次数可能比红黑树要多，不过 RBT 也少不了变色的操作，所以具体 RBT 和 AVL 哪个更快，我也说不清楚，知乎上有人自己做了一下测评，结论是差不多... [AVL树，红黑树，B树，B+树，Trie树都分别应用在哪些现实场景中？ - 韦易笑的回答 - 知乎](https://www.zhihu.com/question/30527705/answer/259948086) emmm 这个无关紧要，自己写很难比得上库里的吧，还是老老实实调用吧 orz 

6. 查找

查找就很简单了，但是，有一个小小的需要注意的地方

> A return value of null does not necessarily indicate that the map contains no mapping for the key; it's also possible that the map explicitly maps the key to null. The containsKey operation may be used to distinguish these two cases.

这是文档中对 get 方法的一段描述，意思是即使 get(key) 返回 null，也不一定表明不包含这么一个 map，因为可以存储 null 值呀。

那么 TreeMap 可以存储 null 的 key 吗？

答案是可以的，因为可以传入比较器，所以可以通过比较器强制让其为 null 时返回一个值，而 TreeMap 在 get 时会根据我们是否传入比较器去 get。

```java
    public V get(Object key) {
        Entry<K,V> p = getEntry(key);
        return (p==null ? null : p.value);
    }
```

```java
    final Entry<K,V> getEntry(Object key) {
        // Offload comparator-based version for sake of performance
        // 如果比较器非空，采用用户提供的比较器
        if (comparator != null)
            return getEntryUsingComparator(key);
        if (key == null)
            throw new NullPointerException();
        @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
        Entry<K,V> p = root;
        while (p != null) {
            // 将传入的 key 与当前结点的 key 比较
            int cmp = k.compareTo(p.key);
            // 如果传入的 key 小，继续查找左子
            if (cmp < 0)
                p = p.left;
            // 如果传入的 key 大，继续查找右子
            else if (cmp > 0)
                p = p.right;
            else // 找到了
                return p;
        }
        return null;
    }
```
采用用户提供的比较器查找
```java
    final Entry<K,V> getEntryUsingComparator(Object key) {
        @SuppressWarnings("unchecked")
            K k = (K) key;
        Comparator<? super K> cpr = comparator;
        if (cpr != null) {
            Entry<K,V> p = root;
            while (p != null) {
                int cmp = cpr.compare(k, p.key);
                if (cmp < 0)
                    p = p.left;
                else if (cmp > 0)
                    p = p.right;
                else
                    return p;
            }
        }
        return null;
    }
```

7. 添加
```java
    public V put(K key, V value) {
        Entry<K,V> t = root;
        // 如果当前树为空，则直接创建一个根结点
        if (t == null) {
            compare(key, key); // type (and possibly null) check

            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        // 存放比较后的结果
        int cmp;
        Entry<K,V> parent;
        // split comparator and comparable paths
        // 获取比较器
        Comparator<? super K> cpr = comparator;
        // 如果比较器不为空，则按照用户提供的比较器进行排序
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                // 如果插入的 key 比该结点的 key 小，则继续遍历左子
                // 直到找到可以插入的合适位置（维持平衡）
                if (cmp < 0)
                    t = t.left;
                // 同理
                else if (cmp > 0)
                    t = t.right;
                else
                    // 如果相等，则覆盖之前的值
                    return t.setValue(value);
            } while (t != null);
        }
        else {
            // 按照自然顺序排序
            if (key == null)
                throw new NullPointerException();
            @SuppressWarnings("unchecked")
                Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0)
                    t = t.left;
                else if (cmp > 0)
                    t = t.right;
                else
                    return t.setValue(value);
            } while (t != null);
        }
        Entry<K,V> e = new Entry<>(key, value, parent);
        // 把这个插入的新结点挂上去
        // 根据比较的结果决定挂到左子还是右子
        if (cmp < 0)
            parent.left = e;
        else
            parent.right = e;
        // 在插入后保证红黑树的平衡
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }
```

8. 删除

RBT 的删除比较复杂

- 要删的结点是叶子结点，这是最简单的情况，直接删除就可以
- 要删的结点只有左子树
    - 将该结点的左子树挂到该结点的父结点的左子上就可以
- 要删的结点只有右子树
    - 将该结点的右子树挂到该结点的父结点的右子上就可以
- 要删的结点左右子均非空
    - TreeMap 中采用的方式是这样的：用该结点的树的中序的前驱 or 后继来代替这个要被删除的结点，也就是用该结点的左子最大者 or 右子最小者来代替它，然后将子结点删除就可以，这样可以简化删除的过程。

```java
    /**
     * Delete node p, and then rebalance the tree.
     */
    private void deleteEntry(Entry<K,V> p) {
        modCount++;
        size--;

        // If strictly internal, copy successor's element to p and then make p
        // point to successor.
        // 如果左右子均非空，用该结点的 successor（右子最小者）来代替
        if (p.left != null && p.right != null) {
            Entry<K,V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        } // p has 2 children

        // Start fixup at replacement node, if it exists.
        // 如果左子存在，用 replacement 表示左子，否则表示右子
        Entry<K,V> replacement = (p.left != null ? p.left : p.right);

        if (replacement != null) {
            // Link replacement to parent
            // 将 replacement 连到要删除结点的父结点上
            replacement.parent = p.parent;
            if (p.parent == null)
                root = replacement;
            // 如果要删的结点是父结点的左子，就把 replacement 挂到左子上
            else if (p == p.parent.left)
                p.parent.left  = replacement;
            // 如果要删的结点是父结点的右子，就把 replacement 挂到右子上
            else
                p.parent.right = replacement;

            // Null out links so they are OK to use by fixAfterDeletion.
            p.left = p.right = p.parent = null;

            // Fix replacement
            if (p.color == BLACK)
                fixAfterDeletion(replacement);
        } else if (p.parent == null) { // return if we are the only node.
            root = null;
        } else { //  No children. Use self as phantom replacement and unlink.
            if (p.color == BLACK)
                fixAfterDeletion(p);

            if (p.parent != null) {
                if (p == p.parent.left)
                    p.parent.left = null;
                else if (p == p.parent.right)
                    p.parent.right = null;
                p.parent = null;
            }
        }
    }
```
在 deleteEntry 中用到的 successor 就是用来返回要删除的结点的树的中序后继的，也就是该结点右子最小者。
```java
    /**
     * Returns the successor of the specified Entry, or null if no such.
     */
    static <K,V> TreeMap.Entry<K,V> successor(Entry<K,V> t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            Entry<K,V> p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            Entry<K,V> p = t.parent;
            Entry<K,V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }
```

9. 修复方法

每次插入或者删除后，需要 fix，让二叉查找树保持红黑树的特征，fix 包括变色旋转，挺复杂的，需要考虑很多种情况，看到一篇讲的很清晰的 IBM 的博客
[通过分析 JDK 源代码研究 TreeMap 红黑树算法实现](https://www.ibm.com/developerworks/cn/java/j-lo-tree/)

##### 1.4 ConcurrentHashMap

#### 2. Set
![image](http://www.codejava.net/images/articles/javacore/collections/set/Simple_Set_API_class_diagram.png)

##### 2.1 HashSet

1. 简介

对 Set 接口的实现，借助了 HashMap。

2. 定义

```java
public class HashSet<E>
    extends AbstractSet<E>
    implements Set<E>, Cloneable, java.io.Serializable
```

3. 构造函数

从构造函数就可以看出来，HashSet 是借助 HashMap 实现的。
```java
    // 借助初始容量为 16，负载因子为 0.75 的 HashMap 创建一个 set
    public HashSet() {
        map = new HashMap<>();
    }

    // 用指定集合的元素创建一个 set
    // 这里的 HashMap 的初始容量可以确保足够，负载因子还是默认的
    public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }

    public HashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    public HashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    // 这个构造函数是提供给继承于 HashSet 的 LinkedHashSet 使用的
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }
```

4. HashSet 和 HashMap

因为 HashSet 是借助 HashMap 实现的，肯定和 HashMap 有很多相似之处，不同的地方只在于 HashSet 只能存储相同类型的对象，而 HashMap 可以存储键值对。

首先，因为 HashMap 不保证顺序，所以 HashSet 也不能保证顺序，因此这两个容器都不能存储重复的元素。为什么不保证顺序就不能存储重复的元素呢？这个很好理解，想象有一对双胞胎，如果没有出生先后顺序，我们是无法分辨他们的。

HashSet 其实是利用 HashMap 中 Key 的唯一性，来保证 HashSet 中不出现重复值，它的元素实际上是作为 HashMap 中的 Key 存放在 HashMap 中的。
```java
// 对抽象类 AbstractCollection 中 add 方法的实现

    private static final Object PRESENT = new Object();

    // 将新值作为 HashMap 的 key 存入，这里的 PRESENT 是一个与新值关联的虚拟值
    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
```
而 HashMap 在 put 时需要使用到 key.hashCode() 和 key.equals() 来判断 key 的唯一性，详见 [Java 容器学习之 HashMap](https://shelbylee.github.io/post/2018/05/30/)

所以，敲黑板了！

被放到 HashSet 中的元素对应的类，必须 override hashCode() 和 equals() 方法！这样才能保证 HashSet 能正常使用（即存放的对象没有重复）。

##### 2.2 LinkedHashSet

LinkedHashSet 继承了 HashSet，和 HashSet 不同的地方在于 LinkedHashSet 维护了双链表，因此它可以保证插入的元素的顺序，不过像 s.contains(e) 返回 true 后立即调用 s.add(e) 的情况，e 之前的顺序不会保持，会变成新插入的顺序。
```java
public class LinkedHashSet<E>
    extends HashSet<E>
    implements Set<E>, Cloneable, java.io.Serializable
```

LinkedHashSet 其实也是借助 LinkedHashMap 实现的。
```java
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }
```

##### 2.3 TreeSet

基于 TreeMap 的对 Set 接口的实现。

无参的构造函数。从构造函数就可以看出来，TreeSet 借助 TreeMap 实现
```java
    public TreeSet() {
        this(new TreeMap<E,Object>());
    }
```

#### 3. List
![image](http://www.codejava.net/images/articles/javacore/collections/list/List%20Collections%20class%20diagram.png)

图源： http://www.codejava.net

TODO: List 的使用

##### 3.1 ArrayList

1. 简介：

ArrayList 就是 Java 对线性表的顺序存储结构的实现，相当于一个 "可增长" 数组，适合随机存取，不是线程安全的。

2. 定义
```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable
```

3. 重要属性
```java
    
    // 默认容量
    private static final int DEFAULT_CAPACITY = 10;

    // 数组可分配的最大容量
    // 有一些虚拟机在数组中保留一些对象头
    // 如果尝试分配超过最大容量的数组可能会导致 OutOfMemoryError
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    // 这个属性很重要，它是在 AbstractList 这个抽象类中定义的
    // 表示的是 List 被 "structurally modified" 的次数
    // "structurally modified" 指的是对正在被迭代的集合进行结构上的改变
    // 如add、remove、clear，那么迭代器就不再合法，
    // 会抛出 ConcurrentModificationException 异常
    protected transient int modCount = 0;

    // 这两个都是共享空数组
    // 区别在于第二个知道在第一个元素加入时如何扩张，在 add 方法中可以看到
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    // 存放元素的数组缓冲区，ArrayList 的容量就是这个缓冲区的大小。
    // 任何空的 ArrayList 在第一个元素加进来时都会被扩容为 DEFAULT_CAPACITY 
    transient Object[] elementData; // non-private to simplify nested class access
```

4. 构造函数

ArrayList 提供了三种创建对象的方式
```java
    第一种：使用默认构造函数创建 ArrayList，默认容量为 10 
    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    第二种：用某个集合的元素来创建 ArrayList，顺序是按照该集合的迭代器返回的顺序
    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayList(Collection<? extends E> c)

    第三种：创建一个自定义容量的 ArrayList
    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity
     *         is negative
     */
    public ArrayList(int initialCapacity)
```

5. 扩容机制

从 add 方法作为入口，add 有两个重载的方法，一个直接插入元素，一个插入到指定位置。先来看直接插入元素的方法。
```java
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
```

先 size + 1，然后进入 ensureCapacityInternal 确保内部容量足够，minCapacity 是最低容量的要求，不能比这个再小。

首先判断 elementData 是否是空的。如果是空的，也就是说现在 add 的是第一个元素，那么 ArrayList 会先扩容为默认容量 10，再进入 ensureExplicitCapacity 方法；如果非空，比如 add 的是第二个元素，那么此时 minCapacity = 2，直接进入 ensureExplicitCapacity。
```java
    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }
```

进入 ensureExplicitCapacity 确保显示的容量，注意这里 modCount 加一，因为属于 "structurally modified"，然后判断要求最低的容量是否比实际元素的长度大，假如我们现在 add 的是第一个元素，则 minCapacity = 10，elementData 此时还是空的，所以进入到 grow 进行扩容；否则的话，假设正在 add 第二个元素，则 elementData.length = 10，而 minCapacity = 2，不会进行扩容，也就是说容量还是 10，同样地，第三个、四个...元素都是这样。
```java
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
```

来看扩容的具体方法，oldCapacity 是目前元素缓冲区的长度，<code>newCapacity = oldCapacity + (oldCapacity >> 1);</code>是将容量变成了 oldCapacity 的 3 / 2，也就是 1.5 倍，通过位运算的方式比之前版本的 <code>int newCapacity = (oldCapacity * 3)/2 + 1;</code> 显然要快。

如果扩容后的 newCapacity 还不够，就直接取 minCapacity 为 newCapacity，再检查一下当前 newCapacity 是不是超过了最大数组大小的限制，如果超过了，先判断一下 minCapacity 是不是溢出整数范围，如果没溢出，再看它是不是超过最大数组大小，如果超过了，就取整数最大值为扩容后的容量，如果没超过就取最大数组大小为扩容后的容量。

最后复制新容量大小个元素到 elementData 中，这实际上是将原数组引用 elementData 指向一个新的数组，就起到了扩容的作用，因为 Java 中数组的大小是不能改变的，所以所谓的扩容就是改变数组的引用而已，这样做会消耗一定的资源，因为原来的数组引用无效，需要进行 GC。

所以，在使用 ArrayList 时要尽量避免让它自动 "扩容"，最好创建时就指定合适的容量~
```java
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
```

6. fail-fast 机制

如果在进行序列化 or 迭代过程中，modCount != expectedModCount，就会抛出 ConcurrentModificationException 异常，从而 fail fast。

序列化：
```java
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException{

            ......

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
```
迭代中的 remove：
```java
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                AbstractList.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
```

7. 关于 rangeCheck

ArrayList 有两个 check 方法，一个是 rangeCheckForAdd，是在 add()、addAll() 中用到，另一个是 rangeCheck()，在 get()、set()、remove() 中用到。
```java
    // get、set、remove
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    // add、addAll
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```
在添加元素到指定位置时需要 check index > size || index < 0，这是很自然的。在 get()、set()、remove() 中调用 rangeCheck()，也是应该的，不过有点奇怪的是 check 时只判断了 index >= size，并没有对负值进行 check。

进到 get() 里面看看，会发现在 rangeCheck() 后会通过 elementData 数组访问下标为 index 的元素
```java
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    E elementData(int index) {
        return (E) elementData[index];
    }
```

同样地，在 set()，remove() 中也是
```java
    public E set(int index, E element) {
        rangeCheck(index);

        E oldValue = elementData(index);

        ......
    }

    public E remove(int index) {
        rangeCheck(index);

        modCount++;
        E oldValue = elementData(index);

        ......
    }
```
因而在 index < 0 时访问数组会抛出 ArrayIndexOutOfBoundsException 异常，相当于通过 elementData 数组进行越界检测，这样做也算是避免了重复逻辑吧。

既然看到了 range check，就考虑一下 ArrayList 的越界问题，我认为在线程安全的前提下是不会发生越界的，如果是在多线程情况下，并且没有同步操作，显然对数据的操作会有问题，是可能发生越界的。

##### 3.2 LinkedList
1. 简介

是对线性表链式存储结构的实现，相当于一个双链表，也可以被当作栈、队列、双向队列来使用。插入删除操作较为方便，非线程安全。

2. 定义

```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```

3. 重要属性
```java
    // 首指针
    transient Node<E> first;

    // 尾指针
    transient Node<E> last;
```

4. 构造函数

和 ArrayList 类似，只不过 LinkedList 不需要容量这个概念
```java
    public LinkedList() {
    }

    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
```

5. Node 静态内部类

是双链表结点对应的数据结构，包含属性：当前结点包含的内容，上一个结点，下一个结点
```java
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

6. 双链表的操作

顺便复习一下数据结构好了 _(:з」∠)_

- 直接插入
```java
    /**
     * Links e as last element.
     */
    void linkLast(E e) {
        // 定义一个结点，这个结点的对象引用指向尾结点
        final Node<E> l = last;
        // 创建一个新的尾结点
        final Node<E> newNode = new Node<>(l, e, null);
        // 让尾指针指向新的尾结点
        last = newNode;
        // 如果当前链表为空，则插入的结点变成头结点，同时改变首指针指向
        if (l == null)
            first = newNode;
        else
        // 插入新的尾结点
            l.next = newNode;
        size++;
        modCount++;
    }
```

- 插入到指定位置
```java
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    // 返回指定位置的非空结点
    Node<E> node(int index) {
        // assert isElementIndex(index);

        // 可以看到这个方法是比较高效的
        // 作者是先判断一下 index 的大小，然后根据判断决定从前至后遍历还是从后至前
        // 充分利用了双链表的特性
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    // 在非空结点 succ 前插入元素 e
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }
```

- 删除头结点
```java
    /**
     * Unlinks non-null first node f.
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        // 如果该链表只有这一个结点
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }
```

7. ArrayList 和 LinkedList 的区别

ArrayList 是线性表的顺序存储结构的实现，因此便于随机查找，但添加删除较慢（除非是在尾部），因为需要移动大量元素。LinkedList 是线性表的链式存储结构的实现，便于添加删除，但是随机查找较慢（除非是在链表首尾进行操作），因为没有索引值。

因此，如果需要频繁存取元素，最好使用 ArrayList，如果需要频繁添加删除，最好使用 LinkedList。

##### 3.3 Vector

- Vector 和 ArrayList 类似，只不过 Vector 是线程安全的，但是开销较大，因为全是靠 synchronized 保证线程安全的。
- 再有就是 Vector 扩容是扩成两倍 <code>int newCapacity = oldCapacity + ((capacityIncrement > 0) ? capacityIncrement : oldCapacity);</code>，而 ArrayList 是扩成 1.5 倍。
- 一般需要保证线程安全时不会使用 Vector，而是使用 Collections.synchronizedList() 获取线程安全的 ArrayList，或者是用 concurrent 包里提供的 CopyOnWriteArrayList。


#### 4. Queue
##### 4.1 LinkedList
##### 4.2 PriorityQueue